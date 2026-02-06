package com.redot.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class PriceCheckRequest {

    @JsonProperty("variable_values")
    private List<GenerationRequest.VariableSelection> variableValues;

    private String resolution;

    @JsonProperty("ai_model")
    private String aiModel;
}