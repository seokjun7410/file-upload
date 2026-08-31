package com.example.demo.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.file.domain.entity.UploadFile;
import com.example.demo.file.service.FileStorage;
import com.example.demo.file.service.impl.UploadFileRecoveryService;
import com.example.demo.file.service.impl.UploadFileStateService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UploadFileRecoveryServiceTests {

    @Mock
    private UploadFileStateService stateService;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private UploadFile completedCandidate;

    @Mock
    private UploadFile failedCandidate;

    private UploadFileRecoveryService recoveryService;

    @BeforeEach
    void setUp() {
        recoveryService = new UploadFileRecoveryService(stateService, fileStorage);
        ReflectionTestUtils.setField(recoveryService, "staleAfterSeconds", 1800L);
    }

    @Test
    @DisplayName("stale 상태에서 최종 파일이 있으면 완료로 복구하고 임시 파일을 정리한다")
    void completesStaleUploadWhenFinalFileExists() {
        // given
        when(stateService.findStaleReceiving(LocalDateTime.of(2026, 8, 30, 11, 30)))
                .thenReturn(List.of(completedCandidate));
        when(completedCandidate.getStoredFilename()).thenReturn("completed.txt");
        when(completedCandidate.getRequestId()).thenReturn("550e8400-e29b-41d4-a716-446655440020");
        when(fileStorage.finalFileExists("completed.txt")).thenReturn(true);

        // when
        recoveryService.recoverStaleUploads(LocalDateTime.of(2026, 8, 30, 12, 0));

        // then
        verify(fileStorage).deleteTemporary("completed.txt");
        verify(stateService).complete("550e8400-e29b-41d4-a716-446655440020");
    }

    @Test
    @DisplayName("stale 상태에서 최종 파일이 없으면 임시 파일을 정리하고 실패로 복구한다")
    void failsStaleUploadWhenFinalFileIsMissing() {
        // given
        when(stateService.findStaleReceiving(LocalDateTime.of(2026, 8, 30, 11, 30)))
                .thenReturn(List.of(failedCandidate));
        when(failedCandidate.getStoredFilename()).thenReturn("failed.txt");
        when(failedCandidate.getRequestId()).thenReturn("550e8400-e29b-41d4-a716-446655440021");
        when(fileStorage.finalFileExists("failed.txt")).thenReturn(false);

        // when
        recoveryService.recoverStaleUploads(LocalDateTime.of(2026, 8, 30, 12, 0));

        // then
        verify(fileStorage).deleteTemporary("failed.txt");
        verify(stateService).fail("550e8400-e29b-41d4-a716-446655440021", "FILE_UPLOAD_FAILED", 500);
    }
}
