package com.example.demo.file.exception;

/** 하나의 업로드 요청에 파일이 두 개 이상 포함됐음을 표현한다. */
public final class MultipleFilesNotAllowedException extends RuntimeException {

    /** 파일을 하나만 업로드하도록 안내하는 예외를 생성한다. */
    public MultipleFilesNotAllowedException() {
        super("한 번에 파일 하나만 업로드할 수 있습니다.");
    }
}
