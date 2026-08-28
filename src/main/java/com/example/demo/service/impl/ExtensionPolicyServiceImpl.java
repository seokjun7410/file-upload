package com.example.demo.service.impl;

import com.example.demo.domain.ExtensionPolicy;
import com.example.demo.domain.ExtensionPolicyRepository;
import com.example.demo.domain.ExtensionPolicyQuota;
import com.example.demo.domain.ExtensionPolicyQuotaRepository;
import com.example.demo.domain.PolicyType;
import com.example.demo.service.ExtensionPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 저장소와 quota 잠금을 이용해 확장자 정책 규칙을 실행하는 서비스 구현체다. */
@Service
@RequiredArgsConstructor
public class ExtensionPolicyServiceImpl implements ExtensionPolicyService {

    private final ExtensionPolicyRepository repository;
    private final ExtensionPolicyQuotaRepository quotaRepository;

    /** 커스텀 정책을 정규화해 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    @Override
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
