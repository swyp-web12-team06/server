package com.redot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {
    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new TreeMap<>();
        response.put("server", "Redot Backend API Server");
        response.put("status", "RUNNING");
        response.put("profile", activeProfile); // 현재 실행 환경 표시 (local, prod 등)

        return ResponseEntity.ok(response);
    }
}
