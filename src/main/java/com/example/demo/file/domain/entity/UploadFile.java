package com.example.demo.file.domain.entity;

import com.example.demo.common.BaseEntity;
import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.domain.entity.vo.OriginalFilename;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 논리적 업로드 ID와 원본·서버 파일명, 실제 저장 상태를 보존하는 메타데이터 엔티티다. */
@Entity
@Table(
        name = "p_upload_file",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_upload_file_request_id", columnNames = "request_id"),
                @UniqueConstraint(name = "uk_upload_file_stored_filename", columnNames = "stored_filename")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadFile extends BaseEntity {

    @Column(name = "request_id", nullable = false, length = 36)
    private String requestId;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UploadStatus status;

    @Column(name = "retry_observation_count", nullable = false)
    private int retryObservationCount;

    @Column(name = "failure_code", length = 64)
    private String failureCode;

    @Column(name = "failure_status")
    private Integer failureStatus;

    private UploadFile(
            String requestId,
            OriginalFilename originalFilename,
            String storedFilename
    ) {
        this.requestId = requestId;
        this.originalFilename = originalFilename.value();
        this.storedFilename = storedFilename;
        this.status = UploadStatus.RECEIVING;
    }

    /** 파일 저장 전에 수신 중인 업로드 메타데이터를 생성한다. */
    public static UploadFile receiving(
            String requestId,
            OriginalFilename originalFilename,
            String storedFilename
    ) {
        return new UploadFile(requestId, originalFilename, storedFilename);
    }

    /** 물리 파일 확정 후 업로드를 완료 상태로 변경한다. */
    public void complete() {
        this.status = UploadStatus.COMPLETED;
        this.failureCode = null;
        this.failureStatus = null;
    }

    /** 저장 실패 또는 stale 복구가 확정된 업로드를 실패 상태로 변경한다. */
    public void fail(String failureCode, int failureStatus) {
        this.status = UploadStatus.FAILED;
        this.failureCode = failureCode;
        this.failureStatus = failureStatus;
    }

    /** 처리 중 중복 요청 관찰 횟수를 증가시킨다. */
    public void observeRetry() {
        this.retryObservationCount++;
    }
}
