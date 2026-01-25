package com.redot.dto.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadResponse {
    private String download_url;
}