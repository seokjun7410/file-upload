package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.file.domain.ExecutableMimeCatalog;
import com.example.demo.file.service.MimeTypeDetectionResult;
import com.example.demo.file.service.impl.TikaMimeTypeDetector;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class TikaMimeTypeDetectorTests {

    @Test
    @DisplayName("파일 콘텐츠에서 일반 텍스트 MIME을 감지한다")
    void detectsPlainTextFromFileContent() {
        // given
        var file = new MockMultipartFile(
                "file",
                "renamed.bin",
                "application/octet-stream",
                "일반 텍스트".getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
        var detector = new TikaMimeTypeDetector();

        // when
        MimeTypeDetectionResult result = detector.detect(file);

        // then
        assertThat(result.mimeType()).isEqualTo("text/plain");
        assertThat(result.isDetected()).isTrue();
    }

    @Test
    @DisplayName("판정할 수 없는 바이너리는 미확인 MIME 결과로 반환한다")
    void returnsUnknownForUnrecognizedContent() {
        // given
        var file = new MockMultipartFile(
                "file",
                "unknown.txt",
                "text/plain",
                new byte[]{0, 1, 2, 3, 4, 5}
        );
        var detector = new TikaMimeTypeDetector();

        // when
        MimeTypeDetectionResult result = detector.detect(file);

        // then
        assertThat(result.isUnknown()).isTrue();
        assertThat(result.mimeType()).isEqualTo("application/octet-stream");
    }

    @Test
    @DisplayName("콘텐츠 MIME 감지에 실패하면 실패 결과로 반환한다")
    void returnsFailedWhenContentDetectionFails() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("read failed"));
        var detector = new TikaMimeTypeDetector();

        // when
        MimeTypeDetectionResult result = detector.detect(file);

        // then
        assertThat(result.status()).isEqualTo(MimeTypeDetectionResult.Status.FAILED);
        assertThat(result.isUnknown()).isFalse();
        assertThat(result.isFailed()).isTrue();
    }

    @Test
    @DisplayName("대표적인 실행 파일 헤더를 실행 MIME으로 감지한다")
    void detectsExecutableHeaderAsExecutableMime() {
        // given
        byte[] executableHeader = new byte[64];
        executableHeader[0] = 'M';
        executableHeader[1] = 'Z';
        var file = new MockMultipartFile("file", "renamed.txt", "text/plain", executableHeader);
        var detector = new TikaMimeTypeDetector();

        // when
        MimeTypeDetectionResult result = detector.detect(file);

        // then
        assertThat(result.isDetected()).isTrue();
        assertThat(ExecutableMimeCatalog.isBlocked(result.mimeType())).isTrue();
    }
}
