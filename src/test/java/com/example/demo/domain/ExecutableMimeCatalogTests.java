package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.ExecutableMimeCatalog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExecutableMimeCatalogTests {

    @Test
    @DisplayName("실행 가능한 바이너리 MIME은 차단 대상으로 판정한다")
    void blocksExecutableBinaryMime() {
        // given
        String executableMime = "application/x-dosexec";

        // when
        boolean blocked = ExecutableMimeCatalog.isBlocked(executableMime);

        // then
        assertThat(blocked).isTrue();
    }

    @Test
    @DisplayName("text/plain MIME은 차단 대상으로 판정하지 않는다")
    void allowsPlainTextMime() {
        // given
        String plainTextMime = "text/plain";

        // when
        boolean blocked = ExecutableMimeCatalog.isBlocked(plainTextMime);

        // then
        assertThat(blocked).isFalse();
    }
}
