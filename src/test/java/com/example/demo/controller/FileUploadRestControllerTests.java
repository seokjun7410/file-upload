package com.example.demo.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.common.ExtensionPolicyRestExceptionHandler;
import com.example.demo.file.controller.FileUploadRestController;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.FileTypeDetectionFailedException;
import com.example.demo.file.exception.InvalidFileException;
import com.example.demo.file.exception.IdempotencyInProgressException;
import com.example.demo.file.exception.handler.FileUploadExceptionHandler;
import com.example.demo.file.service.FileUploadService;
import com.example.demo.file.service.UploadedFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FileUploadRestController.class)
@Import({ExtensionPolicyRestExceptionHandler.class, FileUploadExceptionHandler.class})
class FileUploadRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileUploadService service;

    @Test
    @DisplayName("Idempotency-Key가 없으면 INVALID_REQUEST_ID 오류를 반환하고 업로드하지 않는다")
    void rejectsMissingIdempotencyKey() throws Exception {
        // given
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());

        // when
        var result = mockMvc.perform(multipart("/api/v1/files").file(file));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_ID"));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Idempotency-Key가 UUID v4가 아니면 INVALID_REQUEST_ID 오류를 반환하고 업로드하지 않는다")
    void rejectsInvalidIdempotencyKey() throws Exception {
        // given
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", "not-a-uuid"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_ID"));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("multipart 파일을 업로드하면 생성된 파일명과 완료 메시지를 반환한다")
    void uploadsMultipartFile() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440000";
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenReturn(new UploadedFile(requestId, "generated.txt"));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.filename").value("generated.txt"))
                .andExpect(jsonPath("$.message").value("파일 업로드가 완료되었습니다."));
    }

    @Test
    @DisplayName("파일 multipart가 없으면 INVALID_FILE 오류를 반환한다")
    void rejectsMissingMultipartFile() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440002";

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
    }

    @Test
    @DisplayName("확장자가 없는 파일은 INVALID_FILE 오류를 반환한다")
    void rejectsExtensionlessFile() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440003";
        var file = new MockMultipartFile("file", "README", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new InvalidFileException("확장자가 없는 파일은 업로드할 수 없습니다."));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
    }

    @Test
    @DisplayName("차단된 확장자는 BLOCKED_EXTENSION 오류를 반환한다")
    void rejectsBlockedExtension() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440004";
        var file = new MockMultipartFile("file", "malware.exe", "application/octet-stream", "blocked".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new BlockedExtensionException(ExtensionName.from("exe")));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXTENSION"))
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.message").value("차단된 확장자(exe)는 업로드할 수 없습니다."));
    }

    @Test
    @DisplayName("실행 가능한 MIME은 BLOCKED_EXECUTABLE_MIME 오류를 반환한다")
    void rejectsExecutableMime() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440005";
        var file = new MockMultipartFile("file", "renamed.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new ExecutableMimeTypeException());

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXECUTABLE_MIME"))
                .andExpect(jsonPath("$.message").value("실행 가능한 파일 형식은 업로드할 수 없습니다."));
    }

    @Test
    @DisplayName("MIME 감지 실패는 500과 FILE_TYPE_DETECTION_FAILED 오류를 반환한다")
    void mapsFileTypeDetectionFailure() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440010";
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new FileTypeDetectionFailedException());

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("FILE_TYPE_DETECTION_FAILED"))
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.context").isMap())
                .andExpect(jsonPath("$.context.*").doesNotExist())
                .andExpect(jsonPath("$.message").value("파일 형식을 확인하지 못했습니다. 잠시 후 다시 시도해 주세요."));
    }

    @Test
    @DisplayName("파일 저장 실패는 FILE_UPLOAD_FAILED 오류를 반환한다")
    void mapsStorageFailure() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440006";
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class))).thenThrow(
                new FileUploadFailedException("파일을 저장하지 못했습니다.", new IllegalStateException())
        );

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("FILE_UPLOAD_FAILED"))
                .andExpect(jsonPath("$.requestId").value(requestId));
    }

    @Test
    @DisplayName("업로드 용량 초과는 413과 FILE_SIZE_EXCEEDED 오류를 반환한다")
    void rejectsExceededUploadSize() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440007";
        var file = new MockMultipartFile("file", "large.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new MaxUploadSizeExceededException(10L * 1024 * 1024));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isPayloadTooLarge())
                .andExpect(jsonPath("$.code").value("FILE_SIZE_EXCEEDED"))
                .andExpect(jsonPath("$.requestId").value(requestId));
    }

    @Test
    @DisplayName("처리 중인 requestId는 409와 Retry-After를 반환한다")
    void returnsRetryAfterForInProgressRequest() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440008";
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(eq(requestId), any(MultipartFile.class)))
                .thenThrow(new IdempotencyInProgressException(requestId, 3));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", requestId));

        // then
        result.andExpect(status().isConflict())
                .andExpect(header().string("Retry-After", "3"))
                .andExpect(jsonPath("$.code").value("IDEMPOTENCY_IN_PROGRESS"))
                .andExpect(jsonPath("$.requestId").value(requestId));
    }
}
