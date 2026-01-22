package com.tn.server.dto.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PromptVariableCreateDto {
    private String keyName;
    private String variableName;
    private String description;
    private Integer orderIndex;
}