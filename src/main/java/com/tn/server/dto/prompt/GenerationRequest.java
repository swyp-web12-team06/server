package com.tn.server.dto.prompt;

import lombok.Getter;
import java.util.List;

@Getter
public class GenerationRequest {
    private String quality; // HD, Standard 등
    private String aiModel;
    private List<VariableSelection> variable_values;

    @Getter
    public static class VariableSelection {
        private Long variable_id;
        private String value;
    }
}