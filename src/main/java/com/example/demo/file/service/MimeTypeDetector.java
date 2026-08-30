package com.example.demo.file.service;

import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일의 바이트에서 콘텐츠 MIME을 감지하는 포트다. */
public interface MimeTypeDetector {

    /** 파일명과 multipart 헤더를 신뢰하지 않고 콘텐츠 MIME 감지 결과를 반환한다. */
    MimeTypeDetectionResult detect(MultipartFile file);
}
