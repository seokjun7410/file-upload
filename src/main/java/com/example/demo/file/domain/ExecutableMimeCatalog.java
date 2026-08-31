package com.example.demo.file.domain;

import java.util.Set;

/** 업로드를 차단할 실행 가능한 바이너리 MIME 목록을 제공한다. */
public final class ExecutableMimeCatalog {

    private static final Set<String> BLOCKED_MIME_TYPES = Set.of(
            "application/x-dosexec",
            "application/x-msdownload",
            "application/x-executable",
            "application/x-elf",
            "application/x-mach-binary",
            "application/x-sharedlib",
            "application/java-archive",
            "application/x-java-archive"
    );

    private ExecutableMimeCatalog() {
    }

    /** MIME이 실행 가능한 바이너리 형식인지 확인한다. */
    public static boolean isBlocked(String mimeType) {
        return BLOCKED_MIME_TYPES.contains(mimeType);
    }
}
