package com.example.demo.controller.dto;

import jakarta.validation.constraints.NotNull;

/** 커스텀 차단 확장자 등록 요청을 표현한다. */
public record CustomExtensionPolicyRequest(@NotNull String extension) {
}
