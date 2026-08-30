package com.example.demo.file.domain.entity.vo;

import com.example.demo.file.domain.ExtensionNormalizer;
import com.example.demo.file.domain.ExtensionValidator;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

/** 정규화와 형식 검증을 통과한 확장자 이름을 표현하는 도메인 값이다. */
@Embeddable
@EqualsAndHashCode
public final class ExtensionName {

    private String value;

    /** JPA가 저장된 확장자 이름을 복원할 때 사용하는 생성자다. */
    protected ExtensionName() {
    }

    /** 입력을 정규화하고 유효성을 검증한 뒤 확장자 이름을 생성한다. */
    private ExtensionName(String value) {
        String normalizedValue = ExtensionNormalizer.normalize(value);
        ExtensionValidator.validateExtension(normalizedValue);
        this.value = normalizedValue;
    }

    /** 원본 입력을 확장자 이름 값으로 변환한다. */
    public static ExtensionName from(String extension) {
        return new ExtensionName(extension);
    }

    /** 정규화된 확장자 문자열을 반환한다. */
    public String value() {
        return value;
    }

    /** 확장자 이름의 문자열 표현을 반환한다. */
    @Override
    public String toString() {
        return value;
    }
}
