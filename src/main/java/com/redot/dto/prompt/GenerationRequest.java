package com.redot.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class GenerationRequest {

    private String resolution;

    @JsonProperty("aspect_ratio")
    private String aspectRatio;

    @Valid
    @JsonProperty("variable_values")
    private List<VariableSelection> variableValues;

    @Getter
    @NoArgsConstructor
    public static class VariableSelection {
        @NotNull(message = "변수 ID는 필수입니다.")
        @JsonProperty("variable_id")
        private Long variableId;

        @NotBlank(message = "변수 값은 비어있을 수 없습니다.")
        private String value;
    }
}