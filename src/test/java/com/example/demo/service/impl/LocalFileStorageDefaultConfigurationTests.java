package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.service.FileStorage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:local-file-storage-default-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LocalFileStorageDefaultConfigurationTests {

    @Autowired
    private FileStorage fileStorage;

    private Path storedFile;

    @AfterEach
    void removeStoredFile() throws IOException {
        if (storedFile != null) {
            Files.deleteIfExists(storedFile);
        }
    }

    @Test
    @DisplayName("저장 경로 설정이 없으면 기본 업로드 경로를 사용한다")
    void storesFileInDefaultDirectory() throws IOException {
        // given
        var file = new MockMultipartFile(
                "file",
                "readme.txt",
                "text/plain",
                "default storage".getBytes(StandardCharsets.UTF_8)
        );

        // when
        String filename = fileStorage.store(file, ExtensionName.from("txt"));
        storedFile = Path.of("./uploads").toAbsolutePath().normalize().resolve(filename);

        // then
        assertThat(storedFile).exists().isRegularFile();
        assertThat(Files.readString(storedFile)).isEqualTo("default storage");
    }
}
