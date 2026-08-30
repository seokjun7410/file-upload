package com.example.demo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.file.domain.FixedExtensionCatalog;
import com.example.demo.file.domain.entity.ExtensionPolicy;
import com.example.demo.file.domain.entity.vo.ExtensionName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExtensionPolicyDomainTests {

    @Test
    @DisplayName("고정 정책은 생성 시 차단 해제 상태다")
    void fixedPolicyStartsUnblocked() {
        // given
        String extension = " EXE ";

        // when
        ExtensionPolicy policy = ExtensionPolicy.fixed(ExtensionName.from(extension));

        // then
        assertThat(policy.getExtension().value()).isEqualTo("exe");
        assertThat(policy.isFixed()).isTrue();
        assertThat(policy.isCustom()).isFalse();
        assertThat(policy.isBlocked()).isFalse();
        assertThat(policy.isBlockedForUpload()).isFalse();
    }

    @Test
    @DisplayName("커스텀 정책은 생성 시 차단 상태다")
    void customPolicyStartsBlocked() {
        // given

        // when
        ExtensionPolicy policy = ExtensionPolicy.custom(ExtensionName.from("sh"));

        // then
        assertThat(policy.isFixed()).isFalse();
        assertThat(policy.isCustom()).isTrue();
        assertThat(policy.isBlocked()).isTrue();
        assertThat(policy.isBlockedForUpload()).isTrue();
    }

    @Test
    @DisplayName("고정 정책은 기본 고정 확장자 목록에 있는 확장자만 생성할 수 있다")
    void fixedPolicyRequiresCatalogExtension() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionPolicy.fixed(ExtensionName.from("unknown")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fixed policy must use a catalog extension");
    }

    @Test
    @DisplayName("커스텀 정책은 기본 고정 확장자를 등록할 수 없다")
    void customPolicyCannotUseCatalogExtension() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionPolicy.custom(ExtensionName.from("exe")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("custom policy cannot use a fixed extension");
    }

    @Test
    @DisplayName("고정 정책의 차단 상태를 변경할 수 있다")
    void fixedPolicyChangesBlockedState() {
        // given
        ExtensionPolicy policy = ExtensionPolicy.fixed(ExtensionName.from("exe"));

        // when
        policy.changeBlocked(true);

        // then
        assertThat(policy.isBlocked()).isTrue();
    }

    @Test
    @DisplayName("커스텀 정책의 차단 상태를 직접 변경할 수 없다")
    void customPolicyCannotChangeBlockedState() {
        // given
        ExtensionPolicy policy = ExtensionPolicy.custom(ExtensionName.from("sh"));

        // when

        // then
        assertThatThrownBy(() -> policy.changeBlocked(false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("custom policy cannot change blocked state");
    }

    @Test
    @DisplayName("확장자가 비어 있으면 정책을 생성할 수 없다")
    void rejectsBlankExtension() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionPolicy.custom(ExtensionName.from(" ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not be blank");
    }

    @Test
    @DisplayName("null 확장자는 정책을 생성할 수 없다")
    void rejectsNullExtension() {
        // given

        // when

        // then
        assertThatThrownBy(() -> ExtensionPolicy.custom(ExtensionName.from(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not be blank");
    }

    @Test
    @DisplayName("정확히 20자인 커스텀 확장자는 정책을 생성할 수 있다")
    void acceptsExtensionWithExactlyTwentyCharacters() {
        // given
        String extension = "abcdefghijklmnopqrst";

        // when
        ExtensionPolicy policy = ExtensionPolicy.custom(ExtensionName.from(extension));

        // then
        assertThat(policy.getExtension().value()).isEqualTo(extension);
    }

    @Test
    @DisplayName("20자를 초과한 확장자는 정책을 생성할 수 없다")
    void rejectsExtensionLongerThanTwentyCharacters() {
        // given
        String extension = "abcdefghijklmnopqrstu";

        // when

        // then
        assertThatThrownBy(() -> ExtensionPolicy.custom(ExtensionName.from(extension)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must be 20 characters or fewer");
    }

    @Test
    @DisplayName("점이 포함된 확장자는 정책을 생성할 수 없다")
    void rejectsExtensionContainingDot() {
        // given

        // when / then
        assertThatThrownBy(() -> ExtensionPolicy.custom(ExtensionName.from("tar.gz")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("extension must not contain a dot");
    }

    @Test
    @DisplayName("고정 확장자 기본 목록은 일곱 개다")
    void fixedExtensionCatalogContainsSevenExtensions() {
        // given

        // when
        var extensions = FixedExtensionCatalog.defaultExtensions();

        // then
        assertThat(extensions)
                .extracting(ExtensionName::value)
                .containsExactly("bat", "cmd", "com", "cpl", "exe", "scr", "js");
    }
}
