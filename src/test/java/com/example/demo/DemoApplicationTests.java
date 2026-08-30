package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private MultipartProperties multipartProperties;

    @Test
    void contextLoads() {
    }

    @Test
    @DisplayName("multipart 파일과 전체 요청 용량 제한은 10MB와 12MB로 설정된다")
    void configuresMultipartSizeLimits() {
        // given

        // when
        var maxFileSize = multipartProperties.getMaxFileSize().toBytes();
        var maxRequestSize = multipartProperties.getMaxRequestSize().toBytes();

        // then
        assertThat(maxFileSize).isEqualTo(10L * 1024 * 1024);
        assertThat(maxRequestSize).isEqualTo(12L * 1024 * 1024);
    }
}
