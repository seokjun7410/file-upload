package com.example.demo.file.controller.dto.req;

import jakarta.validation.constraints.NotNull;

/** 고정 확장자의 차단 상태 변경 요청을 표현한다. */
public record ChangeFixedExtensionPolicyRequest(@NotNull Boolean blocked) {
}
