package com.example.demo.file.exception;

/** 허용된 파일을 저장하는 과정에서 서버 오류가 발생했을 때 발생한다. */
public final class FileUploadFailedException extends RuntimeException {

    /** 저장 실패 원인과 내부 예외를 포함한 업로드 실패 예외를 생성한다. */
    public FileUploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
