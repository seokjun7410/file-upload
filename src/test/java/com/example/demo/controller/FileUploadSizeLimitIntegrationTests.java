package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;

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
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:file-upload-size-limit;DB_CLOSE_DELAY=-1",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class FileUploadSizeLimitIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("10MB를 초과하는 파일은 저장 전에 413으로 거부된다")
    void rejectsFileLargerThanTenMegabytes() {
        // given
        var file = resource("large.txt", 10 * 1024 * 1024 + 1);

        // when
        var response = upload(file);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).contains("FILE_SIZE_EXCEEDED");
    }

    @Test
    @DisplayName("전체 multipart 요청이 12MB를 초과하면 413으로 거부된다")
    void rejectsMultipartRequestLargerThanTwelveMegabytes() {
        // given
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", resource("allowed.txt", 9 * 1024 * 1024));
        body.add("padding", resource("padding.bin", 4 * 1024 * 1024));

        // when
        var response = restTemplate.postForEntity(
                "/api/v1/files",
                request(body),
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYLOAD_TOO_LARGE);
        assertThat(response.getBody()).contains("FILE_SIZE_EXCEEDED");
    }

    private ResponseEntity<String> upload(ByteArrayResource file) {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", file);
        return restTemplate.postForEntity("/api/v1/files", request(body), String.class);
    }

    private HttpEntity<MultiValueMap<String, Object>> request(
            MultiValueMap<String, Object> body
    ) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    private ByteArrayResource resource(String filename, int size) {
        return new ByteArrayResource(new byte[size]) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }
}
