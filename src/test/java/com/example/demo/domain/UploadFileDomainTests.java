package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.domain.entity.UploadFile;
import com.example.demo.file.domain.entity.vo.OriginalFilename;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UploadFileDomainTests {

    @Test
    @DisplayName("원본 경로를 제거한 파일명과 서버 저장 파일명으로 RECEIVING 업로드를 생성한다")
    void createsReceivingUploadWithBasename() {
        // given
        var originalFilename = OriginalFilename.from("../../report.txt");

        // when
        var uploadFile = UploadFile.receiving(
                "550e8400-e29b-41d4-a716-446655440000",
                originalFilename,
                "650e8400-e29b-41d4-a716-446655440000.txt"
        );

        // then
        assertThat(uploadFile.getRequestId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(uploadFile.getOriginalFilename()).isEqualTo("report.txt");
        assertThat(uploadFile.getStoredFilename()).isEqualTo("650e8400-e29b-41d4-a716-446655440000.txt");
        assertThat(uploadFile.getStatus()).isEqualTo(UploadStatus.RECEIVING);
    }
}
