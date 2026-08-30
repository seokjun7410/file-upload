package com.example.demo.file.service.impl;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.service.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** 설정된 저장 루트에 UUID 기반 파일명으로 업로드 파일을 저장한다. */
@Component
public class LocalFileStorage implements FileStorage {

    private static final String DEFAULT_UPLOAD_DIRECTORY = "./uploads";

    private final Path uploadDirectory;

    /** 애플리케이션 설정으로 지정된 업로드 디렉터리를 사용하는 저장소를 생성한다. */
    @Autowired
    public LocalFileStorage(
            @Value("${file.upload.storage-path:./uploads}") String uploadDirectory
    ) {
        this(Path.of(uploadDirectory));
    }

    /** 테스트와 기본 동작에서 사용할 업로드 디렉터리를 생성한다. */
    public LocalFileStorage() {
        this(Path.of(DEFAULT_UPLOAD_DIRECTORY));
    }

    /** 지정한 디렉터리를 사용하는 저장소를 생성한다. 테스트에서 격리된 저장 위치를 주입할 때 사용한다. */
    public LocalFileStorage(Path uploadDirectory) {
        this.uploadDirectory = uploadDirectory.toAbsolutePath().normalize();
    }

    /** 업로드 파일을 UUID와 확장자로 구성한 새 파일명으로 저장한다. */
    @Override
    public String store(MultipartFile file, ExtensionName extension) {
        String filename = UUID.randomUUID() + "." + extension.value();
        Path target = uploadDirectory.resolve(filename).normalize();

        try {
            Files.createDirectories(uploadDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target);
            }
            return filename;
        } catch (IOException exception) {
            deletePartialFile(target);
            throw new FileUploadFailedException("파일을 저장하지 못했습니다.", exception);
        }
    }

    /** 저장에 실패한 부분 파일을 제거해 불완전한 업로드가 남지 않도록 한다. */
    private void deletePartialFile(Path target) {
        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
            // 원래 저장 오류를 호출자에게 전달하고 정리 실패는 내부에 남긴다.
        }
    }
}
