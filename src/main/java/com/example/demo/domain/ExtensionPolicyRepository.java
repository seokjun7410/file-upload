package com.example.demo.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** fixed/custom 유형을 포함한 확장자 정책의 저장·조회 기능을 제공한다. */
public interface ExtensionPolicyRepository extends JpaRepository<ExtensionPolicy, Long> {

    /** 정규화된 확장자 하나에 대한 정책 존재 여부를 확인한다. */
    boolean existsByExtension(String extension);

    /** 확장자명으로 단일 정책을 조회한다. */
    Optional<ExtensionPolicy> findByExtension(String extension);

    /** 유형별 정책 개수를 조회한다. */
    long countByPolicyType(PolicyType policyType);

    /** 식별자 순서로 모든 정책을 조회한다. */
    List<ExtensionPolicy> findAllByOrderByIdAsc();
}
