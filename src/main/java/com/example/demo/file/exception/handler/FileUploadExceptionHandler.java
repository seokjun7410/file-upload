package com.example.demo.file.exception.handler;

import com.example.demo.common.ErrorResponse;
import com.example.demo.file.controller.FileUploadRestController;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.FileTypeDetectionFailedException;
import com.example.demo.file.exception.IdempotencyInProgressException;
import com.example.demo.file.exception.InvalidFileException;
import com.example.demo.file.exception.InvalidRequestIdException;
import com.example.demo.file.exception.MultipleFilesNotAllowedException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 파일 업로드 서비스와 multipart 처리의 기능 예외를 업로드 API 응답으로 변환한다. */
@RestControllerAdvice(assignableTypes = FileUploadRestController.class)
public class FileUploadExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(FileUploadExceptionHandler.class);
    private static final String INVALID_FILE_MESSAGE = "잘못된 파일 요청입니다.";

    /** 누락·빈 파일·확장자가 없는 파일 요청을 400 INVALID_FILE로 변환한다. */
    @ExceptionHandler({
            InvalidFileException.class,
            MissingServletRequestPartException.class,
            MultipartException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidFile(
            Exception exception,
            HttpServletRequest request
    ) {
        String requestId = requestId(request);
        if (exception instanceof MultipartException) {
            log.warn(
                    "multipart 요청 오류 requestId={} code=INVALID_FILE status=400",
                    requestId,
                    exception
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("INVALID_FILE", requestId, Map.of(), INVALID_FILE_MESSAGE));
        }
        return response(HttpStatus.BAD_REQUEST, "INVALID_FILE", requestId, Map.of(), exception.getMessage());
    }

    /** 여러 파일을 포함한 요청을 400 MULTIPLE_FILES_NOT_ALLOWED로 변환한다. */
    @ExceptionHandler(MultipleFilesNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleMultipleFilesNotAllowed(
            MultipleFilesNotAllowedException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "MULTIPLE_FILES_NOT_ALLOWED",
                requestId(request),
                Map.of(),
                exception.getMessage()
        );
    }

    /** 멱등성 키가 없는 업로드 요청을 400 INVALID_REQUEST_ID로 변환한다. */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestId(
            MissingRequestHeaderException exception
    ) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_ID",
                null,
                Map.of(),
                "Idempotency-Key가 필요합니다."
        );
    }

    /** 잘못된 UUID v4 업로드 키를 400 INVALID_REQUEST_ID로 변환한다. */
    @ExceptionHandler(InvalidRequestIdException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestId(InvalidRequestIdException exception) {
        return response(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_ID",
                null,
                Map.of(),
                exception.getMessage()
        );
    }

    /** 처리 중인 동일 requestId를 409와 재시도 대기 시간으로 변환한다. */
    @ExceptionHandler(IdempotencyInProgressException.class)
    public ResponseEntity<ErrorResponse> handleIdempotencyInProgress(
            IdempotencyInProgressException exception
    ) {
        log.info(
                "업로드 처리 중 오류 requestId={} code=IDEMPOTENCY_IN_PROGRESS status=409 retryAfter={}",
                exception.requestId(),
                exception.retryAfterSeconds()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(exception.retryAfterSeconds()))
                .body(new ErrorResponse(
                        "IDEMPOTENCY_IN_PROGRESS",
                        exception.requestId(),
                        Map.of(),
                        exception.getMessage()
                ));
    }

    /** 저장된 정책으로 차단된 확장자 업로드를 422 BLOCKED_EXTENSION으로 변환한다. */
    @ExceptionHandler(BlockedExtensionException.class)
    public ResponseEntity<ErrorResponse> handleBlockedExtension(
            BlockedExtensionException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "BLOCKED_EXTENSION",
                requestId(request),
                Map.of("extension", exception.extension()),
                exception.getMessage()
        );
    }

    /** 실행 가능한 MIME 업로드를 422 BLOCKED_EXECUTABLE_MIME으로 변환한다. */
    @ExceptionHandler(ExecutableMimeTypeException.class)
    public ResponseEntity<ErrorResponse> handleExecutableMime(
            ExecutableMimeTypeException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "BLOCKED_EXECUTABLE_MIME",
                requestId(request),
                Map.of(),
                exception.getMessage()
        );
    }

    /** MIME 콘텐츠 감지 실패를 500 FILE_TYPE_DETECTION_FAILED로 변환한다. */
    @ExceptionHandler(FileTypeDetectionFailedException.class)
    public ResponseEntity<ErrorResponse> handleFileTypeDetectionFailed(
            FileTypeDetectionFailedException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "FILE_TYPE_DETECTION_FAILED",
                requestId(request),
                Map.of(),
                exception.getMessage()
        );
    }

    /** 파일 저장 실패를 500 FILE_UPLOAD_FAILED로 변환하고 내부 오류를 노출하지 않는다. */
    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadFailed(
            FileUploadFailedException exception,
            HttpServletRequest request
    ) {
        return response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "FILE_UPLOAD_FAILED",
                requestId(request),
                Map.of(),
                exception.getMessage()
        );
    }

    /** 파일 업로드 API 오류 응답을 생성한다. */
    private ResponseEntity<ErrorResponse> response(
            HttpStatus status,
            String code,
            String requestId,
            Map<String, Object> context,
            String message
    ) {
        log.warn("업로드 오류 requestId={} code={} status={}", requestId, code, status.value());
        return ResponseEntity.status(status).body(new ErrorResponse(code, requestId, context, message));
    }

    /** 현재 업로드 요청의 검증된 requestId를 반환한다. */
    private String requestId(HttpServletRequest request) {
        try {
            return com.example.demo.file.domain.entity.vo.UploadRequestId
                    .from(request.getHeader("Idempotency-Key"))
                    .value();
        } catch (InvalidRequestIdException exception) {
            return null;
        }
    }
}
