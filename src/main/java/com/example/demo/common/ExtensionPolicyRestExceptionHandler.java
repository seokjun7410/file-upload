package com.example.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** REST API 전반에서 공통으로 발생하는 요청 형식 오류를 JSON 응답으로 변환한다. */
@RestControllerAdvice
public class ExtensionPolicyRestExceptionHandler {

    /** JSON 필드 누락·형식 오류를 400 INVALID_REQUEST로 변환한다. */
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 형식이 올바르지 않습니다.");
    }

    /** 공통 오류 응답을 생성한다. */
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }
}
