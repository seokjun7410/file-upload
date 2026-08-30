package com.example.demo.file.service.impl;

import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.ExtensionPolicyQuota;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.repository.ExtensionPolicyQuotaRepository;
import com.example.demo.file.repository.ExtensionPolicyRepository;
import com.example.demo.file.exception.CustomExtensionLimitExceededException;
import com.example.demo.file.exception.DuplicateExtensionPolicyException;
import com.example.demo.common.EntityNotFoundException;
import com.example.demo.file.service.ExtensionPolicyService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 검증된 확장자 이름을 저장소와 quota 잠금에 전달해 정책 규칙을 실행하는 서비스 구현체다. */
@Service
@RequiredArgsConstructor
public class ExtensionPolicyServiceImpl implements ExtensionPolicyService {

    private final ExtensionPolicyRepository repository;
    private final ExtensionPolicyQuotaRepository quotaRepository;

    /** fixed와 custom 정책을 각각 확장자명 오름차순으로 조회해 fixed부터 반환한다. */
    @Override
    @Transactional(readOnly = true)
    public List<ExtensionPolicy> findAll() {
        List<ExtensionPolicy> policies = new ArrayList<>(
                repository.findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType.FIXED)
        );
        policies.addAll(repository.findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType.CUSTOM));
        return policies;
    }

    /** 검증된 커스텀 확장자 이름을 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    @Override
    @Transactional
    public ExtensionPolicy registerCustom(ExtensionName extension) {
        ExtensionPolicyQuota quota = quotaRepository
                .findForUpdateByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow(() -> new IllegalStateException("custom extension policy quota is missing"));
        if (repository.existsByExtension(extension)) {
            throw new DuplicateExtensionPolicyException("extension policy already exists");
        }
        if (repository.countByPolicyType(PolicyType.CUSTOM) >= quota.getMaxCount()) {
            throw new CustomExtensionLimitExceededException("custom extension policy limit exceeded");
        }
        ExtensionPolicy policy = ExtensionPolicy.custom(extension);
        return repository.save(policy);
    }

    /** 검증된 고정 확장자 이름의 차단 상태를 변경하고 저장된 최신 정책을 반환한다. */
    @Override
    @Transactional
    public ExtensionPolicy changeFixedBlocked(ExtensionName extension, boolean blocked) {
        ExtensionPolicy policy = repository.findByExtension(extension)
                .filter(ExtensionPolicy::isFixed)
                .orElseThrow(() -> new EntityNotFoundException("fixed extension policy"));
        policy.changeBlocked(blocked);
        return policy;
    }

    /** 검증된 확장자 이름에 해당하는 커스텀 정책을 물리 삭제한다. */
    @Override
    @Transactional
    public void deleteCustom(ExtensionName extension) {
        ExtensionPolicy policy = repository.findByExtension(extension)
                .filter(ExtensionPolicy::isCustom)
                .orElseThrow(() -> new EntityNotFoundException("custom extension policy"));
        repository.delete(policy);
    }

    /** 확장자 이름에 저장된 fixed·custom 차단 정책이 적용되는지 반환한다. */
    @Override
    public boolean isBlocked(ExtensionName extension) {
        return repository.findByExtension(extension)
                .map(ExtensionPolicy::isBlockedForUpload)
                .orElse(false);
    }
}
