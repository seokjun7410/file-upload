package com.example.demo.domain;

import com.example.demo.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 커스텀 확장자 등록 한도를 저장하고 동시 등록 시 잠금 대상이 되는 quota다. */
@Entity
@Table(
        name = "p_extension_policy_quota",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_extension_policy_quota_key",
                columnNames = "quota_key"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtensionPolicyQuota extends BaseEntity {

    /** 커스텀 확장자 등록 한도를 식별하는 고정 키다. */
    public static final String CUSTOM_QUOTA_KEY = "CUSTOM_EXTENSION_POLICY";

    /** 새 환경에서 사용할 기본 커스텀 확장자 최대 개수다. */
    public static final int DEFAULT_CUSTOM_MAX_COUNT = 200;

    @Column(name = "quota_key", nullable = false, length = 40)
    private String quotaKey;

    @Column(name = "max_count", nullable = false)
    private int maxCount;

    private ExtensionPolicyQuota(String quotaKey, int maxCount) {
        if (quotaKey == null || quotaKey.isBlank()) {
            throw new IllegalArgumentException("quotaKey must not be blank");
        }
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must be greater than zero");
        }
        this.quotaKey = quotaKey;
        this.maxCount = maxCount;
    }

    /** 기본 커스텀 확장자 quota를 생성한다. */
    public static ExtensionPolicyQuota customDefault() {
        return new ExtensionPolicyQuota(CUSTOM_QUOTA_KEY, DEFAULT_CUSTOM_MAX_COUNT);
    }
}
