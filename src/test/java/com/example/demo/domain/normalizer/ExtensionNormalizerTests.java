package com.example.demo.domain.normalizer;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.domain.ExtensionNormalizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionNormalizerTests {

    @Test
    @DisplayName("확장자의 앞뒤 공백을 제거하고 소문자로 변환한다")
    void normalizesExtension() {
        // given

        // when
        String normalized = ExtensionNormalizer.normalize(" EXE ");

        // then
        assertThat(normalized).isEqualTo("exe");
    }

    @Test
    @DisplayName("정규화기는 형식 검증 없이 빈 문자열을 반환할 수 있다")
    void normalizesWithoutValidating() {
        // given

        // when
        String normalized = ExtensionNormalizer.normalize(" ");

        // then
        assertThat(normalized).isEmpty();
    }
}
