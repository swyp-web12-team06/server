package com.tn.server.dto.library;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class LibrarySalesResponse {
    private Long prompt_id;
    private String title;
    private Integer price;
    private String status; // PENDING, APPROVED, REJECTED 등
    private Integer sales_count; // 누적 판매 횟수
    private Integer total_revenue; // 총 수익 (price * sales_count)
    private String created_at;
}