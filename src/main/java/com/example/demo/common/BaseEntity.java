package com.example.demo.common;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

/** 모든 JPA 엔티티가 공통으로 사용하는 식별자와 생성·수정 시각을 제공한다. */
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 엔티티가 처음 저장된 시각이다. 최초 저장 이후에는 변경되지 않는다. */
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** 엔티티가 마지막으로 저장 변경된 시각이다. */
    @Column
    private LocalDateTime updatedAt;

    /** 엔티티 최초 저장 직전에 생성·수정 시각을 모두 기록한다. */
    @PrePersist
    private void recordCreationTime() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /** 엔티티 변경 사항이 저장되기 직전에 마지막 수정 시각을 갱신한다. */
    @PreUpdate
    private void recordUpdateTime() {
        this.updatedAt = LocalDateTime.now();
    }
}
