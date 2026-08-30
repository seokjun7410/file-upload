package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.service.FileStorage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:local-file-storage-config-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LocalFileStorageConfigurationTests {

    @TempDir
    private static Path configuredDirectory;

    @Autowired
    private FileStorage fileStorage;

    @DynamicPropertySource
    static void configureStoragePath(DynamicPropertyRegistry registry) {
        registry.add("file.upload.storage-path", () -> configuredDirectory.toString());
    }

    @Test
    @DisplayName("설정한 저장 루트에 업로드 파일을 저장하고 기본 경로에는 저장하지 않는다")
    void storesFileInConfiguredDirectory() throws IOException {
        // given
        var file = new MockMultipartFile(
                "file",
                "readme.txt",
                "text/plain",
                "configured storage".getBytes(StandardCharsets.UTF_8)
        );

        // when
        String filename = fileStorage.store(file, ExtensionName.from("txt"));

        // then
        assertThat(configuredDirectory.resolve(filename)).exists().isRegularFile();
        assertThat(Files.readString(configuredDirectory.resolve(filename)))
                .isEqualTo("configured storage");
        assertThat(Path.of("uploads").toAbsolutePath().normalize().resolve(filename))
                .doesNotExist();
    }
}
