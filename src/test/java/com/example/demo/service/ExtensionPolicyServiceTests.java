package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.ExtensionPolicyRepository;
import com.example.demo.domain.ExtensionPolicyQuota;
import com.example.demo.domain.ExtensionPolicyQuotaRepository;
import com.example.demo.domain.PolicyType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    @Test
    @DisplayName("커스텀 확장자를 정규화해 등록한다")
    void registersNormalizedCustomExtension() {
        // given

        // when
        var policy = service.registerCustom(" Sh ");

        // then
        assertThat(policy.getExtension()).isEqualTo("sh");
        assertThat(repository.countByPolicyType(PolicyType.CUSTOM)).isEqualTo(1);
    }

    @Test
    @DisplayName("이미 존재하는 정규화 확장자는 커스텀 정책으로 다시 등록할 수 없다")
    void rejectsDuplicateCustomExtension() {
        // given
        service.registerCustom("sh");

        // when

        // then
        assertThatThrownBy(() -> service.registerCustom(" SH "))
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
            service.registerCustom("custom" + index);
        }

        // when

        // then
        assertThatThrownBy(() -> service.registerCustom("overflow"))
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
            service.registerCustom("parallel" + requestIndex);
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
