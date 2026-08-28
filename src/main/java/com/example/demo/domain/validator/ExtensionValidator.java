package com.example.demo.domain.validator;

import java.util.Locale;

/** 정책 저장과 API 경로·요청의 확장자 형식을 하나의 규칙으로 검증하고 정규화한다. */
public final class ExtensionValidator {

    private static final int MAX_LENGTH = 20;

    private ExtensionValidator() {
    }

    /** 확장자를 trim·소문자 처리한 값으로 반환하고 잘못된 입력은 예외로 거부한다. */
    public static String normalize(String extension) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("extension must not be blank");
        }

        String normalized = extension.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("extension must be 20 characters or fewer");
        }
        if (normalized.contains(".")) {
            throw new IllegalArgumentException("extension must not contain a dot");
        }
        return normalized;
    }
}
