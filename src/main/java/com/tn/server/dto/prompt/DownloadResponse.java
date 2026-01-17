package com.tn.server.dto.prompt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadResponse {
    private String download_url;
}