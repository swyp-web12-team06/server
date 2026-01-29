package com.redot.dto.prompt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadResponse {
    @JsonProperty("download_url")
    private String downloadUrl;
}