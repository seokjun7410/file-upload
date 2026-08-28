package com.example.demo.controller.dto;

import com.example.demo.domain.ExtensionPolicy;

/** 등록된 커스텀 확장자를 API 응답으로 표현한다. */
public record CustomExtensionPolicyResponse(String extension) {

    /** 도메인 정책의 정규화된 확장자를 커스텀 정책 응답으로 변환한다. */
    public static CustomExtensionPolicyResponse from(ExtensionPolicy policy) {
        return new CustomExtensionPolicyResponse(policy.getExtension());
    }
}
