package com.redot.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PromptVariableCreateDto {
    private String keyName;
    private String description;
    private Integer orderIndex;
}