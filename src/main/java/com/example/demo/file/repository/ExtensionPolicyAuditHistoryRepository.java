package com.example.demo.file.repository;

import com.example.demo.file.domain.entity.ExtensionPolicyAuditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/** 확장자 정책 변경 감사 이력을 저장한다. */
public interface ExtensionPolicyAuditHistoryRepository
        extends JpaRepository<ExtensionPolicyAuditHistory, Long> {
}
