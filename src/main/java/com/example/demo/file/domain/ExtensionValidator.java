package com.example.demo.file.domain;

import com.example.demo.file.domain.entity.vo.ExtensionName;

/** 정책 저장과 API 경로·요청의 확장자 형식 및 정책 불변식을 검증한다. */
public final class ExtensionValidator {

    private static final int MAX_LENGTH = 20;

    private ExtensionValidator() {
    }

    /** 정규화된 확장자의 형식을 검증하고 잘못된 입력은 예외로 거부한다. */
    public static void validateExtension(String extension) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("extension must not be blank");
        }

        if (extension.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("extension must be 20 characters or fewer");
        }
        if (extension.contains(".")) {
            throw new IllegalArgumentException("extension must not contain a dot");
        }
    }

    /** 정책 유형이 존재하는지 검증하고, 검증된 유형을 반환한다. */
    public static PolicyType requirePolicyType(PolicyType policyType) {
        if (policyType == null) {
            throw new IllegalArgumentException("policyType must not be null");
        }
        return policyType;
    }

    /** 확장자와 정책 유형이 고정·커스텀 정책 규칙에 맞는지 검증한다. */
    public static void validatePolicy(ExtensionName extension, PolicyType policyType, boolean blocked) {
        boolean fixedExtension = FixedExtensionCatalog.contains(extension);
        if (policyType == PolicyType.FIXED && !fixedExtension) {
            throw new IllegalArgumentException("fixed policy must use a catalog extension");
        }
        if (policyType == PolicyType.CUSTOM && fixedExtension) {
            throw new IllegalArgumentException("custom policy cannot use a fixed extension");
        }
        if (policyType == PolicyType.CUSTOM && !blocked) {
            throw new IllegalArgumentException("custom policy must be blocked");
        }
    }
}
