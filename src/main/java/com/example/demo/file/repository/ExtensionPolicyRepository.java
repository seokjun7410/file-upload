package com.example.demo.file.repository;

import com.example.demo.file.domain.PolicyType;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** fixed/custom 유형을 포함한 확장자 정책의 저장·조회 기능을 제공한다. */
public interface ExtensionPolicyRepository extends JpaRepository<ExtensionPolicy, Long> {

    /** 정규화된 확장자 하나에 대한 정책 존재 여부를 확인한다. */
    boolean existsByExtension(ExtensionName extension);

    /** 확장자명으로 단일 정책을 조회한다. */
    Optional<ExtensionPolicy> findByExtension(ExtensionName extension);

    /** 유형별 정책 개수를 조회한다. */
    long countByPolicyType(PolicyType policyType);

    /** 정책 유형에 해당하는 정책을 확장자명 오름차순으로 조회한다. */
    List<ExtensionPolicy> findAllByPolicyTypeOrderByExtension_ValueAsc(PolicyType policyType);
}
