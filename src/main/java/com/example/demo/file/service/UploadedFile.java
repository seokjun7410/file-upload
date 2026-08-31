package com.example.demo.file.service;

/** 업로드가 성공한 파일의 논리적 요청과 서버 저장 결과를 표현한다. */
public record UploadedFile(String requestId, String filename) {

    /** 기존 서비스 단위 호출과의 호환을 위해 requestId 없는 결과를 생성한다. */
    public UploadedFile(String filename) {
        this(null, filename);
    }
}
