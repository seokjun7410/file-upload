package com.example.demo.file.domain;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import java.util.List;

/** 스프린트 1에서 항상 관리해야 하는 고정 차단 확장자 이름 목록을 제공한다. */
public final class FixedExtensionCatalog {

    private static final List<ExtensionName> DEFAULT_EXTENSIONS = List.of(
            ExtensionName.from("bat"),
            ExtensionName.from("cmd"),
            ExtensionName.from("com"),
            ExtensionName.from("cpl"),
            ExtensionName.from("exe"),
            ExtensionName.from("scr"),
            ExtensionName.from("js")
    );

    private FixedExtensionCatalog() {
    }

    /** 초기화와 조회에 사용할 고정 확장자 이름 7개의 순서 있는 목록을 반환한다. */
    public static List<ExtensionName> defaultExtensions() {
        return DEFAULT_EXTENSIONS;
    }

    /** 정규화된 확장자가 애플리케이션 기본 고정 목록에 포함되는지 확인한다. */
    public static boolean contains(ExtensionName extension) {
        return DEFAULT_EXTENSIONS.contains(extension);
    }
}
