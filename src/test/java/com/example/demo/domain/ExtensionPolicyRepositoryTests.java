package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("fixed와 custom 정책을 하나의 테이블에 저장하고 유형과 상태를 조회할 수 있다")
    void savesAndLoadsPoliciesFromOneTable() {
        // given
        ExtensionPolicy fixed = repository.save(ExtensionPolicy.fixed("exe"));
        ExtensionPolicy custom = repository.save(ExtensionPolicy.custom("sh"));

        // when
        repository.flush();
        var policies = repository.findAllByOrderByIdAsc();

        // then
        assertThat(policies).hasSize(2);
        assertThat(repository.findByExtension("exe").orElseThrow().getPolicyType())
                .isEqualTo(PolicyType.FIXED);
        assertThat(repository.findByExtension("sh").orElseThrow().getPolicyType())
                .isEqualTo(PolicyType.CUSTOM);
        assertThat(fixed.getId()).isNotNull();
        assertThat(custom.getId()).isNotNull();
    }

    @Test
    @DisplayName("하나의 정규화 확장자에는 하나의 정책만 저장할 수 있다")
    void rejectsDuplicateNormalizedExtension() {
        // given
        repository.saveAndFlush(ExtensionPolicy.fixed(" EXE "));

        // when

        // then
        assertThatThrownBy(
                        () -> repository.saveAndFlush(ExtensionPolicy.fixed("exe"))
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
