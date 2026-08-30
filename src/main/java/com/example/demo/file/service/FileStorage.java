package com.example.demo.file.service;

import com.example.demo.file.domain.entity.vo.ExtensionName;
import org.springframework.web.multipart.MultipartFile;

/** 검증이 끝난 업로드 파일을 서버의 로컬 저장 위치에 기록한다. */
public interface FileStorage {

    /** 파일을 서버가 생성한 이름으로 저장하고 저장된 파일명을 반환한다. */
    String store(MultipartFile file, ExtensionName extension);
}
