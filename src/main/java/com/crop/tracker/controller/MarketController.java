package com.crop.tracker.controller;

import com.crop.tracker.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MarketController {

    @Autowired
    private MarketService marketService;

    /**
     * Get market prices for a specific commodity
     */
    @GetMapping("/prices")
    public ResponseEntity<?> getMarketPrices(
            @RequestParam(required = false) String commodity,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district) {
        
        try {
            List<Map<String, Object>> prices = marketService.getMarketPrices(commodity, state, district);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to fetch market prices: " + e.getMessage()));
        }
    }

    /**
     * Get all market prices (for dashboard)
     */
    @GetMapping("/prices/all")
    public ResponseEntity<?> getAllMarketPrices() {
        try {
            List<Map<String, Object>> prices = marketService.getAllMarketPrices();
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get market summary statistics
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getMarketSummary() {
        try {
            Map<String, Object> summary = marketService.getMarketSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get market trends (top gainers/losers)
     */
    @GetMapping("/trends")
    public ResponseEntity<?> getMarketTrends() {
        try {
            Map<String, Object> trends = marketService.getMarketTrends();
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get prices by state
     */
    @GetMapping("/prices/state/{state}")
    public ResponseEntity<?> getPricesByState(@PathVariable String state) {
        try {
            List<Map<String, Object>> prices = marketService.getMarketPrices(null, state, null);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get prices by commodity
     */
    @GetMapping("/prices/commodity/{commodity}")
    public ResponseEntity<?> getPricesByCommodity(@PathVariable String commodity) {
        try {
            List<Map<String, Object>> prices = marketService.getMarketPrices(commodity, null, null);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}