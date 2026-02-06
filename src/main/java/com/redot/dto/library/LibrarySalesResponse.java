package com.redot.dto.library;

import com.redot.domain.Prompt;
import com.redot.domain.PromptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibrarySalesResponse {
    private Long prompt_id;
    private String title;
    private Integer price;
    private PromptStatus status;
    private Integer sales_count;
    private Integer total_revenue;
    private String created_at;

    public static LibrarySalesResponse from(Prompt prompt) {
        return LibrarySalesResponse.builder()
                .prompt_id(prompt.getId())
                .title(prompt.getTitle())
                .price(prompt.getPrice())
                .status(prompt.getStatus())
                .sales_count(0)
                .total_revenue(0)
                .created_at(prompt.getCreatedAt() != null ?
                        prompt.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .build();
    }
}