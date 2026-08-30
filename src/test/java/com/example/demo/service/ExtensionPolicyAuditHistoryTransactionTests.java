package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.file.domain.entity.ExtensionPolicyAuditHistory;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyAuditHistoryRepository;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import com.example.demo.file.service.ExtensionPolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:extension-policy-audit-transaction-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ExtensionPolicyAuditHistoryTransactionTests {

    @Autowired
    private ExtensionPolicyService service;

    @Autowired
    private ExtensionPolicyRepository policyRepository;

    @MockitoBean
    private ExtensionPolicyAuditHistoryRepository auditHistoryRepository;

    @Test
    @DisplayName("감사 이력 저장에 실패하면 커스텀 정책 등록도 롤백한다")
    void rollsBackCustomPolicyWhenAuditHistorySaveFails() {
        // given
        ExtensionName extension = ExtensionName.from("sh");
        when(auditHistoryRepository.save(any(ExtensionPolicyAuditHistory.class)))
                .thenThrow(new IllegalStateException("audit history save failed"));

        // when

        // then
        assertThatThrownBy(() -> service.registerCustom(extension))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("audit history save failed");
        assertThat(policyRepository.findByExtension(extension)).isEmpty();
    }
}
