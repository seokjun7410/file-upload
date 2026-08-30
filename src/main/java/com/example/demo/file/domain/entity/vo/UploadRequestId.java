package com.example.demo.file.domain.entity.vo;

import com.example.demo.file.exception.InvalidRequestIdException;
import java.util.UUID;

/** 하나의 논리적 파일 업로드를 식별하는 UUID v4 값 객체다. */
public record UploadRequestId(String value) {

    /** 헤더 문자열을 검증된 업로드 requestId로 변환한다. */
    public static UploadRequestId from(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidRequestIdException("Idempotency-Key가 필요합니다.");
        }

        try {
            UUID uuid = UUID.fromString(value);
            if (uuid.version() != 4 || uuid.variant() != 2) {
                throw new InvalidRequestIdException("Idempotency-Key는 UUID v4 형식이어야 합니다.");
            }
            return new UploadRequestId(uuid.toString());
        } catch (IllegalArgumentException exception) {
            throw new InvalidRequestIdException("Idempotency-Key는 UUID v4 형식이어야 합니다.");
        }
    }
}
