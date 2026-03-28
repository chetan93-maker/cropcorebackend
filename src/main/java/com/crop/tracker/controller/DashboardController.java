package com.crop.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crop.tracker.dto.DashboardSummary;
import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.service.DashboardService;
import com.crop.tracker.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin("*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;
    
    @Autowired
    private FarmerRepository farmerRepo;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/summary")
    public ResponseEntity<?> summary(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            // Get summary for this specific farmer
            DashboardSummary summary = dashboardService.getSummaryByFarmer(farmer.getId());
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired");
        }
    }
    
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}