package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.UploadStatus;
import com.example.demo.file.domain.entity.vo.OriginalFilename;
import com.example.demo.file.service.impl.UploadFileStateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:upload-state-service-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UploadFileStateServiceTests {

    @Autowired
    private UploadFileStateService stateService;

    @Test
    @DisplayName("같은 requestId를 다시 예약하면 기존 RECEIVING 상태를 재사용하고 관찰 횟수를 늘린다")
    void reusesExistingReceivingUpload() {
        // given
        String requestId = "550e8400-e29b-41d4-a716-446655440000";
        var originalFilename = OriginalFilename.from("report.txt");
        stateService.reserve(requestId, originalFilename, "stored.txt");

        // when
        var reservation = stateService.reserve(requestId, originalFilename, "other.txt");

        // then
        assertThat(reservation.newlyCreated()).isFalse();
        assertThat(reservation.uploadFile().getStatus()).isEqualTo(UploadStatus.RECEIVING);
        assertThat(reservation.uploadFile().getStoredFilename()).isEqualTo("stored.txt");
        assertThat(reservation.uploadFile().getRetryObservationCount()).isEqualTo(1);
    }
}
