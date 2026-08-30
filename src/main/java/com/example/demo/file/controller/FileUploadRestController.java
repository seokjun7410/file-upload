package com.example.demo.file.controller;

import com.example.demo.file.controller.dto.res.FileUploadResponse;
import com.example.demo.file.service.FileUploadService;
import com.example.demo.file.service.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일을 서버 정책으로 검증하고 저장하는 REST 요청을 처리한다. */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadRestController {

    private final FileUploadService service;

    /** 파일을 저장하고 생성된 서버 파일명을 201 응답으로 반환한다. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file
    ) {
        UploadedFile uploadedFile = service.upload(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FileUploadResponse.from(uploadedFile));
    }
}
