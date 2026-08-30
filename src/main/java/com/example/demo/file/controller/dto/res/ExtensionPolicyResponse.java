package com.example.demo.file.controller.dto.res;

import com.example.demo.file.domain.entity.ExtensionPolicy;
import java.util.List;

/** 고정 확장자 정책과 커스텀 차단 확장자 목록을 함께 표현한다. */
public record ExtensionPolicyResponse(
        List<FixedExtensionPolicyResponse> fixed,
        List<String> custom
) {

    /** 도메인 정책 목록을 fixed 객체 목록과 custom 문자열 목록으로 변환한다. */
    public static ExtensionPolicyResponse from(List<ExtensionPolicy> policies) {
        List<FixedExtensionPolicyResponse> fixed = policies.stream()
                .filter(ExtensionPolicy::isFixed)
                .map(FixedExtensionPolicyResponse::from)
                .toList();
        List<String> custom = policies.stream()
                .filter(ExtensionPolicy::isCustom)
                .map(CustomExtensionPolicyResponse::from)
                .map(CustomExtensionPolicyResponse::extension)
                .toList();
        return new ExtensionPolicyResponse(fixed, custom);
    }
}
