package com.redot.dto.prompt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class GenerationRequest {

    @JsonProperty("prompt_text")
    private String promptText;

    private String resolution;

    @JsonProperty("ai_model")
    private String aiModel;

    @JsonProperty("aspect_ratio")
    private String aspectRatio;

    @JsonProperty("variable_values")
    private List<VariableSelection> variableValues;

    @Getter
    @NoArgsConstructor
    public static class VariableSelection {
        @JsonProperty("variable_id")
        private Long variableId;

        private String value;
    }
}