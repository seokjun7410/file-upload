package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.exception.BlockedExtensionException;
import com.example.demo.file.exception.ExecutableMimeTypeException;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.exception.InvalidFileException;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.service.ExtensionPolicyService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.example.demo.file.service.impl.FileUploadServiceImpl;
import com.example.demo.file.service.impl.TikaMimeTypeDetector;
import com.example.demo.file.service.FileExtensionExtractor;
import com.example.demo.file.service.impl.LocalFileStorage;
import com.example.demo.file.service.impl.RetryAfterCalculator;
import com.example.demo.file.service.impl.UploadFileStateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:file-upload-service-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FileUploadServiceTests {

    @Autowired
    private ExtensionPolicyService extensionPolicyService;

    @Autowired
    private UploadFileStateService uploadFileStateService;

    @Autowired
    private RetryAfterCalculator retryAfterCalculator;

    @TempDir
    private Path uploadDirectory;

    @Test
    @DisplayName("허용된 파일을 UUID와 정규화된 확장자로 저장한다")
    void storesAllowedFileWithGeneratedFilename() throws IOException {
        // given
        var service = uploadService();
        var file = multipartFile("/tmp/readme.TXT", "hello");

        // when
        var uploadedFile = service.upload(file);

        // then
        assertThat(uploadedFile.filename()).matches("[0-9a-f-]{36}\\.txt");
        Path storedFile = uploadDirectory.resolve(uploadedFile.filename());
        assertThat(storedFile).exists().isRegularFile();
        assertThat(Files.readString(storedFile)).isEqualTo("hello");
    }

    @Test
    @DisplayName("원본 파일명의 경로와 무관하게 업로드 파일을 저장한다")
    void doesNotUseOriginalPathForStorage() {
        // given
        var service = uploadService();
        var file = multipartFile("../../outside.txt", "safe");

        // when
        var uploadedFile = service.upload(file);

        // then
        assertThat(uploadDirectory.resolve(uploadedFile.filename())).exists();
        assertThat(uploadedFile.filename()).doesNotContain("outside");
    }

    @Test
    @DisplayName("차단된 고정 확장자는 저장하지 않는다")
    void rejectsBlockedFixedExtensionBeforeStorage() throws IOException {
        // given
        extensionPolicyService.changeFixedBlocked(ExtensionName.from("exe"), true);
        var service = uploadService();
        var file = multipartFile("malware.exe", "blocked");

        // when

        // then
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(BlockedExtensionException.class)
                .hasMessage("차단된 확장자(exe)는 업로드할 수 없습니다.");
        assertThat(storedFiles()).isEmpty();
    }

    @Test
    @DisplayName("실행 파일을 텍스트 확장자로 위장해도 저장하지 않는다")
    void rejectsExecutableContentRenamedAsText() throws IOException {
        // given
        var service = uploadService();
        byte[] executableHeader = new byte[64];
        executableHeader[0] = 'M';
        executableHeader[1] = 'Z';
        var file = new MockMultipartFile("file", "document.txt", "text/plain", executableHeader);

        // when

        // then
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(ExecutableMimeTypeException.class)
                .hasMessage("실행 가능한 파일 형식은 업로드할 수 없습니다.");
        assertThat(storedFiles()).isEmpty();
    }

    @Test
    @DisplayName("커스텀 확장자를 삭제하면 같은 확장자 파일을 다시 업로드할 수 있다")
    void allowsCustomExtensionAfterPolicyDeletion() {
        // given
        extensionPolicyService.registerCustom(ExtensionName.from("sh"));
        var service = uploadService();
        var file = multipartFile("script.SH", "allowed after deletion");
        extensionPolicyService.deleteCustom(ExtensionName.from("sh"));

        // when
        var uploadedFile = service.upload(file);

        // then
        assertThat(uploadDirectory.resolve(uploadedFile.filename())).exists();
    }

    @Test
    @DisplayName("빈 파일과 확장자 없는 파일은 업로드하지 않는다")
    void rejectsEmptyAndExtensionlessFiles() throws IOException {
        // given
        var service = uploadService();
        var emptyFile = new MockMultipartFile("file", "empty.txt", "text/plain", new byte[0]);
        var extensionlessFile = multipartFile("README", "content");

        // when

        // then
        assertThatThrownBy(() -> service.upload(emptyFile))
                .isInstanceOf(InvalidFileException.class);
        assertThatThrownBy(() -> service.upload(extensionlessFile))
                .isInstanceOf(InvalidFileException.class);
        assertThat(storedFiles()).isEmpty();
    }

    @Test
    @DisplayName("저장 디렉터리를 사용할 수 없으면 파일 업로드 실패로 처리한다")
    void mapsStorageFailure() throws IOException {
        // given
        Path fileInsteadOfDirectory = uploadDirectory.resolve("not-a-directory");
        Files.createFile(fileInsteadOfDirectory);
        var service = new FileUploadServiceImpl(
                extensionPolicyService,
                new LocalFileStorage(fileInsteadOfDirectory),
                new FileExtensionExtractor(),
                new TikaMimeTypeDetector(),
                uploadFileStateService,
                retryAfterCalculator
        );
        var file = multipartFile("readme.txt", "content");

        // when

        // then
        assertThatThrownBy(() -> service.upload(file))
                .isInstanceOf(FileUploadFailedException.class)
                .hasMessage("파일을 저장하지 못했습니다.");
    }

    /** 테스트용 업로드 서비스에 임시 로컬 저장소를 연결한다. */
    private FileUploadServiceImpl uploadService() {
        return new FileUploadServiceImpl(
                extensionPolicyService,
                new LocalFileStorage(uploadDirectory),
                new FileExtensionExtractor(),
                new TikaMimeTypeDetector(),
                uploadFileStateService,
                retryAfterCalculator
        );
    }

    /** 테스트용 multipart 파일을 생성한다. */
    private MockMultipartFile multipartFile(String filename, String content) {
        return new MockMultipartFile(
                "file",
                filename,
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    /** 임시 업로드 디렉터리에 저장된 파일 목록을 반환한다. */
    private java.util.List<Path> storedFiles() throws IOException {
        try (var paths = Files.list(uploadDirectory)) {
            return paths.toList();
        }
    }
}
