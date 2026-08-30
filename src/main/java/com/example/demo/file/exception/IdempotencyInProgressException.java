package com.example.demo.file.exception;

/** 같은 requestId의 업로드가 아직 완료되지 않았음을 표현한다. */
public final class IdempotencyInProgressException extends RuntimeException {

    private final String requestId;
    private final long retryAfterSeconds;

    public IdempotencyInProgressException(String requestId, long retryAfterSeconds) {
        super("같은 requestId의 업로드가 처리 중입니다.");
        this.requestId = requestId;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String requestId() {
        return requestId;
    }

    public long retryAfterSeconds() {
        return retryAfterSeconds;
    }
}
