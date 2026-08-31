package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.example.demo.file.domain.entity.UploadFile;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.exception.FileTypeDetectionFailedException;
import com.example.demo.file.service.ExtensionPolicyService;
import com.example.demo.file.service.FileExtensionExtractor;
import com.example.demo.file.service.FileStorage;
import com.example.demo.file.service.MimeTypeDetectionResult;
import com.example.demo.file.service.MimeTypeDetector;
import com.example.demo.file.service.UploadReservation;
import com.example.demo.file.service.impl.FileUploadServiceImpl;
import com.example.demo.file.service.impl.RetryAfterCalculator;
import com.example.demo.file.service.impl.UploadFileStateService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileUploadMimePolicyTests {

    @Test
    @DisplayName("실행 가능한 MIME 파일은 저장 전에 업로드를 거부한다")
    void rejectsExecutableMimeBeforeStorage() {
        // given
        var file = new MockMultipartFile(
                "file",
                "renamed.txt",
                "text/plain",
                new byte[]{0x4d, 0x5a}
        );
        var extensionPolicyService = mock(ExtensionPolicyService.class);
        var fileStorage = mock(FileStorage.class);
        var mimeTypeDetector = mock(MimeTypeDetector.class);
        var stateService = mock(UploadFileStateService.class);
        when(mimeTypeDetector.detect(file))
                .thenReturn(MimeTypeDetectionResult.detected("application/x-dosexec"));
        var service = new FileUploadServiceImpl(
                extensionPolicyService,
                fileStorage,
                new FileExtensionExtractor(),
                mimeTypeDetector,
                stateService,
                new RetryAfterCalculator()
        );

        // when

        // then
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(ExecutableMimeTypeException.class)
                .hasMessage("실행 가능한 파일 형식은 업로드할 수 없습니다.");
        verify(extensionPolicyService, never()).isBlocked(ExtensionName.from("txt"));
        verify(fileStorage, never()).store(file, ExtensionName.from("txt"));
    }

    @Test
    @DisplayName("미확인 MIME 파일은 경고 후 기존 확장자 정책에 따라 저장한다")
    void storesUnknownMimeAfterWarning() {
        // given
        var file = new MockMultipartFile(
                "file",
                "unknown.bin",
                "application/octet-stream",
                new byte[]{0, 1, 2, 3}
        );
        var extensionPolicyService = mock(ExtensionPolicyService.class);
        var fileStorage = mock(FileStorage.class);
        var mimeTypeDetector = mock(MimeTypeDetector.class);
        var stateService = mock(UploadFileStateService.class);
        when(mimeTypeDetector.detect(file))
                .thenReturn(MimeTypeDetectionResult.unknown("application/octet-stream"));
        when(fileStorage.generateFilename(ExtensionName.from("bin"))).thenReturn("generated.bin");
        when(stateService.reserve(anyString(), any(), anyString()))
                .thenReturn(new UploadReservation(mock(UploadFile.class), true));
        var service = new FileUploadServiceImpl(
                extensionPolicyService,
                fileStorage,
                new FileExtensionExtractor(),
                mimeTypeDetector,
                stateService,
                new RetryAfterCalculator()
        );

        // when
        var uploadedFile = service.upload(file);

        // then
        assertThat(uploadedFile.filename()).isEqualTo("generated.bin");
        verify(extensionPolicyService).isBlocked(ExtensionName.from("bin"));
        verify(fileStorage).storeTemporary(file, "generated.bin");
    }

    @Test
    @DisplayName("MIME 감지 실패 파일은 저장 전에 업로드를 거부한다")
    void rejectsFileWhenMimeDetectionFails() {
        // given
        var file = new MockMultipartFile(
                "file",
                "readme.txt",
                "text/plain",
                "content".getBytes()
        );
        var extensionPolicyService = mock(ExtensionPolicyService.class);
        var fileStorage = mock(FileStorage.class);
        var mimeTypeDetector = mock(MimeTypeDetector.class);
        var stateService = mock(UploadFileStateService.class);
        when(mimeTypeDetector.detect(file)).thenReturn(MimeTypeDetectionResult.failed());
        var service = new FileUploadServiceImpl(
                extensionPolicyService,
                fileStorage,
                new FileExtensionExtractor(),
                mimeTypeDetector,
                stateService,
                new RetryAfterCalculator()
        );

        // when
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(FileTypeDetectionFailedException.class)
                .hasMessage("파일 형식을 확인하지 못했습니다. 잠시 후 다시 시도해 주세요.");

        // then
        verify(extensionPolicyService, never()).isBlocked(ExtensionName.from("txt"));
        verify(fileStorage, never()).generateFilename(ExtensionName.from("txt"));
        verify(fileStorage, never()).storeTemporary(any(), anyString());
        verify(stateService, never()).reserve(anyString(), any(), anyString());
    }

    @Test
    @DisplayName("미확인 MIME은 원본 파일명 없이 경고 로그를 남긴다")
    void warnsUnknownMimeWithoutOriginalFilename() {
        // given
        var file = new MockMultipartFile(
                "file",
                "private-report.bin",
                "application/octet-stream",
                new byte[]{0, 1, 2, 3}
        );
        var extensionPolicyService = mock(ExtensionPolicyService.class);
        var fileStorage = mock(FileStorage.class);
        var mimeTypeDetector = mock(MimeTypeDetector.class);
        var stateService = mock(UploadFileStateService.class);
        when(mimeTypeDetector.detect(file))
                .thenReturn(MimeTypeDetectionResult.unknown("application/octet-stream"));
        when(fileStorage.generateFilename(ExtensionName.from("bin"))).thenReturn("generated.bin");
        when(stateService.reserve(anyString(), any(), anyString()))
                .thenReturn(new UploadReservation(mock(UploadFile.class), true));
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FileUploadServiceImpl.class);
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.WARN);
        var service = new FileUploadServiceImpl(
                extensionPolicyService,
                fileStorage,
                new FileExtensionExtractor(),
                mimeTypeDetector,
                stateService,
                new RetryAfterCalculator()
        );

        // when
        service.upload(file);

        // then
        assertThat(appender.list)
                .anySatisfy(event -> assertThat(event.getFormattedMessage())
                        .contains("extension=bin")
                        .contains("detectedMime=application/octet-stream")
                        .doesNotContain("private-report.bin"));
        logger.detachAppender(appender);
    }
}
