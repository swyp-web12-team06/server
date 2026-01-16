package com.tn.server.controller;

import com.tn.server.dto.prompt.PreviewRequest;
import com.tn.server.dto.prompt.PreviewResponse;
import com.tn.server.service.PreviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prompts")
public class PreviewController {

    private final PreviewService previewService;

    @PostMapping("/{promptId}/preview")
    public ResponseEntity<PreviewResponse> getPreview(
            @PathVariable Long promptId,
            @RequestBody PreviewRequest request
    ) {
        return ResponseEntity.ok(previewService.getPreviewImage(promptId, request));
    }
}