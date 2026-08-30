package com.example.demo.file.repository;

import com.example.demo.file.domain.entity.ExtensionPolicyQuota;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/** 확장자 정책 quota를 조회하고 등록 한도 검증을 위한 비관적 잠금을 제공한다. */
public interface ExtensionPolicyQuotaRepository extends JpaRepository<ExtensionPolicyQuota, Long> {

    /** quota 키로 등록 한도 행을 조회한다. */
    Optional<ExtensionPolicyQuota> findByQuotaKey(String quotaKey);

    /** quota 키의 행을 쓰기 잠금으로 조회해 여러 인스턴스의 등록을 직렬화한다. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select quota from ExtensionPolicyQuota quota where quota.quotaKey = :quotaKey")
    Optional<ExtensionPolicyQuota> findForUpdateByQuotaKey(String quotaKey);
}
