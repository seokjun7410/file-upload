package com.example.demo.file.service.impl;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import com.example.demo.file.exception.FileUploadFailedException;
import com.example.demo.file.service.FileStorage;
import com.example.demo.file.service.StoredFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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

    /** UUID와 확장자로 서버 저장 파일명을 생성한다. */
    @Override
    public String generateFilename(ExtensionName extension) {
        return UUID.randomUUID() + "." + extension.value();
    }

    /** 업로드 파일을 지정한 이름으로 임시 저장한다. */
    @Override
    public void storeTemporary(MultipartFile file, String filename) {
        Path target = temporaryPath(filename);

        try {
            Files.createDirectories(target.getParent());
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target);
            }
        } catch (IOException exception) {
            deletePartialFile(target);
            throw new FileUploadFailedException("파일을 저장하지 못했습니다.", exception);
        }
    }

    /** 임시 파일을 같은 파일시스템의 최종 경로로 원자적으로 이동한다. */
    @Override
    public void finalizeFile(String filename) {
        Path temporary = temporaryPath(filename);
        Path target = uploadDirectory.resolve(filename).normalize();
        try {
            Files.createDirectories(uploadDirectory);
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException exception) {
            throw new FileUploadFailedException("파일을 저장하지 못했습니다.", exception);
        }
    }

    /** 임시 파일을 삭제한다. */
    @Override
    public void deleteTemporary(String filename) {
        deletePartialFile(temporaryPath(filename));
    }

    /** 최종 저장 파일을 삭제한다. */
    @Override
    public void deleteFinal(String filename) {
        deletePartialFile(uploadDirectory.resolve(filename).normalize());
    }

    /** 최종 경로에 확정 파일이 존재하는지 반환한다. */
    @Override
    public boolean finalFileExists(String filename) {
        return Files.isRegularFile(uploadDirectory.resolve(filename).normalize());
    }

    /** 저장 루트 아래의 임시 파일 경로를 계산한다. */
    private Path temporaryPath(String filename) {
        return uploadDirectory.resolve(".tmp").resolve(filename + ".part").normalize();
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
