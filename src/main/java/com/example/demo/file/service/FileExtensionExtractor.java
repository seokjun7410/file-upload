package com.example.demo.file.service;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.InvalidFileException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 파일명에서 검증된 확장자 이름을 추출하는 파일 입력 모듈이다. */
@Component
public final class FileExtensionExtractor {

    /** 원본 파일명에서 마지막 확장자를 찾아 검증된 확장자 이름으로 반환한다. */
    public ExtensionName extract(MultipartFile file) {
        return extractAll(file).getLast();
    }

    /** 원본 파일명에서 모든 확장자 구간을 파일명 순서대로 추출한다. */
    public List<ExtensionName> extractAll(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileException("업로드 파일명이 없습니다.");
        }

        String basename = originalFilename.replace('\\', '/');
        int lastSlash = basename.lastIndexOf('/');
        basename = basename.substring(lastSlash + 1);
        int firstDot = basename.indexOf('.');
        int lastDot = basename.lastIndexOf('.');
        if (firstDot <= 0 || lastDot == basename.length() - 1) {
            throw new InvalidFileException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }

        List<ExtensionName> extensions = new ArrayList<>();
        for (String extension : basename.substring(firstDot + 1).split("\\.", -1)) {
            if (extension.isBlank()) {
                continue;
            }
            try {
                extensions.add(ExtensionName.from(extension));
            } catch (IllegalArgumentException exception) {
                throw new InvalidFileException("업로드 파일 확장자가 올바르지 않습니다.");
            }
        }
        return List.copyOf(extensions);
    }
}
