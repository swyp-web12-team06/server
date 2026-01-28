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
    private String aiModel;

    private String aspect_ratio;

    @JsonProperty("ratio_x")
    private Integer ratioX;

    @JsonProperty("ratio_y")
    private Integer ratioY;

    @JsonProperty("variable_values")
    private List<VariableSelection> variableValues;

    @JsonIgnore
    public String getAspectRatio() {
        if (ratioX == null || ratioY == null) return "1:1";
        return ratioX + ":" + ratioY;
    }

    @Getter
    @NoArgsConstructor
    public static class VariableSelection {
        @JsonProperty("variable_id")
        private Long variableId;

        @JsonProperty("variable_name")
        private String variableName;

        private String value;
    }
}