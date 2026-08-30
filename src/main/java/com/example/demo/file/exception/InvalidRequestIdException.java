package com.example.demo.file.exception;

/** 업로드 멱등성 키가 없거나 UUID v4 형식이 아닐 때 발생한다. */
public class InvalidRequestIdException extends RuntimeException {

    public InvalidRequestIdException(String message) {
        super(message);
    }
}
