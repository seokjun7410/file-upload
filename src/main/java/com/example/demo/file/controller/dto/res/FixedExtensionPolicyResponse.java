package com.example.demo.file.controller.dto.res;

import com.example.demo.file.domain.entity.ExtensionPolicy;

/** 고정 확장자 하나의 차단 상태를 API 응답으로 표현한다. */
public record FixedExtensionPolicyResponse(String extension, boolean blocked) {

    /** 도메인 정책의 확장자와 차단 상태를 고정 정책 응답으로 변환한다. */
    public static FixedExtensionPolicyResponse from(ExtensionPolicy policy) {
        return new FixedExtensionPolicyResponse(policy.getExtension().value(), policy.isBlocked());
    }
}
