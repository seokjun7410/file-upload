package com.example.demo.file.controller.dto.res;

import com.example.demo.file.service.UploadedFile;

/** 파일 업로드 성공 결과를 REST 응답으로 표현한다. */
public record FileUploadResponse(String requestId, String filename, String message) {

    private static final String SUCCESS_MESSAGE = "파일 업로드가 완료되었습니다.";

    /** 서비스의 저장 결과를 업로드 성공 응답으로 변환한다. */
    public static FileUploadResponse from(UploadedFile uploadedFile) {
        return new FileUploadResponse(uploadedFile.requestId(), uploadedFile.filename(), SUCCESS_MESSAGE);
    }
}
