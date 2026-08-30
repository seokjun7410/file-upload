package com.example.demo.file.exception.handler;

import com.example.demo.common.ErrorResponse;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.InvalidFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/** 파일 업로드 서비스와 multipart 처리의 기능 예외를 업로드 API 응답으로 변환한다. */
@RestControllerAdvice
public class FileUploadExceptionHandler {

    /** 누락·빈 파일·확장자가 없는 파일 요청을 400 INVALID_FILE로 변환한다. */
    @ExceptionHandler({
            InvalidFileException.class,
            MissingServletRequestPartException.class,
            MultipartException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidFile(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_FILE", exception.getMessage());
    }

    /** multipart 파일 또는 전체 요청 용량 초과를 413 FILE_SIZE_EXCEEDED로 변환한다. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadSizeExceeded(
            MaxUploadSizeExceededException exception
    ) {
        return response(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "FILE_SIZE_EXCEEDED",
                "업로드 용량이 허용된 제한을 초과했습니다."
        );
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

    /** 파일 업로드 API 오류 응답을 생성한다. */
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }
}
