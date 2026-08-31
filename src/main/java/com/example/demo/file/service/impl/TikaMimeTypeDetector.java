package com.example.demo.file.service.impl;

import com.example.demo.file.service.MimeTypeDetectionResult;
import com.example.demo.file.service.MimeTypeDetector;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/** Apache Tika로 업로드 파일의 콘텐츠 MIME을 감지한다. */
@Component
public class TikaMimeTypeDetector implements MimeTypeDetector {

    private static final String UNKNOWN_MIME_TYPE = "application/octet-stream";
    private static final Logger log = LoggerFactory.getLogger(TikaMimeTypeDetector.class);

    private final Tika tika;

    /** 기본 Tika MIME 감지기를 생성한다. */
    public TikaMimeTypeDetector() {
        this(new Tika());
    }

    TikaMimeTypeDetector(Tika tika) {
        this.tika = tika;
    }

    /** 파일명과 요청 헤더를 사용하지 않고 파일 콘텐츠에서 MIME을 감지한다. */
    @Override
    public MimeTypeDetectionResult detect(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String mimeType = tika.detect(inputStream);
            if (isUnknownMime(mimeType)) {
                return MimeTypeDetectionResult.unknown(mimeType);
            }
            return MimeTypeDetectionResult.detected(mimeType);
        } catch (IOException exception) {
            log.warn("Tika MIME 감지에 실패했습니다.", exception);
            return MimeTypeDetectionResult.failed();
        }
    }

    /** Tika가 판정하지 못한 MIME인지 확인한다. */
    private boolean isUnknownMime(String mimeType) {
        return mimeType == null || mimeType.isBlank() || UNKNOWN_MIME_TYPE.equals(mimeType);
    }
}
