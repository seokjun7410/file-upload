package com.example.demo.file.exception;

/** 누락·빈 파일 또는 확장자가 없는 업로드 요청을 거부할 때 발생한다. */
public final class InvalidFileException extends RuntimeException {

    /** 잘못된 파일 입력을 설명하는 예외를 생성한다. */
    public InvalidFileException(String message) {
        super(message);
    }
}
