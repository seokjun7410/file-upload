package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.InvalidFileException;
import com.example.demo.file.service.FileExtensionExtractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileExtensionExtractorTests {

    private final FileExtensionExtractor extractor = new FileExtensionExtractor();

    @Test
    @DisplayName("파일명에서 마지막 확장자를 정규화된 값 객체로 추출한다")
    void extractsNormalizedExtensionName() {
        // given
        var file = new MockMultipartFile(
                "file",
                "directory\\readme.TXT",
                "text/plain",
                "content".getBytes()
        );

        // when
        ExtensionName extension = extractor.extract(file);

        // then
        assertThat(extension).isEqualTo(ExtensionName.from("txt"));
    }

    @Test
    @DisplayName("파일명의 모든 확장자 구간을 파일명 순서대로 추출한다")
    void extractsAllExtensionSegments() {
        // given
        var file = new MockMultipartFile(
                "file",
                "test.EXE.PDF",
                "application/pdf",
                "content".getBytes()
        );

        // when
        var extensions = extractor.extractAll(file);

        // then
        assertThat(extensions).containsExactly(ExtensionName.from("exe"), ExtensionName.from("pdf"));
    }

    @Test
    @DisplayName("점이 여러 개인 파일명에서는 마지막 확장자를 추출한다")
    void extractsLastExtension() {
        // given
        var file = new MockMultipartFile(
                "file",
                "archive.tar.gz",
                "application/gzip",
                "content".getBytes()
        );

        // when
        ExtensionName extension = extractor.extract(file);

        // then
        assertThat(extension).isEqualTo(ExtensionName.from("gz"));
    }

    @Test
    @DisplayName("확장자가 없는 파일명은 거부한다")
    void rejectsExtensionlessFile() {
        // given
        var file = new MockMultipartFile(
                "file",
                "README",
                "text/plain",
                "content".getBytes()
        );

        // when / then
        assertThatThrownBy(() -> extractor.extract(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("확장자가 없는 파일은 업로드할 수 없습니다.");
    }

    @Test
    @DisplayName("빈 파일은 확장자와 관계없이 거부한다")
    void rejectsEmptyFile() {
        // given
        var file = new MockMultipartFile(
                "file",
                "readme.txt",
                "text/plain",
                new byte[0]
        );

        // when / then
        assertThatThrownBy(() -> extractor.extract(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("업로드할 파일이 없습니다.");
    }

    @Test
    @DisplayName("길이 제한을 초과한 확장자는 파일 입력 오류로 변환한다")
    void rejectsExtensionLongerThanAllowedLength() {
        // given
        var file = new MockMultipartFile(
                "file",
                "readme.abcdefghijklmnopqrstu",
                "text/plain",
                "content".getBytes()
        );

        // when / then
        assertThatThrownBy(() -> extractor.extract(file))
                .isInstanceOf(InvalidFileException.class)
                .hasMessage("업로드 파일 확장자가 올바르지 않습니다.");
    }
}
