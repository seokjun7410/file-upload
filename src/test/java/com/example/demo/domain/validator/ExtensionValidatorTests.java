package com.example.demo.domain.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.ExtensionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionValidatorTests {

    @Test
    @DisplayName("빈 확장자는 유효하지 않다")
    void rejectsBlankExtension() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionValidator.validateExtension(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not be blank");
    }

    @Test
    @DisplayName("점이 포함된 확장자는 유효하지 않다")
    void rejectsExtensionContainingDot() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionValidator.validateExtension("tar.gz"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not contain a dot");
    }
}
