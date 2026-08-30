package com.example.demo.file.service;

/** 임시 저장된 파일의 서버 저장 파일명을 표현한다. 실제 경로는 저장소가 소유한다. */
public record StoredFile(String filename) {
}
