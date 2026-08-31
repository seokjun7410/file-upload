package com.example.demo.file.domain.entity.vo;

import com.example.demo.file.exception.InvalidFileException;

/** 저장 경로와 분리해 보존할 검증된 원본 basename 값 객체다. */
public record OriginalFilename(String value) {

    private static final int MAX_CODE_POINT_COUNT = 255;

    /** 원본 파일명에서 basename을 추출하고 표시용 파일명 규칙을 검증한다. */
    public static OriginalFilename from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidFileException("원본 파일명이 비어 있습니다.");
        }

        int lastUnixSeparator = value.lastIndexOf('/');
        int lastWindowsSeparator = value.lastIndexOf('\\');
        int basenameStart = Math.max(lastUnixSeparator, lastWindowsSeparator) + 1;
        String basename = value.substring(basenameStart);

        if (basename.isBlank()
                || basename.codePoints().anyMatch(Character::isISOControl)
                || basename.codePointCount(0, basename.length()) > MAX_CODE_POINT_COUNT) {
            throw new InvalidFileException("원본 파일명이 올바르지 않습니다.");
        }
        return new OriginalFilename(basename);
    }
}
