package com.example.demo.file.service.impl;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Component;

/** 처리 중 중복 요청을 분산하기 위한 capped exponential backoff와 jitter를 계산한다. */
@Component
public class RetryAfterCalculator {

    private static final int MIN_SECONDS = 1;
    private static final int MAX_SECONDS = 30;

    /** 중복 관찰 횟수에 따라 1~30초 범위의 재시도 대기 시간을 계산한다. */
    public int calculate(int observationCount) {
        int exponent = Math.min(Math.max(observationCount, 0), 5);
        int upperBound = Math.min(MAX_SECONDS, 1 << exponent);
        return ThreadLocalRandom.current().nextInt(MIN_SECONDS, upperBound + 1);
    }
}
