package com.example.demo.common;

/** 도메인 엔티티를 찾지 못했을 때 사용하는 공통 예외다. */
public final class EntityNotFoundException extends RuntimeException {

    /** 도메인 이름을 포함한 엔티티 미존재 예외를 생성한다. */
    public EntityNotFoundException(String domainName) {
        super(domainName + " not found");
    }
}
