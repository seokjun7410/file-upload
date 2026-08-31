package com.example.demo.file.repository;

import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.domain.entity.UploadFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/** 업로드 메타데이터와 논리적 requestId의 상태를 저장·조회한다. */
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    /** requestId에 해당하는 업로드를 조회한다. */
    Optional<UploadFile> findByRequestId(String requestId);

    /** 동일 requestId의 동시 상태 판정을 직렬화한다. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select uploadFile from UploadFile uploadFile where uploadFile.requestId = :requestId")
    Optional<UploadFile> findForUpdateByRequestId(String requestId);

    /** stale 수신 중 업로드를 정리 대상으로 조회한다. */
    List<UploadFile> findAllByStatusAndUpdatedAtBefore(UploadStatus status, LocalDateTime threshold);
}
