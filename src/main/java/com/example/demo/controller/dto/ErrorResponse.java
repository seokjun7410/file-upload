package com.example.demo.controller.dto;

/** API 요청 실패 원인과 클라이언트가 해석할 오류 코드를 표현한다. */
public record ErrorResponse(String code, String message) {
}
