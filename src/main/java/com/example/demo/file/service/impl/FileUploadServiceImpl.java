package com.example.demo.file.service.impl;

import com.example.demo.file.domain.ExecutableMimeCatalog;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.domain.entity.vo.OriginalFilename;
import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.IdempotencyInProgressException;
import com.example.demo.file.service.ExtensionPolicyService;
import com.example.demo.file.service.FileExtensionExtractor;
import com.example.demo.file.service.FileStorage;
import com.example.demo.file.service.MimeTypeDetector;
import com.example.demo.file.service.FileUploadService;
import com.example.demo.file.service.UploadedFile;
import com.example.demo.file.service.UploadReservation;
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
    private final UploadFileStateService uploadFileStateService;
    private final RetryAfterCalculator retryAfterCalculator;

    /** 파일 입력을 검증하고 차단 정책을 확인한 뒤 허용된 파일을 저장한다. */
    @Override
    public UploadedFile upload(String requestId, MultipartFile file) {
        UploadReservation existing = uploadFileStateService.findExisting(requestId);
        if (existing != null) {
            log.info("업로드 기존 상태 재사용 requestId={} status={}", requestId, existing.uploadFile().getStatus());
            return reuseExistingResult(requestId, existing);
        }

        OriginalFilename originalFilename = OriginalFilename.from(file.getOriginalFilename());
        ExtensionName extension = extensionExtractor.extract(file);
        enforceExecutableMimePolicy(file, extension);

        if (extensionPolicyService.isBlocked(extension)) {
            throw new BlockedExtensionException(extension);
        }

        String storedFilename = fileStorage.generateFilename(extension);
        UploadReservation reservation = reserve(requestId, originalFilename, storedFilename);
        if (!reservation.newlyCreated()) {
            return reuseExistingResult(requestId, reservation);
        }

        try {
            fileStorage.storeTemporary(file, storedFilename);
            fileStorage.finalizeFile(storedFilename);
            uploadFileStateService.complete(requestId);
            log.info("업로드 완료 requestId={} status=COMPLETED", requestId);
            return new UploadedFile(requestId, storedFilename);
        } catch (FileUploadFailedException exception) {
            fileStorage.deleteTemporary(storedFilename);
            fileStorage.deleteFinal(storedFilename);
            uploadFileStateService.fail(requestId, "FILE_UPLOAD_FAILED", 500);
            log.warn("업로드 실패 requestId={} code=FILE_UPLOAD_FAILED status=500", requestId);
            throw exception;
        } catch (RuntimeException exception) {
            fileStorage.deleteTemporary(storedFilename);
            fileStorage.deleteFinal(storedFilename);
            uploadFileStateService.fail(requestId, "FILE_UPLOAD_FAILED", 500);
            log.warn("업로드 실패 requestId={} code=FILE_UPLOAD_FAILED status=500", requestId);
            throw new FileUploadFailedException("파일을 저장하지 못했습니다.", exception);
        }
    }

    /** 동일 requestId 선점 경쟁에서 기존 상태를 다시 조회한다. */
    private UploadReservation reserve(
            String requestId,
            OriginalFilename originalFilename,
            String storedFilename
    ) {
        try {
            return uploadFileStateService.reserve(requestId, originalFilename, storedFilename);
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            return uploadFileStateService.reserve(requestId, originalFilename, storedFilename);
        }
    }

    /** 기존 업로드 상태에 따라 성공 결과 재사용 또는 처리 중·실패 응답을 만든다. */
    private UploadedFile reuseExistingResult(String requestId, UploadReservation reservation) {
        UploadStatus status = reservation.uploadFile().getStatus();
        if (status == UploadStatus.COMPLETED) {
            return new UploadedFile(requestId, reservation.uploadFile().getStoredFilename());
        }
        if (status == UploadStatus.RECEIVING) {
            int count = reservation.uploadFile().getRetryObservationCount();
            int retryAfter = retryAfterCalculator.calculate(count);
            log.info("업로드 처리 중 requestId={} retryAfter={} status=RECEIVING", requestId, retryAfter);
            throw new IdempotencyInProgressException(requestId, retryAfter);
        }
        throw new FileUploadFailedException("파일을 저장하지 못했습니다.", null);
    }

    private void enforceExecutableMimePolicy(MultipartFile file, ExtensionName extension) {
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
    }

}
