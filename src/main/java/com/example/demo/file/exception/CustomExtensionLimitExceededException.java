package com.example.demo.file.exception;

/** 커스텀 확장자 최대 개수를 초과해 등록하려 할 때 발생한다. */
public final class CustomExtensionLimitExceededException extends IllegalStateException {

    /** 커스텀 확장자 한도 초과를 설명하는 예외를 생성한다. */
    public CustomExtensionLimitExceededException(String message) {
        super(message);
    }
}
