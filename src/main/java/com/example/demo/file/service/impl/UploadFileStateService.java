package com.example.demo.file.service.impl;

import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.domain.entity.UploadFile;
import com.example.demo.file.domain.entity.vo.OriginalFilename;
import com.example.demo.file.repository.UploadFileRepository;
import com.example.demo.file.service.UploadReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** 파일 I/O와 분리된 업로드 상태 트랜잭션을 관리한다. */
@Service
@RequiredArgsConstructor
public class UploadFileStateService {

    private final UploadFileRepository repository;

    /** 이미 시작된 requestId를 잠금 조회하고 처리 중이면 중복 관찰 횟수를 증가시킨다. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UploadReservation findExisting(String requestId) {
        UploadFile existing = repository.findForUpdateByRequestId(requestId).orElse(null);
        if (existing != null && existing.getStatus() == UploadStatus.RECEIVING) {
            existing.observeRetry();
        }
        return existing == null ? null : new UploadReservation(existing, false);
    }

    /** lease가 만료된 수신 중 업로드의 메타데이터를 조회한다. */
    @Transactional(readOnly = true)
    public List<UploadFile> findStaleReceiving(LocalDateTime threshold) {
        return repository.findAllByStatusAndUpdatedAtBefore(UploadStatus.RECEIVING, threshold);
    }

    /** requestId를 선점하거나 기존 상태를 잠금 조회하고 중복 관찰 횟수를 증가시킨다. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UploadReservation reserve(
            String requestId,
            OriginalFilename originalFilename,
            String storedFilename
    ) {
        UploadFile existing = repository.findForUpdateByRequestId(requestId).orElse(null);
        if (existing != null) {
            if (existing.getStatus() == UploadStatus.RECEIVING) {
                existing.observeRetry();
            }
            return new UploadReservation(existing, false);
        }

        UploadFile created = UploadFile.receiving(requestId, originalFilename, storedFilename);
        repository.save(created);
        return new UploadReservation(created, true);
    }

    /** 물리 파일 확정 후 별도 트랜잭션에서 업로드를 완료한다. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void complete(String requestId) {
        UploadFile uploadFile = repository.findForUpdateByRequestId(requestId)
                .orElseThrow(() -> new IllegalStateException("upload metadata not found"));
        uploadFile.complete();
    }

    /** 저장 실패를 별도 트랜잭션에서 실패 상태로 기록한다. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(String requestId, String failureCode, int failureStatus) {
        repository.findForUpdateByRequestId(requestId)
                .ifPresent(uploadFile -> uploadFile.fail(failureCode, failureStatus));
    }
}
