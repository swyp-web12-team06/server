package com.redot.controller;

import com.redot.dto.purchase.PurchaseResponse;
import com.redot.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/{promptId}/purchase")
    public ResponseEntity<PurchaseResponse> purchasePrompt(
            @PathVariable Long promptId,
            @RequestParam Long userId
    ) {
        PurchaseResponse response = purchaseService.purchasePrompt(userId, promptId);
        return ResponseEntity.ok(response);
    }
}