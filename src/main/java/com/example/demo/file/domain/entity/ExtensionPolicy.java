package com.example.demo.file.domain.entity;

import com.example.demo.common.BaseEntity;
import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.ExtensionValidator;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class ExtensionPolicy extends BaseEntity {

    private static final int MAX_EXTENSION_LENGTH = 20;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "extension", nullable = false, length = MAX_EXTENSION_LENGTH)
    )
    private ExtensionName extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 10)
    private PolicyType policyType;

    @Column(nullable = false)
    private boolean blocked;

    private ExtensionPolicy(
            ExtensionName extension,
            PolicyType policyType,
            boolean blocked
    ) {
        this.extension = extension;
        this.policyType = ExtensionValidator.requirePolicyType(policyType);
        this.blocked = blocked;
        ExtensionValidator.validatePolicy(this.extension, this.policyType, this.blocked);
    }

    /** 차단 해제 상태의 고정 확장자 정책을 생성한다. */
    public static ExtensionPolicy fixed(ExtensionName extension) {
        return new ExtensionPolicy(extension, PolicyType.FIXED, false);
    }

    /** 차단 상태의 커스텀 확장자 정책을 생성한다. */
    public static ExtensionPolicy custom(ExtensionName extension) {
        return new ExtensionPolicy(extension, PolicyType.CUSTOM, true);
    }

    /** 이 정책이 고정 확장자 정책인지 반환한다. */
    public boolean isFixed() {
        return policyType == PolicyType.FIXED;
    }

    /** 이 정책이 커스텀 확장자 정책인지 반환한다. */
    public boolean isCustom() {
        return policyType == PolicyType.CUSTOM;
    }

    /** 파일 업로드 대상 확장자가 차단되어야 하는 정책인지 반환한다. */
    public boolean isBlockedForUpload() {
        return isCustom() || blocked;
    }

    /** 고정 확장자의 차단 상태를 변경하고 커스텀 정책의 상태 변경은 거부한다. */
    public void changeBlocked(boolean blocked) {
        if (!isFixed()) {
            throw new IllegalStateException("custom policy cannot change blocked state");
        }
        this.blocked = blocked;
    }
}
