package com.tn.server.dto.library;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class LibraryResponse {
    private Long purchase_id;
    private Long prompt_id;
    private String title;
    private Integer amount;
    private List<VariableInfo> variables;
    private List<ImageInfo> generated_images;
    private String purchased_at;

    @Getter
    @Builder
    public static class VariableInfo {
        private Long variable_id;
        private String value;
    }

    @Getter
    @Builder
    public static class ImageInfo {
        private Long id;
        private String image_url;
    }
}