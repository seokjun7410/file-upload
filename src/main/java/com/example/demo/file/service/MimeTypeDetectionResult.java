package com.example.demo.file.service;

/** 업로드 파일 콘텐츠 MIME 감지 결과를 표현한다. */
public record MimeTypeDetectionResult(String mimeType, Status status) {

    /** MIME 감지 결과의 상태다. */
    public enum Status {
        DETECTED,
        UNKNOWN,
        FAILED
    }

    /** MIME을 감지한 결과를 생성한다. */
    public static MimeTypeDetectionResult detected(String mimeType) {
        return new MimeTypeDetectionResult(mimeType, Status.DETECTED);
    }

    /** MIME을 알 수 없는 결과를 생성한다. */
    public static MimeTypeDetectionResult unknown(String mimeType) {
        return new MimeTypeDetectionResult(mimeType, Status.UNKNOWN);
    }

    /** MIME 감지에 실패한 결과를 생성한다. */
    public static MimeTypeDetectionResult failed() {
        return new MimeTypeDetectionResult(null, Status.FAILED);
    }

    /** 파일 콘텐츠에서 MIME을 감지한 결과인지 확인한다. */
    public boolean isDetected() {
        return status == Status.DETECTED;
    }

    /** MIME을 알 수 없어 후속 정책에 위임해야 하는 결과인지 확인한다. */
    public boolean isUnknown() {
        return status != Status.DETECTED;
    }
}
