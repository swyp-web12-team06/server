package com.redot.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModelOptionType {
    ASPECT_RATIO, // 비율
    RESOLUTION    // 해상도
}