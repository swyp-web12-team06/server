package com.redot.dto.library;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLibraryResponse {
    private Long prompt_id;
    private String title;
    private Integer price;
    private String preview_image_url;
    private String created_at;
}
