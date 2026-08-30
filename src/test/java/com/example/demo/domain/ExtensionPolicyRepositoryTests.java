package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import org.hibernate.exception.DataException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class ExtensionPolicyRepositoryTests {

    @Autowired
    private ExtensionPolicyRepository repository;

    @Autowired
    private EntityManager entityManager;

    /** 엔티티 저장 시 생성 시각을 기록하고 변경 시 수정 시각을 갱신하는지 검증한다. */
    @Test
    @DisplayName("정책을 저장하면 생성 시각과 수정 시각이 기록되고 변경 시 수정 시각이 갱신된다")
    void recordsCreatedAtAndUpdatesUpdatedAt() throws InterruptedException {
        // given
        ExtensionPolicy policy = ExtensionPolicy.fixed(ExtensionName.from("exe"));

        // when
        repository.saveAndFlush(policy);
        LocalDateTime createdAt = policy.getCreatedAt();
        LocalDateTime firstUpdatedAt = policy.getUpdatedAt();
        Thread.sleep(2);
        policy.changeBlocked(true);
        repository.saveAndFlush(policy);

        // then
        assertThat(createdAt).isNotNull();
        assertThat(firstUpdatedAt).isNotNull();
        assertThat(policy.getCreatedAt()).isEqualTo(createdAt);
        assertThat(policy.getUpdatedAt()).isAfter(firstUpdatedAt);
    }

    @Test
    @DisplayName("fixed와 custom 정책을 유형별 확장자 오름차순으로 조회한다")
    void savesAndLoadsPoliciesInTypeAndExtensionOrder() {
        // given
        ExtensionPolicy fixed = repository.save(ExtensionPolicy.fixed(ExtensionName.from("exe")));
        ExtensionPolicy secondFixed = repository.save(ExtensionPolicy.fixed(ExtensionName.from("js")));
        ExtensionPolicy custom = repository.save(ExtensionPolicy.custom(ExtensionName.from("sh")));
        ExtensionPolicy secondCustom = repository.save(ExtensionPolicy.custom(ExtensionName.from("php")));

        // when
        var fixedPolicies = repository.findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType.FIXED);
        var customPolicies = repository.findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType.CUSTOM);

        // then
        assertThat(fixedPolicies)
                .extracting(policy -> policy.getExtension().value())
                .containsExactly("exe", "js");
        assertThat(customPolicies)
                .extracting(policy -> policy.getExtension().value())
                .containsExactly("php", "sh");
        entityManager.flush();
        entityManager.clear();
        ExtensionPolicy loadedFixed = repository.findByExtension(ExtensionName.from("exe")).orElseThrow();
        assertThat(loadedFixed.getExtension()).isEqualTo(ExtensionName.from("exe"));
        assertThat(loadedFixed.getPolicyType())
                .isEqualTo(PolicyType.FIXED);
        assertThat(repository.findByExtension(ExtensionName.from("sh")).orElseThrow().getPolicyType())
                .isEqualTo(PolicyType.CUSTOM);
        assertThat(fixed.getId()).isNotNull();
        assertThat(secondFixed.getId()).isNotNull();
        assertThat(custom.getId()).isNotNull();
        assertThat(secondCustom.getId()).isNotNull();
    }

    @Test
    @DisplayName("하나의 정규화 확장자에는 하나의 정책만 저장할 수 있다")
    void rejectsDuplicateNormalizedExtension() {
        // given
        repository.saveAndFlush(ExtensionPolicy.fixed(ExtensionName.from(" EXE ")));

        // when

        // then
        assertThatThrownBy(
                        () -> repository.saveAndFlush(ExtensionPolicy.fixed(ExtensionName.from("exe")))
                )
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("UK_EXTENSION_POLICY_EXTENSION");
    }

    @Test
    @DisplayName("데이터베이스는 차단되지 않은 커스텀 정책 상태를 저장할 수 없다")
    void rejectsUnblockedCustomPolicyAtDatabaseBoundary() {
        // given
        String insertSql = """
                INSERT INTO p_extension_policy
                    (extension, policy_type, blocked)
                VALUES
                    ('sh', 'CUSTOM', false)
                """;

        // when

        // then
        assertThatThrownBy(() -> entityManager.createNativeQuery(insertSql).executeUpdate())
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("CK_EXTENSION_POLICY_CUSTOM_BLOCKED");
    }

    @Test
    @DisplayName("데이터베이스는 정의되지 않은 정책 유형을 저장할 수 없다")
    void rejectsUnknownPolicyTypeAtDatabaseBoundary() {
        // given
        String insertSql = """
                INSERT INTO p_extension_policy
                    (extension, policy_type, blocked)
                VALUES
                    ('unknown', 'UNKNOWN', true)
                """;

        // when

        // then
        assertThatThrownBy(() -> entityManager.createNativeQuery(insertSql).executeUpdate())
                .isInstanceOf(DataException.class)
                .hasMessageContaining("UNKNOWN");
    }

    @Test
    @DisplayName("데이터베이스는 null 확장자를 저장할 수 없다")
    void rejectsNullExtensionAtDatabaseBoundary() {
        // given
        String insertSql = """
                INSERT INTO p_extension_policy
                    (extension, policy_type, blocked)
                VALUES
                    (NULL, 'FIXED', false)
                """;

        // when

        // then
        assertThatThrownBy(() -> entityManager.createNativeQuery(insertSql).executeUpdate())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("데이터베이스는 null 정책 유형을 저장할 수 없다")
    void rejectsNullPolicyTypeAtDatabaseBoundary() {
        // given
        String insertSql = """
                INSERT INTO p_extension_policy
                    (extension, policy_type, blocked)
                VALUES
                    ('unknown', NULL, false)
                """;

        // when

        // then
        assertThatThrownBy(() -> entityManager.createNativeQuery(insertSql).executeUpdate())
                .isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DisplayName("데이터베이스는 null 차단 상태를 저장할 수 없다")
    void rejectsNullBlockedAtDatabaseBoundary() {
        // given
        String insertSql = """
                INSERT INTO p_extension_policy
                    (extension, policy_type, blocked)
                VALUES
                    ('unknown', 'FIXED', NULL)
                """;

        // when

        // then
        assertThatThrownBy(() -> entityManager.createNativeQuery(insertSql).executeUpdate())
                .isInstanceOf(ConstraintViolationException.class);
    }
}
