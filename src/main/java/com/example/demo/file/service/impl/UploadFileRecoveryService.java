package com.example.demo.file.service.impl;

import com.example.demo.file.domain.entity.UploadFile;
import com.example.demo.file.service.FileStorage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/** 오래 처리되지 않은 업로드 상태와 임시 파일을 복구한다. */
@Service
@RequiredArgsConstructor
public class UploadFileRecoveryService {

    private final UploadFileStateService uploadFileStateService;
    private final FileStorage fileStorage;

    @Value("${file.upload.recovery-stale-after-seconds:1800}")
    private long staleAfterSeconds;

    /** 주기적으로 lease가 만료된 수신 중 업로드를 최종 파일 또는 실패 상태로 정리한다. */
    @Scheduled(fixedDelayString = "${file.upload.recovery-interval-ms:60000}")
    public void recoverStaleUploads() {
        recoverStaleUploads(LocalDateTime.now());
    }

    /** 테스트와 운영 복구 흐름이 같은 기준 시각을 사용하도록 stale 업로드를 정리한다. */
    public int recoverStaleUploads(LocalDateTime now) {
        LocalDateTime threshold = now.minusSeconds(staleAfterSeconds);
        var staleUploads = uploadFileStateService.findStaleReceiving(threshold);
        for (UploadFile uploadFile : staleUploads) {
            if (fileStorage.finalFileExists(uploadFile.getStoredFilename())) {
                fileStorage.deleteTemporary(uploadFile.getStoredFilename());
                uploadFileStateService.complete(uploadFile.getRequestId());
            } else {
                fileStorage.deleteTemporary(uploadFile.getStoredFilename());
                uploadFileStateService.fail(
                        uploadFile.getRequestId(),
                        "FILE_UPLOAD_FAILED",
                        500
                );
            }
        }
        return staleUploads.size();
    }
}
