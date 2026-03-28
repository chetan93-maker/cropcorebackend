package com.crop.tracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("service", "CropTracker API");
        status.put("version", "1.0.0");
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/health/db")
    public ResponseEntity<Map<String, Object>> dbHealth() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("database", "MySQL");
        status.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(status);
    }
}