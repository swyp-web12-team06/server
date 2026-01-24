package com.tn.server.dto.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerationResponse {
    private Long image_id;
    private String image_url;
    private int total_price;
}