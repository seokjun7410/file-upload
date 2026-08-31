package com.example.demo.file.service;

import com.example.demo.file.domain.entity.UploadFile;

/** requestId 선점 결과와 신규 업로드 여부를 표현한다. */
public record UploadReservation(UploadFile uploadFile, boolean newlyCreated) {
}
