package com.example.demo.file.domain.entity;

import com.example.demo.common.BaseEntity;
import com.example.demo.file.domain.ExtensionPolicyAuditAction;
import com.example.demo.file.domain.ExtensionPolicyAuditState;
import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 물리 삭제된 정책까지 포함해 확장자 정책 변경 사건을 append-only로 보존한다. */
@Entity
@Table(name = "p_extension_policy_audit_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionPolicyAuditHistory extends BaseEntity {

    private static final String SYSTEM_ACTOR = "SYSTEM";

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Embedded
    @AttributeOverride(
            name = "value",
            column = @Column(name = "extension", nullable = false, length = 20)
    )
    private ExtensionName extension;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false, length = 10)
    private PolicyType policyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 20)
    private ExtensionPolicyAuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_state", length = 10)
    private ExtensionPolicyAuditState beforeState;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_state", length = 10)
    private ExtensionPolicyAuditState afterState;

    @Column(name = "actor", nullable = false, length = 30)
    private String actor;

    private ExtensionPolicyAuditHistory(
            final ExtensionPolicy policy,
            final ExtensionPolicyAuditAction action,
            final ExtensionPolicyAuditState beforeState,
            final ExtensionPolicyAuditState afterState
    ) {
        this.policyId = Objects.requireNonNull(policy, "policy must not be null").getId();
        if (this.policyId == null) {
            throw new IllegalArgumentException("policy must be persisted before recording audit history");
        }
        this.extension = policy.getExtension();
        this.policyType = policy.getPolicyType();
        this.action = Objects.requireNonNull(action, "action must not be null");
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.actor = SYSTEM_ACTOR;
    }

    /** 커스텀 정책 생성 이력을 만든다. */
    public static ExtensionPolicyAuditHistory created(final ExtensionPolicy policy) {
        return new ExtensionPolicyAuditHistory(
                policy,
                ExtensionPolicyAuditAction.CREATED,
                null,
                ExtensionPolicyAuditState.from(policy.isBlocked())
        );
    }

    /** 초기화 과정에서 고정 정책을 생성한 이력을 만든다. */
    public static ExtensionPolicyAuditHistory initialized(final ExtensionPolicy policy) {
        return new ExtensionPolicyAuditHistory(
                policy,
                ExtensionPolicyAuditAction.INITIALIZED,
                null,
                ExtensionPolicyAuditState.from(policy.isBlocked())
        );
    }

    /** 고정 정책의 차단 상태 변경 이력을 만든다. */
    public static ExtensionPolicyAuditHistory blockedChanged(
            final ExtensionPolicy policy,
            final ExtensionPolicyAuditState beforeState,
            final ExtensionPolicyAuditState afterState
    ) {
        return new ExtensionPolicyAuditHistory(
                policy,
                ExtensionPolicyAuditAction.BLOCKED_CHANGED,
                Objects.requireNonNull(beforeState, "beforeState must not be null"),
                Objects.requireNonNull(afterState, "afterState must not be null")
        );
    }

    /** 커스텀 정책 삭제 이력을 만든다. */
    public static ExtensionPolicyAuditHistory deleted(final ExtensionPolicy policy) {
        return new ExtensionPolicyAuditHistory(
                policy,
                ExtensionPolicyAuditAction.DELETED,
                ExtensionPolicyAuditState.from(policy.isBlocked()),
                null
        );
    }
}
