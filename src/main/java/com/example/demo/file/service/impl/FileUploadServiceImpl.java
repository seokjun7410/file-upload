package com.example.demo.file.service.impl;

import com.example.demo.file.domain.ExecutableMimeCatalog;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.service.ExtensionPolicyService;
import com.example.demo.file.service.FileExtensionExtractor;
import com.example.demo.file.service.FileStorage;
import com.example.demo.file.service.MimeTypeDetector;
import com.example.demo.file.service.UploadedFile;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일의 확장자를 추출해 정책을 판정하고 허용된 파일만 저장한다. */
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    private final ExtensionPolicyService extensionPolicyService;
    private final FileStorage fileStorage;
    private final FileExtensionExtractor extensionExtractor;
    private final MimeTypeDetector mimeTypeDetector;

    /** 파일 입력을 검증하고 차단 정책을 확인한 뒤 허용된 파일을 저장한다. */
    @Override
    public UploadedFile upload(MultipartFile file) {
        ExtensionName extension = extensionExtractor.extract(file);
        var mimeDetection = mimeTypeDetector.detect(file);
        if (mimeDetection.isDetected()
                && ExecutableMimeCatalog.isBlocked(mimeDetection.mimeType())) {
            throw new ExecutableMimeTypeException();
        }
        if (mimeDetection.isUnknown()) {
            log.warn(
                    "업로드 MIME을 확인할 수 없어 확장자 정책으로 계속 처리합니다. extension={}, detectedMime={}, status={}",
                    extension.value(),
                    mimeDetection.mimeType(),
                    mimeDetection.status()
            );
        }
        if (extensionPolicyService.isBlocked(extension)) {
            throw new BlockedExtensionException(extension);
        }
        return new UploadedFile(fileStorage.store(file, extension));
    }

}
