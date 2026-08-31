package com.example.demo.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:extension-policy-integration;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ExtensionPolicyApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final List<Path> createdFiles = new ArrayList<>();

    @AfterEach
    void removesCreatedFiles() throws IOException {
        for (Path createdFile : createdFiles) {
            Files.deleteIfExists(createdFile);
        }
    }

    @Test
    @DisplayName("최초 정책 조회는 차단 해제된 고정 확장자와 빈 커스텀 목록을 반환한다")
    void getsInitialPolicies() throws Exception {
        // given

        // when
        var result = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed").isArray())
                .andExpect(jsonPath("$.fixed").isNotEmpty())
                .andExpect(jsonPath("$.fixed[0].extension").isNotEmpty())
                .andExpect(jsonPath("$.fixed[0].blocked").value(false))
                .andExpect(jsonPath("$.fixed[?(@.blocked == true)]").isEmpty())
                .andExpect(jsonPath("$.custom").isEmpty());
    }

    @Test
    @DisplayName("정책을 변경·등록·삭제하면 다음 정책 조회에 최신 상태가 반영된다")
    void getsLatestPoliciesAfterMutations() throws Exception {
        // given
        var changeResult = mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"blocked\":true}"));
        var registerResult = mockMvc.perform(post("/api/v1/extension-policies/custom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"extension\":\"sh\"}"));

        // when
        var resultAfterMutation = mockMvc.perform(get("/api/v1/extension-policies"));
        var deleteResult = mockMvc.perform(delete("/api/v1/extension-policies/custom/sh"));
        var resultAfterDelete = mockMvc.perform(get("/api/v1/extension-policies"));

        // then
        changeResult.andExpect(status().isOk());
        registerResult.andExpect(status().isCreated());
        resultAfterMutation.andExpect(status().isOk())
                .andExpect(jsonPath("$.fixed[?(@.extension == 'exe')].blocked", hasItem(true)))
                .andExpect(jsonPath("$.custom", hasItem("sh")));
        deleteResult.andExpect(status().isNoContent());
        resultAfterDelete.andExpect(status().isOk())
                .andExpect(jsonPath("$.custom").isEmpty());
    }

    @Test
    @DisplayName("차단된 고정 확장자는 저장하지 않고 해제하면 업로드할 수 있다")
    void appliesFixedPolicyToFileUpload() throws Exception {
        // given
        mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":true}"));
        Set<Path> beforeBlockedUpload = snapshotUploadFiles();
        var blockedFile = new MockMultipartFile(
                "file", "malware.exe", "application/octet-stream", "blocked".getBytes()
        );

        // when
        var blockedResult = mockMvc.perform(multipart("/api/v1/files")
                .file(blockedFile)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440010"));

        // then
        blockedResult.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXTENSION"));
        org.assertj.core.api.Assertions.assertThat(snapshotUploadFiles())
                .isEqualTo(beforeBlockedUpload);

        // when
        mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":false}"));
        var allowedResult = mockMvc.perform(multipart("/api/v1/files")
                .file(blockedFile)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440011"));

        // then
        allowedResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.filename").value(org.hamcrest.Matchers.endsWith(".exe")))
                .andExpect(jsonPath("$.message").value("파일 업로드가 완료되었습니다."));
        rememberCreatedFile(allowedResult.andReturn());
    }

    @Test
    @DisplayName("차단된 중간 확장자는 HTTP 업로드에서도 거부되고 저장하지 않는다")
    void rejectsBlockedIntermediateExtensionThroughApi() throws Exception {
        // given
        mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":true}"));
        Set<Path> beforeUpload = snapshotUploadFiles();
        var file = new MockMultipartFile(
                "file", "malware.exe.pdf", "application/pdf", "blocked".getBytes()
        );

        // when
        var result = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440018"));

        // then
        result.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXTENSION"))
                .andExpect(jsonPath("$.context.extension").value("exe"));
        org.assertj.core.api.Assertions.assertThat(snapshotUploadFiles()).isEqualTo(beforeUpload);
    }

    @Test
    @DisplayName("완료된 requestId를 재사용하면 파일을 다시 저장하지 않고 기존 성공 응답을 반환한다")
    void reusesCompletedUploadResult() throws Exception {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440017";
        var file = new MockMultipartFile("file", "readme.exe", "text/plain", "content".getBytes());
        var firstResult = mockMvc.perform(multipart("/api/v1/files")
                        .file(file)
                        .header("Idempotency-Key", requestId))
                .andExpect(status().isCreated())
                .andReturn();
        String storedFilename = objectMapper.readTree(firstResult.getResponse().getContentAsString())
                .get("filename")
                .asText();
        rememberCreatedFile(firstResult);

        mockMvc.perform(patch("/api/v1/extension-policies/fixed/exe")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\":true}"));

        // when
        var replayResult = mockMvc.perform(multipart("/api/v1/files")
                .file(new MockMultipartFile("file", "different.exe", "application/octet-stream", "different".getBytes()))
                .header("Idempotency-Key", requestId));

        // then
        replayResult.andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.filename").value(storedFilename));
        org.assertj.core.api.Assertions.assertThat(snapshotUploadFiles())
                .contains(Path.of("uploads", storedFilename));
    }

    @Test
    @DisplayName("커스텀 확장자를 삭제하면 해당 확장자 파일 업로드가 허용된다")
    void appliesCustomPolicyToFileUpload() throws Exception {
        // given
        mockMvc.perform(post("/api/v1/extension-policies/custom")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"extension\":\"sh\"}"));
        var file = new MockMultipartFile("file", "script.sh", "text/plain", "content".getBytes());

        // when
        var blockedResult = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440012"));

        // then
        blockedResult.andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("BLOCKED_EXTENSION"));

        // when
        mockMvc.perform(delete("/api/v1/extension-policies/custom/sh"))
                .andExpect(status().isNoContent());
        var allowedResult = mockMvc.perform(multipart("/api/v1/files")
                .file(file)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440013"));

        // then
        allowedResult.andExpect(status().isCreated());
        rememberCreatedFile(allowedResult.andReturn());
    }

    @Test
    @DisplayName("파일이 없거나 확장자가 없으면 INVALID_FILE 오류를 반환한다")
    void rejectsInvalidFileRequests() throws Exception {
        // given
        var emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        var extensionlessFile = new MockMultipartFile("file", "README", "text/plain", "content".getBytes());

        // when
        var missingResult = mockMvc.perform(multipart("/api/v1/files")
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440014"));
        var emptyResult = mockMvc.perform(multipart("/api/v1/files")
                .file(emptyFile)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440015"));
        var extensionlessResult = mockMvc.perform(multipart("/api/v1/files")
                .file(extensionlessFile)
                .header("Idempotency-Key", "550e8400-e29b-41d4-a716-446655440016"));

        // then
        missingResult.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
        emptyResult.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
        extensionlessResult.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE"));
    }

    /** 업로드 디렉터리에 현재 존재하는 파일 목록을 테스트용 값으로 반환한다. */
    private Set<Path> snapshotUploadFiles() throws IOException {
        Path uploadDirectory = Path.of("uploads");
        if (Files.notExists(uploadDirectory)) {
            return Set.of();
        }
        try (var paths = Files.list(uploadDirectory)) {
            return paths.collect(java.util.stream.Collectors.toSet());
        }
    }

    /** 업로드 성공 응답의 서버 생성 파일을 테스트 종료 시 정리 대상으로 등록한다. */
    private void rememberCreatedFile(org.springframework.test.web.servlet.MvcResult result) throws Exception {
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        createdFiles.add(Path.of("uploads", response.get("filename").asText()));
    }

}
