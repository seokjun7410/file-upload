package com.example.demo.common;

import com.example.demo.exception.BlockedExtensionException;
import com.example.demo.exception.CustomExtensionLimitExceededException;
import com.example.demo.exception.CustomExtensionPolicyNotFoundException;
import com.example.demo.exception.DuplicateExtensionPolicyException;
import com.example.demo.exception.FixedExtensionPolicyNotFoundException;
import com.example.demo.exception.FileUploadFailedException;
import com.example.demo.exception.InvalidExtensionException;
import com.example.demo.exception.InvalidFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    /** 누락·빈 파일·확장자가 없는 파일 요청을 400 INVALID_FILE로 변환한다. */
    @ExceptionHandler({
            InvalidFileException.class,
            MissingServletRequestPartException.class,
            MultipartException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidFile(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_FILE", exception.getMessage());
    }

    /** 저장된 정책으로 차단된 확장자 업로드를 422 BLOCKED_EXTENSION으로 변환한다. */
    @ExceptionHandler(BlockedExtensionException.class)
    public ResponseEntity<ErrorResponse> handleBlockedExtension(
            BlockedExtensionException exception
    ) {
        return response(HttpStatus.UNPROCESSABLE_ENTITY, "BLOCKED_EXTENSION", exception.getMessage());
    }

    /** 파일 저장 실패를 500 FILE_UPLOAD_FAILED로 변환하고 내부 오류를 노출하지 않는다. */
    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadFailed(
            FileUploadFailedException exception
    ) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_UPLOAD_FAILED", exception.getMessage());
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
