package com.example.demo.file.exception;

/** 확장자 정책 API의 입력 확장자가 계약된 형식에 맞지 않을 때 발생한다. */
public final class InvalidExtensionException extends IllegalArgumentException {

    /** 잘못된 확장자 입력을 설명하는 예외를 생성한다. */
    public InvalidExtensionException(String message) {
        super(message);
    }
}
