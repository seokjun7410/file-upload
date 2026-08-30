package com.example.demo.file.domain;

/** 하나의 확장자 정책이 고정 정책인지 커스텀 정책인지 나타낸다. */
public enum PolicyType {

    /** 애플리케이션이 기본 제공하며 차단 상태를 변경할 수 있는 정책이다. */
    FIXED,

    /** 사용자가 등록하고 삭제로 차단 여부를 표현하는 정책이다. */
    CUSTOM
}
