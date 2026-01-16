package com.tn.server.dto.prompt;

import lombok.Getter;
import java.util.List;
import java.util.Map;
@Getter
public class PreviewRequest {
    private List<VariableSelection> variable_values;

    @Getter
    public static class VariableSelection {
        private Long variable_id;
        private String value;
    }
}