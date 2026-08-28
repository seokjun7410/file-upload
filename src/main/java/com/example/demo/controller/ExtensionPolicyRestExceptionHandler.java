package com.example.demo.controller;

import com.example.demo.controller.dto.ErrorResponse;
import com.example.demo.exception.CustomExtensionLimitExceededException;
import com.example.demo.exception.CustomExtensionPolicyNotFoundException;
import com.example.demo.exception.DuplicateExtensionPolicyException;
import com.example.demo.exception.FixedExtensionPolicyNotFoundException;
import com.example.demo.exception.InvalidExtensionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 정책 REST API의 도메인·요청 오류를 공통 JSON 응답으로 변환한다. */
@RestControllerAdvice
public class ExtensionPolicyRestExceptionHandler {

    /** 잘못된 확장자 입력을 400 INVALID_EXTENSION으로 변환한다. */
    @ExceptionHandler(InvalidExtensionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidExtension(
            InvalidExtensionException exception
    ) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_EXTENSION", exception.getMessage());
    }

    /** 중복 확장자 등록을 409 DUPLICATE_EXTENSION으로 변환한다. */
    @ExceptionHandler(DuplicateExtensionPolicyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateExtension(
            DuplicateExtensionPolicyException exception
    ) {
        return response(HttpStatus.CONFLICT, "DUPLICATE_EXTENSION", exception.getMessage());
    }

    /** 커스텀 정책 최대 개수 초과를 409 CUSTOM_LIMIT_EXCEEDED로 변환한다. */
    @ExceptionHandler(CustomExtensionLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleCustomLimitExceeded(
            CustomExtensionLimitExceededException exception
    ) {
        return response(HttpStatus.CONFLICT, "CUSTOM_LIMIT_EXCEEDED", exception.getMessage());
    }

    /** 고정 정책 미존재를 404 FIXED_EXTENSION_NOT_FOUND로 변환한다. */
    @ExceptionHandler(FixedExtensionPolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFixedPolicyNotFound(
            FixedExtensionPolicyNotFoundException exception
    ) {
        return response(HttpStatus.NOT_FOUND, "FIXED_EXTENSION_NOT_FOUND", exception.getMessage());
    }

    /** 커스텀 정책 미존재를 404 CUSTOM_EXTENSION_NOT_FOUND로 변환한다. */
    @ExceptionHandler(CustomExtensionPolicyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomPolicyNotFound(
            CustomExtensionPolicyNotFoundException exception
    ) {
        return response(HttpStatus.NOT_FOUND, "CUSTOM_EXTENSION_NOT_FOUND", exception.getMessage());
    }

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
