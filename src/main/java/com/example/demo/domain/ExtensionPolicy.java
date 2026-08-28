package com.example.demo.domain;

import java.util.Locale;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

/** 정규화된 확장자 하나의 차단 정책과 유형별 상태를 관리하는 단일 도메인 엔티티다. */
@Entity
@Table(
        name = "p_extension_policy",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_extension_policy_extension",
                columnNames = "extension"
        )
)
@Check(name = "ck_extension_policy_policy_type", constraints = "policy_type IN ('FIXED', 'CUSTOM')")
@Check(name = "ck_extension_policy_custom_blocked", constraints = "policy_type = 'FIXED' OR blocked = true")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionPolicy {

    private static final int MAX_EXTENSION_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_EXTENSION_LENGTH)
    private String extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 10)
    private PolicyType policyType;

    @Column(nullable = false)
    private boolean blocked;

    private ExtensionPolicy(
            String extension,
            PolicyType policyType,
            boolean blocked
    ) {
        this.extension = normalize(extension);
        this.policyType = requirePolicyType(policyType);
        this.blocked = blocked;
        validateCatalog();
        validateState();
    }

    /** 차단 해제 상태의 고정 확장자 정책을 생성한다. */
    public static ExtensionPolicy fixed(String extension) {
        return new ExtensionPolicy(extension, PolicyType.FIXED, false);
    }

    /** 차단 상태의 커스텀 확장자 정책을 생성한다. */
    public static ExtensionPolicy custom(String extension) {
        return new ExtensionPolicy(extension, PolicyType.CUSTOM, true);
    }

    /** 고정 확장자의 차단 상태를 변경하고 커스텀 정책의 상태 변경은 거부한다. */
    public void changeBlocked(boolean blocked) {
        if (policyType != PolicyType.FIXED) {
            throw new IllegalStateException("custom policy cannot change blocked state");
        }
        this.blocked = blocked;
    }

    /** 정책 유형에 맞는 상태 조합인지 확인한다. */
    private void validateState() {
        if (policyType == PolicyType.CUSTOM && !blocked) {
            throw new IllegalArgumentException("custom policy must be blocked");
        }
    }

    /** 정책 유형과 고정 확장자 카탈로그가 서로 맞는지 확인한다. */
    private void validateCatalog() {
        boolean fixedExtension = FixedExtensionCatalog.contains(extension);
        if (policyType == PolicyType.FIXED && !fixedExtension) {
            throw new IllegalArgumentException("fixed policy must use a catalog extension");
        }
        if (policyType == PolicyType.CUSTOM && fixedExtension) {
            throw new IllegalArgumentException("custom policy cannot use a fixed extension");
        }
    }

    /** 정책 유형이 null이 아님을 확인하고 반환한다. */
    private PolicyType requirePolicyType(PolicyType policyType) {
        if (policyType == null) {
            throw new IllegalArgumentException("policyType must not be null");
        }
        return policyType;
    }

    /** 확장자를 정규화하고 정책 입력 규칙을 검증한다. */
    private String normalize(String extension) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("extension must not be blank");
        }

        String normalized = extension.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_EXTENSION_LENGTH) {
            throw new IllegalArgumentException("extension must be 20 characters or fewer");
        }
        if (normalized.contains(".")) {
            throw new IllegalArgumentException("extension must not contain a dot");
        }
        return normalized;
    }
}
