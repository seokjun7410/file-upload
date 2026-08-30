package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.common.ExtensionPolicyRestExceptionHandler;
import com.example.demo.file.controller.FileUploadRestController;
import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.InvalidFileException;
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
    @DisplayName("multipart 파일을 업로드하면 생성된 파일명과 완료 메시지를 반환한다")
    void uploadsMultipartFile() throws Exception {
        // given
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(any(MultipartFile.class))).thenReturn(new UploadedFile("generated.txt"));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files").file(file));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").value("generated.txt"))
                .andExpect(jsonPath("$.message").value("파일 업로드가 완료되었습니다."));
    }

    @Test
    @DisplayName("파일 multipart가 없으면 INVALID_FILE 오류를 반환한다")
    void rejectsMissingMultipartFile() throws Exception {
        // given

        // when
        var result = mockMvc.perform(multipart("/api/v1/files"));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
    }

    @Test
    @DisplayName("확장자가 없는 파일은 INVALID_FILE 오류를 반환한다")
    void rejectsExtensionlessFile() throws Exception {
        // given
        var file = new MockMultipartFile("file", "README", "text/plain", "content".getBytes());
        when(service.upload(any(MultipartFile.class)))
                .thenThrow(new InvalidFileException("확장자가 없는 파일은 업로드할 수 없습니다."));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files").file(file));

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
    }

    @Test
    @DisplayName("차단된 확장자는 BLOCKED_EXTENSION 오류를 반환한다")
    void rejectsBlockedExtension() throws Exception {
        // given
        var file = new MockMultipartFile("file", "malware.exe", "application/octet-stream", "blocked".getBytes());
        when(service.upload(any(MultipartFile.class)))
                .thenThrow(new BlockedExtensionException(ExtensionName.from("exe")));

        // when
        var result = mockMvc.perform(multipart("/api/v1/files").file(file));

        // then
        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXTENSION"))
                .andExpect(jsonPath("$.message").value("차단된 확장자(exe)는 업로드할 수 없습니다."));
    }

    @Test
    @DisplayName("파일 저장 실패는 FILE_UPLOAD_FAILED 오류를 반환한다")
    void mapsStorageFailure() throws Exception {
        // given
        var file = new MockMultipartFile("file", "readme.txt", "text/plain", "content".getBytes());
        when(service.upload(any(MultipartFile.class))).thenThrow(
                new FileUploadFailedException("파일을 저장하지 못했습니다.", new IllegalStateException())
        );

        // when
        var result = mockMvc.perform(multipart("/api/v1/files").file(file));

        // then
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("FILE_UPLOAD_FAILED"));
    }
}
