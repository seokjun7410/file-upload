package com.example.demo.domain.value;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionNameTests {

    @Test
    @DisplayName("확장자 이름을 정규화된 값 객체로 생성한다")
    void createsNormalizedExtensionName() {
        // given

        // when
        ExtensionName extensionName = ExtensionName.from(" EXE ");

        // then
        assertThat(extensionName.value()).isEqualTo("exe");
    }

    @Test
    @DisplayName("유효하지 않은 확장자로 확장자 이름을 생성할 수 없다")
    void rejectsInvalidExtensionName() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionName.from("tar.gz"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not contain a dot");
    }
}
