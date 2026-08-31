package com.example.demo.file.exception;

/** 콘텐츠 MIME 분석 자체를 완료하지 못했을 때 발생한다. */
public final class FileTypeDetectionFailedException extends RuntimeException {

    /** MIME 분석 실패 예외를 생성한다. */
    public FileTypeDetectionFailedException() {
        super("파일 형식을 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
