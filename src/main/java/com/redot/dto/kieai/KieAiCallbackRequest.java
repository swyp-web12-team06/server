package com.redot.dto.kieai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KieAiCallbackRequest {
    private int code;
    private String msg;
    private CallbackData data;

    @Getter
    @NoArgsConstructor
    public static class CallbackData {
        private String taskId;
        private String state;

        @JsonProperty("resultJson")
        private String resultJson;

        private String failMsg;
        private String failCode;
        private Long completeTime;
    }
}