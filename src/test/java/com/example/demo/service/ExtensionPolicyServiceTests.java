package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.ExtensionPolicyAuditAction;
import com.example.demo.file.domain.ExtensionPolicyAuditState;
import com.example.demo.file.domain.entity.ExtensionPolicyQuota;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyQuotaRepository;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import com.example.demo.file.repository.ExtensionPolicyAuditHistoryRepository;
import com.example.demo.common.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.example.demo.file.service.ExtensionPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:extension-policy-service-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExtensionPolicyServiceTests {

    @Autowired
    private ExtensionPolicyService service;

    @Autowired
    private ExtensionPolicyRepository repository;

    @Autowired
    private ExtensionPolicyQuotaRepository quotaRepository;

    @Autowired
    private ExtensionPolicyAuditHistoryRepository auditHistoryRepository;

    @Test
    @DisplayName("커스텀 확장자를 등록하면 생성 감사 이력을 남긴다")
    void recordsCreatedAuditHistoryWhenRegisteringCustomExtension() {
        // given
        ExtensionName extension = ExtensionName.from("sh");

        // when
        var policy = service.registerCustom(extension);

        // then
        assertThat(auditHistoryRepository.findAll())
                .filteredOn(history -> history.getExtension().equals(extension))
                .singleElement()
                .satisfies(history -> {
                    assertThat(history.getPolicyId()).isEqualTo(policy.getId());
                    assertThat(history.getAction()).isEqualTo(ExtensionPolicyAuditAction.CREATED);
                    assertThat(history.getBeforeState()).isNull();
                    assertThat(history.getAfterState()).isEqualTo(ExtensionPolicyAuditState.BLOCKED);
                    assertThat(history.getActor()).isEqualTo("SYSTEM");
                });
    }

    @Test
    @DisplayName("고정 확장자의 차단 상태를 변경하면 변경 전후 감사 이력을 남긴다")
    void recordsBlockedChangedAuditHistoryWhenChangingFixedPolicy() {
        // given
        ExtensionName extension = ExtensionName.from("exe");

        // when
        service.changeFixedBlocked(extension, true);

        // then
        assertThat(auditHistoryRepository.findAll())
                .filteredOn(history -> history.getExtension().equals(extension))
                .filteredOn(history -> history.getAction() == ExtensionPolicyAuditAction.BLOCKED_CHANGED)
                .singleElement()
                .satisfies(history -> {
                    assertThat(history.getBeforeState()).isEqualTo(ExtensionPolicyAuditState.UNBLOCKED);
                    assertThat(history.getAfterState()).isEqualTo(ExtensionPolicyAuditState.BLOCKED);
                    assertThat(history.getActor()).isEqualTo("SYSTEM");
                });
    }

    @Test
    @DisplayName("고정 확장자의 차단 상태가 같으면 감사 이력을 추가하지 않는다")
    void doesNotRecordAuditHistoryWhenFixedPolicyStateDoesNotChange() {
        // given
        ExtensionName extension = ExtensionName.from("exe");

        // when
        service.changeFixedBlocked(extension, false);

        // then
        assertThat(auditHistoryRepository.findAll())
                .filteredOn(history -> history.getExtension().equals(extension))
                .filteredOn(history -> history.getAction() == ExtensionPolicyAuditAction.BLOCKED_CHANGED)
                .isEmpty();
    }

    @Test
    @DisplayName("커스텀 확장자를 삭제하면 삭제 감사 이력과 정책 식별자를 보존한다")
    void preservesDeletedAuditHistoryAndPolicyIdentityWhenDeletingCustomExtension() {
        // given
        ExtensionName extension = ExtensionName.from("sh");
        var policy = service.registerCustom(extension);
        Long policyId = policy.getId();

        // when
        service.deleteCustom(extension);

        // then
        assertThat(repository.findByExtension(extension)).isEmpty();
        assertThat(auditHistoryRepository.findAll())
                .filteredOn(history -> history.getExtension().equals(extension))
                .filteredOn(history -> history.getAction() == ExtensionPolicyAuditAction.DELETED)
                .singleElement()
                .satisfies(history -> {
                    assertThat(history.getPolicyId()).isEqualTo(policyId);
                    assertThat(history.getBeforeState()).isEqualTo(ExtensionPolicyAuditState.BLOCKED);
                    assertThat(history.getAfterState()).isNull();
                    assertThat(history.getActor()).isEqualTo("SYSTEM");
                });
    }

    @Test
    @DisplayName("삭제 후 같은 커스텀 확장자를 재등록하면 정책 생애주기를 구분한다")
    void distinguishesPolicyLifecycleWhenReRegisteringDeletedCustomExtension() {
        // given
        ExtensionName extension = ExtensionName.from("sh");
        var firstPolicy = service.registerCustom(extension);
        service.deleteCustom(extension);

        // when
        var secondPolicy = service.registerCustom(extension);

        // then
        assertThat(secondPolicy.getId()).isNotEqualTo(firstPolicy.getId());
        assertThat(auditHistoryRepository.findAll().stream()
                .filter(history -> history.getExtension().equals(extension))
                .sorted(Comparator.comparing(history -> history.getCreatedAt()))
                .map(history -> history.getAction())
                .toList())
                .containsExactly(
                        ExtensionPolicyAuditAction.CREATED,
                        ExtensionPolicyAuditAction.DELETED,
                        ExtensionPolicyAuditAction.CREATED
                );
    }

    @Test
    @DisplayName("커스텀 확장자를 정규화해 등록한다")
    void registersNormalizedCustomExtension() {
        // given

        // when
        var policy = service.registerCustom(ExtensionName.from(" Sh "));

        // then
        assertThat(policy.getExtension().value()).isEqualTo("sh");
        assertThat(repository.countByPolicyType(PolicyType.CUSTOM)).isEqualTo(1);
    }

    @Test
    @DisplayName("정책 조회는 fixed와 custom을 각각 확장자명 오름차순으로 반환한다")
    void findsPoliciesInTypeAndExtensionAscendingOrder() {
        // given
        service.registerCustom(ExtensionName.from(" PHP "));
        service.registerCustom(ExtensionName.from("sh"));

        // when
        var policies = service.findAll();

        // then
        assertThat(policies)
                .extracting(policy -> policy.getExtension().value())
                .containsExactly("bat", "cmd", "com", "cpl", "exe", "js", "scr", "php", "sh");
    }

    @Test
    @DisplayName("고정 확장자 차단 상태를 정규화된 확장자 기준으로 변경하고 저장한다")
    void changesFixedPolicyBlockedState() {
        // given

        // when
        var policy = service.changeFixedBlocked(ExtensionName.from(" EXE "), true);

        // then
        assertThat(policy.isBlocked()).isTrue();
        assertThat(repository.findByExtension(ExtensionName.from("exe")).orElseThrow().isBlocked()).isTrue();
    }

    @Test
    @DisplayName("저장된 고정·커스텀 정책의 차단 상태를 판정한다")
    void determinesBlockedPolicyState() {
        // given
        service.registerCustom(ExtensionName.from("sh"));
        service.changeFixedBlocked(ExtensionName.from("exe"), true);

        // when
        boolean blockedFixed = service.isBlocked(ExtensionName.from(" EXE "));
        boolean blockedCustom = service.isBlocked(ExtensionName.from(" SH "));
        boolean allowedExtension = service.isBlocked(ExtensionName.from("txt"));

        // then
        assertThat(blockedFixed).isTrue();
        assertThat(blockedCustom).isTrue();
        assertThat(allowedExtension).isFalse();
    }

    @Test
    @DisplayName("커스텀 확장자를 물리적으로 삭제한다")
    void deletesCustomPolicyPhysically() {
        // given
        service.registerCustom(ExtensionName.from("sh"));

        // when
        service.deleteCustom(ExtensionName.from(" SH "));

        // then
        assertThat(repository.findByExtension(ExtensionName.from("sh"))).isEmpty();
    }

    @Test
    @DisplayName("고정 확장자를 커스텀 정책처럼 삭제할 수 없다")
    void rejectsDeletingFixedPolicyAsCustom() {
        // given
        int historyCountBefore = auditHistoryRepository.findAll().size();

        // when

        // then
        assertThatThrownBy(() -> service.deleteCustom(ExtensionName.from("exe")))
                .isInstanceOf(EntityNotFoundException.class);
        assertThat(auditHistoryRepository.findAll()).hasSize(historyCountBefore);
    }

    @Test
    @DisplayName("커스텀 확장자를 고정 정책처럼 변경할 수 없다")
    void rejectsChangingCustomPolicyAsFixed() {
        // given
        service.registerCustom(ExtensionName.from("sh"));

        // when

        // then
        assertThatThrownBy(() -> service.changeFixedBlocked(ExtensionName.from("sh"), true))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("이미 존재하는 정규화 확장자는 커스텀 정책으로 다시 등록할 수 없다")
    void rejectsDuplicateCustomExtension() {
        // given
        service.registerCustom(ExtensionName.from("sh"));

        // when

        // then
        assertThatThrownBy(() -> service.registerCustom(ExtensionName.from(" SH ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension policy already exists");
    }

    @Test
    @DisplayName("커스텀 정책은 200개를 초과해 등록할 수 없다")
    void rejectsCustomExtensionAfterTwoHundredPolicies() {
        // given
        int maxCount = quotaRepository
                .findByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow()
                .getMaxCount();
        for (int index = 0; index < maxCount; index++) {
            service.registerCustom(ExtensionName.from("custom" + index));
        }

        // when

        // then
        assertThatThrownBy(() -> service.registerCustom(ExtensionName.from("overflow")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("custom extension policy limit exceeded");
    }

    @Test
    @DisplayName("동시에 커스텀 확장자를 등록해도 최대 200개를 넘지 않는다")
    void doesNotExceedCustomPolicyLimitWhenRegisteringConcurrently() throws Exception {
        // given
        int maxCount = quotaRepository
                .findByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow()
                .getMaxCount();
        int requestCount = maxCount + 10;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (int index = 0; index < requestCount; index++) {
            int requestIndex = index;
            futures.add(executor.submit(() -> registerAfter(start, requestIndex)));
        }

        // when
        start.countDown();

        // then
        long successCount = futures.stream()
                .map(this::getResult)
                .filter(Boolean.TRUE::equals)
                .count();
        assertThat(successCount).isEqualTo(maxCount);
        assertThat(repository.countByPolicyType(PolicyType.CUSTOM))
                .isEqualTo(maxCount);
        executor.shutdownNow();
    }

    /** 시작 신호 후 하나의 고유한 커스텀 확장자를 등록하고 성공 여부를 반환한다. */
    private boolean registerAfter(CountDownLatch start, int requestIndex) throws InterruptedException {
        start.await();
        try {
            service.registerCustom(ExtensionName.from("parallel" + requestIndex));
            return true;
        } catch (IllegalStateException exception) {
            return false;
        }
    }

    /** 비동기 등록 결과를 테스트에서 확인할 수 있는 boolean으로 변환한다. */
    private Boolean getResult(Future<Boolean> future) {
        try {
            return future.get();
        } catch (Exception exception) {
            throw new AssertionError("concurrent registration task failed", exception);
        }
    }
}
