package com.example.demo.service.impl;

import com.example.demo.domain.validator.ExtensionValidator;
import com.example.demo.exception.BlockedExtensionException;
import com.example.demo.exception.InvalidFileException;
import com.example.demo.service.ExtensionPolicyService;
import com.example.demo.service.FileStorage;
import com.example.demo.service.FileUploadService;
import com.example.demo.service.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일의 확장자를 추출해 정책을 판정하고 허용된 파일만 저장한다. */
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final ExtensionPolicyService extensionPolicyService;
    private final FileStorage fileStorage;

    /** 파일 입력을 검증하고 차단 정책을 확인한 뒤 허용된 파일을 저장한다. */
    @Override
    public UploadedFile upload(MultipartFile file) {
        String extension = extractExtension(file);
        if (extensionPolicyService.isBlocked(extension)) {
            throw new BlockedExtensionException(extension);
        }
        return new UploadedFile(fileStorage.store(file, extension));
    }

    /** 원본 파일명의 마지막 확장자를 추출하고 공통 확장자 규칙으로 정규화한다. */
    private String extractExtension(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileException("업로드 파일명이 없습니다.");
        }

        String basename = originalFilename.replace('\\', '/');
        int lastSlash = basename.lastIndexOf('/');
        basename = basename.substring(lastSlash + 1);
        int lastDot = basename.lastIndexOf('.');
        if (lastDot <= 0 || lastDot == basename.length() - 1) {
            throw new InvalidFileException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }

        try {
            return ExtensionValidator.normalize(basename.substring(lastDot + 1));
        } catch (IllegalArgumentException exception) {
            throw new InvalidFileException("업로드 파일 확장자가 올바르지 않습니다.");
        }
    }
}
