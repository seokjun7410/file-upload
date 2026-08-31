package com.example.demo.file.domain;

/** 확장자 정책이 업로드를 차단하는지 나타내는 감사 이력 상태다. */
public enum ExtensionPolicyAuditState {

    /** 확장자 정책이 업로드를 차단하는 상태다. */
    BLOCKED,

    /** 확장자 정책이 업로드를 허용하는 상태다. */
    UNBLOCKED;

    /** 정책의 boolean 차단 상태를 감사 이력 상태로 변환한다. */
    public static ExtensionPolicyAuditState from(final boolean blocked) {
        return blocked ? BLOCKED : UNBLOCKED;
    }
}
