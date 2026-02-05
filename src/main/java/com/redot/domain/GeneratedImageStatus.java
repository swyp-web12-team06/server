package com.redot.domain;

import lombok.Getter;

@Getter
public enum GeneratedImageStatus {
    PROCESSING("생성 중"),
    COMPLETED("완료"),
    FAILED("실패");

    private final String description;

    GeneratedImageStatus(String description) {
        this.description = description;
    }
}