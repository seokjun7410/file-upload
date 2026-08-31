package com.example.demo.common;

import com.example.demo.file.domain.FixedExtensionCatalog;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.ExtensionPolicyQuota;
import com.example.demo.file.domain.entity.ExtensionPolicyAuditHistory;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyQuotaRepository;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import com.example.demo.file.repository.ExtensionPolicyAuditHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 애플리케이션 시작 시 단일 정책 테이블에 고정 확장자를 준비한다. */
@Component
@RequiredArgsConstructor
public class ExtensionPolicyInitializer implements CommandLineRunner {

    private final ExtensionPolicyRepository repository;
    private final ExtensionPolicyQuotaRepository quotaRepository;
    private final ExtensionPolicyAuditHistoryRepository auditHistoryRepository;

    /** 누락된 고정 정책만 차단 해제 상태로 저장하고 기존 상태는 보존한다. */
    @Override
    @Transactional
    public void run(String... args) {
        if (quotaRepository.findByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY).isEmpty()) {
            quotaRepository.save(ExtensionPolicyQuota.customDefault());
        }
        for (ExtensionName extension : FixedExtensionCatalog.defaultExtensions()) {
            if (!repository.existsByExtension(extension)) {
                ExtensionPolicy policy = repository.save(ExtensionPolicy.fixed(extension));
                auditHistoryRepository.save(ExtensionPolicyAuditHistory.initialized(policy));
            }
        }
    }
}
