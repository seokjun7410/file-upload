package com.example.demo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/** API 요청 실패 원인과 클라이언트가 해석할 오류 코드를 표현한다. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String requestId,
        Map<String, Object> context,
        String message
) {

    /** 기존 정책 API가 사용하는 오류 응답을 생성한다. */
    public ErrorResponse(String code, String message) {
        this(code, null, null, message);
    }
}
