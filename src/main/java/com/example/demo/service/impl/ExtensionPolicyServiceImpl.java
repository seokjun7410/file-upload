package com.example.demo.service.impl;

import com.example.demo.domain.ExtensionPolicy;
import com.example.demo.domain.ExtensionPolicyRepository;
import com.example.demo.domain.ExtensionPolicyQuota;
import com.example.demo.domain.ExtensionPolicyQuotaRepository;
import com.example.demo.domain.FixedExtensionCatalog;
import com.example.demo.domain.PolicyType;
import com.example.demo.domain.validator.ExtensionValidator;
import com.example.demo.exception.CustomExtensionLimitExceededException;
import com.example.demo.exception.CustomExtensionPolicyNotFoundException;
import com.example.demo.exception.DuplicateExtensionPolicyException;
import com.example.demo.exception.FixedExtensionPolicyNotFoundException;
import com.example.demo.exception.InvalidExtensionException;
import com.example.demo.service.ExtensionPolicyService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 저장소와 quota 잠금을 이용해 확장자 정책 규칙을 실행하는 서비스 구현체다. */
@Service
@RequiredArgsConstructor
public class ExtensionPolicyServiceImpl implements ExtensionPolicyService {

    private final ExtensionPolicyRepository repository;
    private final ExtensionPolicyQuotaRepository quotaRepository;

    /** 저장된 fixed·custom 정책을 고정 카탈로그와 확장자 순서로 반환한다. */
    @Override
    @Transactional(readOnly = true)
    public List<ExtensionPolicy> findAll() {
        List<ExtensionPolicy> policies = repository.findAllByOrderByIdAsc();
        List<ExtensionPolicy> orderedPolicies = new ArrayList<>();
        for (String extension : FixedExtensionCatalog.defaultExtensions()) {
            policies.stream()
                    .filter(policy -> policy.getExtension().equals(extension))
                    .findFirst()
                    .ifPresentOrElse(
                            orderedPolicies::add,
                            () -> {
                                throw new IllegalStateException(
                                        "fixed extension policy is missing: " + extension
                                );
                            }
                    );
        }
        policies.stream()
                .filter(policy -> policy.getPolicyType() == PolicyType.CUSTOM)
                .sorted(Comparator.comparing(ExtensionPolicy::getExtension))
                .forEach(orderedPolicies::add);
        return orderedPolicies;
    }

    /** 커스텀 정책을 정규화해 저장하고, 중복·최대 개수 위반 시 등록을 거부한다. */
    @Override
    @Transactional
    public ExtensionPolicy registerCustom(String extension) {
        String normalizedExtension = normalizeForApi(extension);
        ExtensionPolicyQuota quota = quotaRepository
                .findForUpdateByQuotaKey(ExtensionPolicyQuota.CUSTOM_QUOTA_KEY)
                .orElseThrow(() -> new IllegalStateException("custom extension policy quota is missing"));
        if (repository.existsByExtension(normalizedExtension)) {
            throw new DuplicateExtensionPolicyException("extension policy already exists");
        }
        if (repository.countByPolicyType(PolicyType.CUSTOM) >= quota.getMaxCount()) {
            throw new CustomExtensionLimitExceededException("custom extension policy limit exceeded");
        }
        ExtensionPolicy policy = ExtensionPolicy.custom(normalizedExtension);
        return repository.saveAndFlush(policy);
    }

    /** 고정 정책의 차단 상태를 변경하고 저장된 최신 정책을 반환한다. */
    @Override
    @Transactional
    public ExtensionPolicy changeFixedBlocked(String extension, boolean blocked) {
        String normalizedExtension = normalizeForApi(extension);
        ExtensionPolicy policy = repository.findByExtension(normalizedExtension)
                .filter(foundPolicy -> foundPolicy.getPolicyType() == PolicyType.FIXED)
                .orElseThrow(() -> new FixedExtensionPolicyNotFoundException(
                        "fixed extension policy not found"
                ));
        policy.changeBlocked(blocked);
        return repository.saveAndFlush(policy);
    }

    /** 커스텀 정책을 정규화된 확장자 기준으로 물리 삭제한다. */
    @Override
    @Transactional
    public void deleteCustom(String extension) {
        String normalizedExtension = normalizeForApi(extension);
        ExtensionPolicy policy = repository.findByExtension(normalizedExtension)
                .filter(foundPolicy -> foundPolicy.getPolicyType() == PolicyType.CUSTOM)
                .orElseThrow(() -> new CustomExtensionPolicyNotFoundException(
                        "custom extension policy not found"
                ));
        repository.delete(policy);
        repository.flush();
    }

    /** API 입력의 확장자를 공통 규칙으로 정규화하고 의미가 분명한 예외로 변환한다. */
    private String normalizeForApi(String extension) {
        try {
            return ExtensionValidator.normalize(extension);
        } catch (IllegalArgumentException exception) {
            throw new InvalidExtensionException(exception.getMessage());
        }
    }
}
