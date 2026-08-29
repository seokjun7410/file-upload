package com.example.demo.exception;

/** 고정 정책으로 관리할 수 없는 확장자를 요청할 때 발생한다. */
public final class FixedExtensionPolicyNotFoundException extends RuntimeException {

    /** 고정 정책 미존재를 설명하는 예외를 생성한다. */
    public FixedExtensionPolicyNotFoundException(String message) {
        super(message);
    }
}
