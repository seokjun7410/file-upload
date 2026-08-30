package com.example.demo.file.domain;

/** 확장자 정책 변경의 운영상 사건 유형을 나타낸다. */
public enum ExtensionPolicyAuditAction {

    /** 초기화 과정에서 누락된 고정 정책을 생성한 사건이다. */
    INITIALIZED,

    /** 커스텀 정책을 등록한 사건이다. */
    CREATED,

    /** 고정 정책의 차단 상태를 변경한 사건이다. */
    BLOCKED_CHANGED,

    /** 커스텀 정책을 삭제한 사건이다. */
    DELETED
}
