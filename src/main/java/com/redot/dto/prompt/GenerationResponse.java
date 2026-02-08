package com.redot.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerationResponse {
    @JsonProperty("image_id")
    private Long imageId;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("total_price")
    private int totalPrice;

    @JsonProperty("current_credit")
    private Long currentCredit;

    @JsonProperty("task_id")
    private String taskId;
}