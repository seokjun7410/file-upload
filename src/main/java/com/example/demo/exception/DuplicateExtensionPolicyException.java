package com.example.demo.exception;

/** 이미 등록된 정규화 확장자를 다시 등록하려 할 때 발생한다. */
public final class DuplicateExtensionPolicyException extends IllegalArgumentException {

    /** 중복 확장자 등록을 설명하는 예외를 생성한다. */
    public DuplicateExtensionPolicyException(String message) {
        super(message);
    }
}
