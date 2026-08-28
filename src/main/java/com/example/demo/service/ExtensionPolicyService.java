package com.example.demo.service;

import com.example.demo.domain.ExtensionPolicy;
import com.example.demo.domain.ExtensionPolicyRepository;
import com.example.demo.domain.ExtensionPolicyQuota;
import com.example.demo.domain.ExtensionPolicyQuotaRepository;
import com.example.demo.domain.PolicyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 커스텀 확장자 등록의 중복·고정 목록·최대 개수 정책을 조정한다. */
@Service
@RequiredArgsConstructor
public class ExtensionPolicyService {

    private final ExtensionPolicyRepository repository;
    private final ExtensionPolicyQuotaRepository quotaRepository;

    /** 커스텀 정책을 정규화해 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    @Transactional
    public ExtensionPolicy registerCustom(String extension) {
        ExtensionPolicy policy = ExtensionPolicy.custom(extension);
        ExtensionPolicyQuota quota = quotaRepository
                .findForUpdateByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow(() -> new IllegalStateException("custom extension policy quota is missing"));
        if (repository.existsByExtension(policy.getExtension())) {
            throw new IllegalArgumentException("extension policy already exists");
        }
        if (repository.countByPolicyType(PolicyType.CUSTOM) >= quota.getMaxCount()) {
            throw new IllegalStateException("custom extension policy limit exceeded");
        }
        return repository.saveAndFlush(policy);
    }
}
