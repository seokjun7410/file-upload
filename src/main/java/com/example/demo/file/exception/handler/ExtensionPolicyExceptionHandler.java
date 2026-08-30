package com.example.demo.file.exception.handler;

import com.example.demo.common.ErrorResponse;
import com.example.demo.file.controller.ExtensionPolicyRestController;
import com.example.demo.file.exception.CustomExtensionLimitExceededException;
import com.example.demo.file.exception.DuplicateExtensionPolicyException;
import com.example.demo.common.EntityNotFoundException;
import com.example.demo.file.exception.InvalidExtensionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 확장자 정책 서비스의 도메인 예외를 정책 API 응답으로 변환한다. */
@RestControllerAdvice(assignableTypes = ExtensionPolicyRestController.class)
public class ExtensionPolicyExceptionHandler {

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

    /** 도메인 엔티티 미존재를 404 ENTITY_NOT_FOUND로 변환한다. */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", exception.getMessage());
    }

    /** 정책 API 오류 응답을 생성한다. */
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }
}
