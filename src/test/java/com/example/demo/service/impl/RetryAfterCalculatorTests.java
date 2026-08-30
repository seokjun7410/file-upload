package com.example.demo.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.file.service.impl.RetryAfterCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RetryAfterCalculatorTests {

    private final RetryAfterCalculator calculator = new RetryAfterCalculator();

    @Test
    @DisplayName("처리 중 중복 관찰 횟수에 따른 Retry-After는 1초 이상 30초 이하이다")
    void calculatesCappedRetryAfter() {
        // given

        // when
        int retryAfter = calculator.calculate(100);

        // then
        assertThat(retryAfter).isBetween(1, 30);
    }
}
