package com.example.demo.file.exception;

/** 콘텐츠가 실행 가능한 MIME으로 감지되었을 때 발생한다. */
public final class ExecutableMimeTypeException extends RuntimeException {

    /** 실행 가능한 콘텐츠 차단 예외를 생성한다. */
    public ExecutableMimeTypeException() {
        super("실행 가능한 파일 형식은 업로드할 수 없습니다.");
    }
}
