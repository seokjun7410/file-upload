package com.example.demo.file.exception;

import com.example.demo.file.domain.entity.vo.ExtensionName;

/** 저장된 정책에 의해 업로드 확장자가 차단되었을 때 발생한다. */
public final class BlockedExtensionException extends RuntimeException {

    private final String extension;

    /** 차단된 확장자를 포함한 업로드 거부 예외를 생성한다. */
    public BlockedExtensionException(ExtensionName extension) {
        super("차단된 확장자(" + extension.value() + ")는 업로드할 수 없습니다.");
        this.extension = extension.value();
    }

    /** 외부 오류 context에 사용할 검증된 확장자다. */
    public String extension() {
        return extension;
    }
}
