package com.example.demo.exception;

/** 저장된 정책에 의해 업로드 확장자가 차단되었을 때 발생한다. */
public final class BlockedExtensionException extends RuntimeException {

    /** 차단된 확장자를 포함한 업로드 거부 예외를 생성한다. */
    public BlockedExtensionException(String extension) {
        super("차단된 확장자(" + extension + ")는 업로드할 수 없습니다.");
    }
}
