package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일의 확장자를 정책과 비교하고 허용된 파일의 저장을 수행한다. */
public interface FileUploadService {

    /** 파일을 검증·저장하고 실제 서버 저장 파일명을 반환한다. */
    UploadedFile upload(MultipartFile file);
}
