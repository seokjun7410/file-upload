package com.example.demo.file.domain;

import java.util.Locale;

/** 확장자 문자열의 표기만 정규화하고 유효성 판단은 수행하지 않는다. */
public final class ExtensionNormalizer {

    private ExtensionNormalizer() {
    }

    /** 확장자의 앞뒤 공백을 제거하고 소문자로 변환하며 null은 그대로 반환한다. */
    public static String normalize(String extension) {
        if (extension == null) {
            return null;
        }
        return extension.trim().toLowerCase(Locale.ROOT);
    }
}
