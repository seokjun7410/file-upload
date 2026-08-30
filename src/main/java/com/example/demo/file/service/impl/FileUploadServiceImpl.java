package com.example.demo.file.service.impl;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일의 확장자를 추출해 정책을 판정하고 허용된 파일만 저장한다. */
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final ExtensionPolicyService extensionPolicyService;
    private final FileStorage fileStorage;
    private final FileExtensionExtractor extensionExtractor;

    /** 파일 입력을 검증하고 차단 정책을 확인한 뒤 허용된 파일을 저장한다. */
    @Override
    public UploadedFile upload(MultipartFile file) {
        ExtensionName extension = extensionExtractor.extract(file);
        if (extensionPolicyService.isBlocked(extension)) {
            throw new BlockedExtensionException(extension);
        }
        return new UploadedFile(fileStorage.store(file, extension));
    }

}
