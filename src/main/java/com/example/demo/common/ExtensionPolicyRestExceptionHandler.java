package com.example.demo.common;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/** REST API 전반에서 공통으로 발생하는 요청 형식 오류를 JSON 응답으로 변환한다. */
@RestControllerAdvice
public class ExtensionPolicyRestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExtensionPolicyRestExceptionHandler.class);

    /** JSON 필드 누락·형식 오류를 400 INVALID_REQUEST로 변환한다. */
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleInvalidRequest(Exception exception) {
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 형식이 올바르지 않습니다.");
    }

    /** multipart 파일 또는 전체 요청 용량 초과를 413 FILE_SIZE_EXCEEDED로 변환한다. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadSizeExceeded(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {
        return uploadResponse(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "FILE_SIZE_EXCEEDED",
                resolveRequestId(request),
                "업로드 용량이 허용된 제한을 초과했습니다."
        );
    }

    /** 공통 오류 응답을 생성한다. */
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String code,
            String message
    ) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message));
    }

    /** 업로드 용량 오류에만 검증된 requestId를 연결한다. */
    private ResponseEntity<ErrorResponse> uploadResponse(
            HttpStatus status,
            String code,
            String requestId,
            String message
    ) {
        log.warn("업로드 오류 requestId={} code={} status={}", requestId, code, status.value());
        return ResponseEntity.status(status)
                .body(new ErrorResponse(code, requestId, Map.of(), message));
    }

    /** 예외 처리 경계에서만 헤더를 보수적으로 검증해 오류 응답에 연결한다. */
    private String resolveRequestId(HttpServletRequest request) {
        String value = request.getHeader("Idempotency-Key");
        try {
            UUID uuid = UUID.fromString(value);
            if (uuid.version() != 4 || uuid.variant() != 2) {
                return null;
            }
            return uuid.toString();
        } catch (IllegalArgumentException | NullPointerException exception) {
            return null;
        }
    }
}
