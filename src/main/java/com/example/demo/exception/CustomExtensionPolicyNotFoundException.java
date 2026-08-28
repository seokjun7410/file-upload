package com.example.demo.exception;

/** 등록된 커스텀 정책이 아닌 확장자를 요청할 때 발생한다. */
public final class CustomExtensionPolicyNotFoundException extends RuntimeException {

    /** 커스텀 정책 미존재를 설명하는 예외를 생성한다. */
    public CustomExtensionPolicyNotFoundException(String message) {
        super(message);
    }
}
