package com.redot.dto.library;

import com.redot.domain.PromptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibrarySalesResponse {
    private Long prompt_id;
    private String title;
    private Integer price;
    private PromptStatus status;
    private String preview_image_url;
    private Integer sales_count;
    private Integer total_revenue;
    private String created_at;
}