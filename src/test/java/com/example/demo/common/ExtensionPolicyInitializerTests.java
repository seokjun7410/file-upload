package com.example.demo.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.ExtensionPolicyAuditAction;
import com.example.demo.file.domain.ExtensionPolicyAuditState;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.ExtensionPolicyQuota;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyQuotaRepository;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import com.example.demo.file.repository.ExtensionPolicyAuditHistoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:extension-policy-initializer-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExtensionPolicyInitializerTests {

    @Autowired
    private ExtensionPolicyRepository repository;

    @Autowired
    private ExtensionPolicyQuotaRepository quotaRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ExtensionPolicyInitializer initializer;

    @Autowired
    private ExtensionPolicyAuditHistoryRepository auditHistoryRepository;

    @Test
    @DisplayName("누락된 고정 확장자를 초기화하면 초기화 감사 이력을 남긴다")
    void recordsInitializedAuditHistoryForMissingFixedPolicy() {
        // given
        ExtensionName extension = ExtensionName.from("exe");

        // when
        var history = auditHistoryRepository.findAll().stream()
                .filter(item -> item.getExtension().equals(extension))
                .findFirst()
                .orElseThrow();

        // then
        assertThat(history.getAction()).isEqualTo(ExtensionPolicyAuditAction.INITIALIZED);
        assertThat(history.getBeforeState()).isNull();
        assertThat(history.getAfterState()).isEqualTo(ExtensionPolicyAuditState.UNBLOCKED);
        assertThat(history.getActor()).isEqualTo("SYSTEM");
    }

    @Test
    @DisplayName("애플리케이션 시작 시 고정 확장자 일곱 개가 하나의 정책 테이블에 준비된다")
    void initializesDefaultFixedPolicies() {
        // given

        // when
        var policies = repository.findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType.FIXED);

        // then
        assertThat(policies)
                .extracting(policy -> policy.getExtension().value())
                .containsExactly("bat", "cmd", "com", "cpl", "exe", "js", "scr");
        assertThat(policies).allMatch(ExtensionPolicy::isFixed);
        assertThat(policies).allMatch(policy -> !policy.isBlocked());
    }

    @Test
    @DisplayName("애플리케이션 시작 시 커스텀 확장자 quota가 최대 200개로 준비된다")
    void initializesCustomPolicyQuota() {
        // given

        // when
        ExtensionPolicyQuota quota = quotaRepository
                .findByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow();

        // then
        assertThat(quota.getMaxCount()).isEqualTo(ExtensionPolicyQuota.DEFAULT_CUSTOM_MAX_COUNT);
    }

    @Test
    @DisplayName("고정 확장자 초기화는 기존 차단 상태를 덮어쓰지 않는다")
    void initializerPreservesExistingBlockedState() {
        // given
        ExtensionPolicy policy = repository.findByExtension(ExtensionName.from("exe")).orElseThrow();
        policy.changeBlocked(true);
        repository.saveAndFlush(policy);
        entityManager.clear();

        // when
        initializer.run();

        // then
        assertThat(repository.findByExtension(ExtensionName.from("exe")).orElseThrow().isBlocked()).isTrue();
    }
}
