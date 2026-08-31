package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.repository.UploadFileRepository;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:multipart-part-limit-integration;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class MultipartFileCountIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UploadFileRepository uploadFileRepository;

    private Set<Path> existingUploadFiles;

    @BeforeEach
    void remembersExistingFiles() throws Exception {
        existingUploadFiles = snapshotUploadFiles();
    }

    @AfterEach
    void removesCreatedFiles() throws Exception {
        Path uploadDirectory = Path.of("uploads");
        if (Files.notExists(uploadDirectory)) {
            return;
        }
        try (var paths = Files.list(uploadDirectory)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> !existingUploadFiles.contains(path))
                    .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (Exception exception) {
                    throw new IllegalStateException("테스트 파일을 정리하지 못했습니다.", exception);
                }
            });
        }
    }

    private Set<Path> snapshotUploadFiles() throws Exception {
        Path uploadDirectory = Path.of("uploads");
        if (Files.notExists(uploadDirectory)) {
            return Set.of();
        }
        try (var paths = Files.list(uploadDirectory)) {
            return paths.filter(Files::isRegularFile).collect(Collectors.toSet());
        }
    }

    @Test
    @DisplayName("파일 part 하나를 보내면 업로드 요청이 처리된다")
    void acceptsSingleMultipartPart() {
        // given
        var request = multipartRequest(UUID.randomUUID().toString(), resource("readme.txt"));

        // when
        var response = restTemplate.postForEntity("/api/v1/files", request, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(uploadFileRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("파일 part를 두 개 보내면 400 MULTIPLE_FILES_NOT_ALLOWED로 거부한다")
    void rejectsMultipleMultipartParts() {
        // given
        long uploadCountBeforeRequest = uploadFileRepository.count();
        var request = multipartRequest(
                UUID.randomUUID().toString(),
                resource("first.txt"),
                resource("second.txt")
        );

        // when
        var response = restTemplate.postForEntity("/api/v1/files", request, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("\"code\":\"MULTIPLE_FILES_NOT_ALLOWED\"");
        assertThat(uploadFileRepository.count()).isEqualTo(uploadCountBeforeRequest);
    }

    private HttpEntity<MultiValueMap<String, Object>> multipartRequest(
            String requestId,
            ByteArrayResource... resources
    ) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (ByteArrayResource resource : resources) {
            body.add("file", resource);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Idempotency-Key", requestId);
        return new HttpEntity<>(body, headers);
    }

    private ByteArrayResource resource(String filename) {
        return new ByteArrayResource("content".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
