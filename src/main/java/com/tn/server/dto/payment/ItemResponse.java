package com.tn.server.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemResponse {
    private String id;
    private String name;
    private Integer price;
    private String currency;
}
