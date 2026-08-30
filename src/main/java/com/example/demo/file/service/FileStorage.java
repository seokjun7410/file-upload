package com.example.demo.file.service;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import org.springframework.web.multipart.MultipartFile;

/** 검증이 끝난 업로드 파일을 서버의 로컬 저장 위치에 기록한다. */
public interface FileStorage {

    /** 검증된 확장자로 서버 저장 파일명을 생성한다. */
    String generateFilename(ExtensionName extension);

    /** 지정한 서버 파일명을 사용해 파일을 임시 경로에 저장한다. */
    void storeTemporary(MultipartFile file, String filename);

    /** 확장자를 기준으로 파일명을 만들고 임시 저장한다. */
    default StoredFile storeTemporary(MultipartFile file, ExtensionName extension) {
        String filename = generateFilename(extension);
        storeTemporary(file, filename);
        return new StoredFile(filename);
    }

    /** 임시 파일을 최종 경로로 원자적으로 확정한다. */
    void finalizeFile(String filename);

    /** 확정되지 않은 임시 파일을 삭제한다. */
    void deleteTemporary(String filename);

    /** 최종 저장 파일을 삭제한다. 저장 상태 전환 실패 뒤 orphan을 정리할 때 사용한다. */
    void deleteFinal(String filename);

    /** 최종 파일 존재 여부를 반환한다. */
    boolean finalFileExists(String filename);

    /** 기존 저장소 호출과의 호환을 위해 임시 저장·확정을 묶는다. */
    default String store(MultipartFile file, ExtensionName extension) {
        StoredFile storedFile = storeTemporary(file, extension);
        finalizeFile(storedFile.filename());
        return storedFile.filename();
    }
}
