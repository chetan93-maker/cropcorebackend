package com.crop.tracker.controller;

import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.security.JwtUtil;
import com.crop.tracker.service.ChatbotService;
import com.crop.tracker.service.MarketService;
import com.crop.tracker.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MarketService marketService;

    @Autowired
    private FarmerRepository farmerRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/ask")
    public ResponseEntity<?> askQuestion(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            // Log the incoming request for debugging
            System.out.println("=== CHAT REQUEST RECEIVED ===");
            System.out.println("Request keys: " + request.keySet());
            System.out.println("Full request: " + request);
            
            // Extract query parameter
            String query = request.get("query");
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Query is required"));
            }

            // Extract mobile from token for farmer context (optional)
            String mobile = null;
            try {
                mobile = extractMobileFromToken(httpRequest);
                System.out.println("Authenticated farmer mobile: " + mobile);
            } catch (Exception e) {
                System.out.println("No valid token provided, continuing without farmer context");
            }

            // Call Gemini API service
            String response = chatbotService.getChatResponse(query, mobile);
            
            System.out.println("Response sent: " + response.substring(0, Math.min(100, response.length())) + "...");
            
            return ResponseEntity.ok(Map.of("response", response));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/suggestions")
    public ResponseEntity<?> getSuggestions(HttpServletRequest httpRequest) {
        try {
            String mobile = extractMobileFromToken(httpRequest);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            String location = farmer.getLocation() != null ? farmer.getLocation() : "Mumbai";
            String weather = weatherService.getWeatherCondition(location);
            
            // Safely get market price with error handling
            double marketPrice = 2500.0; // Default value
            try {
                marketPrice = marketService.getCropPrice("wheat");
            } catch (Exception e) {
                System.out.println("Error fetching market price: " + e.getMessage());
            }
            
            var suggestions = chatbotService.getSuggestions(location, weather, marketPrice);
            
            return ResponseEntity.ok(Map.of("suggestions", suggestions));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/debug")
    public ResponseEntity<?> debugEndpoint(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        System.out.println("=== DEBUG ENDPOINT HIT ===");
        System.out.println("Request headers: ");
        java.util.Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println(headerName + ": " + httpRequest.getHeader(headerName));
        }
        
        System.out.println("Request body: " + request);
        System.out.println("Request keys: " + request.keySet());
        
        Map<String, Object> response = new HashMap<>();
        response.put("received", request);
        response.put("message", "Debug endpoint working");
        return ResponseEntity.ok(response);
    }

    // ========== HELPER METHOD ==========
    private String extractMobileFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return jwtUtil.extractMobile(token);
        }
        throw new RuntimeException("No token provided");
    }
}