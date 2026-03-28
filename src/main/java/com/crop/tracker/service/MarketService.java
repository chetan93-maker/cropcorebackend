package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MarketService {

    @Value("${market.api.key:demo}")
    private String apiKey;

    @Value("${market.api.url:https://api.data.gov.in/resource/9ef84268-d588-465a-a308-a864a43d0070}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Safe number conversion methods
    private Double safeConvertToDouble(Object value) {
        if (value == null) return 0.0;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private Integer safeConvertToInteger(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Get market prices for a specific commodity
     * @param commodity Crop name (e.g., "wheat", "rice", "cotton")
     * @param state Optional state filter
     * @param district Optional district filter
     * @return List of market price records
     */
    @Cacheable(value = "marketPrices", key = "#commodity + '-' + #state + '-' + #district")
    public List<Map<String, Object>> getMarketPrices(String commodity, String state, String district) {
        try {
            // Build URL with parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("api-key", apiKey)
                    .queryParam("format", "json")
                    .queryParam("limit", 100);
            
            // Add filters if provided
            if (commodity != null && !commodity.isEmpty()) {
                builder.queryParam("filters[commodity]", commodity);
            }
            if (state != null && !state.isEmpty()) {
                builder.queryParam("filters[state]", state);
            }
            if (district != null && !district.isEmpty()) {
                builder.queryParam("filters[district]", district);
            }

            String url = builder.build().toString();
            System.out.println("Fetching market data from: " + url.replace(apiKey, "HIDDEN"));

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("records")) {
                List<Map<String, Object>> records = (List<Map<String, Object>>) response.getBody().get("records");
                return parseMarketRecords(records);
            }

            return getMockMarketData(commodity);

        } catch (Exception e) {
            System.err.println("Error fetching market data: " + e.getMessage());
            e.printStackTrace();
            return getMockMarketData(commodity);
        }
    }

    /**
     * Parse API response records
     */
    private List<Map<String, Object>> parseMarketRecords(List<Map<String, Object>> records) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> record : records) {
            Map<String, Object> marketInfo = new HashMap<>();

            // Extract fields from API response
            marketInfo.put("commodity", record.get("commodity"));
            marketInfo.put("market", record.get("market"));
            marketInfo.put("district", record.get("district"));
            marketInfo.put("state", record.get("state"));

            // Price information
            marketInfo.put("min_price", safeConvertToDouble(record.get("min_price")));
            marketInfo.put("max_price", safeConvertToDouble(record.get("max_price")));
            marketInfo.put("modal_price", safeConvertToDouble(record.get("modal_price")));

            // Calculate change (mock for now - API may not provide this)
            marketInfo.put("change", calculatePriceChange(marketInfo));

            // Date information
            marketInfo.put("arrival_date", record.get("arrival_date"));
            marketInfo.put("timestamp", System.currentTimeMillis());

            result.add(marketInfo);
        }

        return result;
    }

    /**
     * Calculate price change (mock implementation)
     */
    private String calculatePriceChange(Map<String, Object> marketInfo) {
        double price = (double) marketInfo.getOrDefault("modal_price", 0.0);
        // Random change between -5% and +8%
        double change = (Math.random() * 13) - 5;
        String sign = change >= 0 ? "+" : "";
        return String.format("%s%.1f%%", sign, change);
    }

    /**
     * Get market summary statistics
     */
    public Map<String, Object> getMarketSummary() {
        List<Map<String, Object>> allPrices = getAllMarketPrices();
        
        double highest = 0;
        double lowest = Double.MAX_VALUE;
        double sum = 0;
        int count = 0;

        for (Map<String, Object> price : allPrices) {
            double modalPrice = (double) price.getOrDefault("modal_price", 0.0);
            if (modalPrice > 0) {
                highest = Math.max(highest, modalPrice);
                lowest = Math.min(lowest, modalPrice);
                sum += modalPrice;
                count++;
            }
        }

        double average = count > 0 ? sum / count : 0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("highest", highest);
        summary.put("lowest", lowest == Double.MAX_VALUE ? 0 : lowest);
        summary.put("average", Math.round(average));
        summary.put("totalMarkets", allPrices.size());
        summary.put("lastUpdated", System.currentTimeMillis());

        return summary;
    }

    /**
     * Get all market prices (for dashboard)
     */
    public List<Map<String, Object>> getAllMarketPrices() {
        return getMarketPrices(null, null, null);
    }

    /**
     * Get average price for a specific crop
     * @param crop The crop name (e.g., "wheat", "rice", "cotton")
     * @return Average modal price for the crop
     */
    public double getCropPrice(String crop) {
        try {
            List<Map<String, Object>> prices = getMarketPrices(crop, null, null);
            
            if (prices.isEmpty()) {
                return 2500.0; // Default price if no data
            }
            
            // Calculate average modal price
            double sum = 0;
            int count = 0;
            
            for (Map<String, Object> price : prices) {
                Object modalPrice = price.get("modal_price");
                if (modalPrice instanceof Number) {
                    sum += ((Number) modalPrice).doubleValue();
                    count++;
                } else if (modalPrice instanceof String) {
                    try {
                        sum += Double.parseDouble((String) modalPrice);
                        count++;
                    } catch (NumberFormatException e) {
                        // Skip invalid numbers
                    }
                }
            }
            
            if (count > 0) {
                return sum / count;
            }
            
            return getDefaultPriceForCrop(crop);
            
        } catch (Exception e) {
            System.err.println("Error getting crop price for " + crop + ": " + e.getMessage());
            return getDefaultPriceForCrop(crop);
        }
    }

    /**
     * Get default price for common crops
     */
    private double getDefaultPriceForCrop(String crop) {
        if (crop == null) return 2500.0;
        
        switch (crop.toLowerCase()) {
            case "wheat":
                return 2750.0;
            case "rice":
                return 3200.0;
            case "cotton":
                return 8200.0;
            case "sugarcane":
                return 350.0;
            case "maize":
                return 2100.0;
            case "potato":
                return 1850.0;
            case "onion":
                return 2450.0;
            case "tomato":
                return 1650.0;
            case "groundnut":
                return 5600.0;
            case "mustard":
                return 4850.0;
            default:
                return 2500.0;
        }
    }

    /**
     * Get top gainers and losers
     */
    public Map<String, Object> getMarketTrends() {
        List<Map<String, Object>> allPrices = getAllMarketPrices();
        
        List<Map<String, Object>> gainers = new ArrayList<>();
        List<Map<String, Object>> losers = new ArrayList<>();

        for (Map<String, Object> price : allPrices) {
            String changeStr = (String) price.get("change");
            if (changeStr != null && changeStr.endsWith("%")) {
                try {
                    double change = Double.parseDouble(changeStr.replace("%", "").replace("+", ""));
                    if (change > 2.0) {
                        gainers.add(price);
                    } else if (change < -1.0) {
                        losers.add(price);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid change values
                }
            }
        }

        // Sort and limit to top 5
        gainers.sort((a, b) -> {
            String ca = (String) a.get("change");
            String cb = (String) b.get("change");
            try {
                return Double.compare(
                    Double.parseDouble(cb.replace("%", "").replace("+", "")),
                    Double.parseDouble(ca.replace("%", "").replace("+", ""))
                );
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        losers.sort((a, b) -> {
            String ca = (String) a.get("change");
            String cb = (String) b.get("change");
            try {
                return Double.compare(
                    Double.parseDouble(ca.replace("%", "").replace("+", "")),
                    Double.parseDouble(cb.replace("%", "").replace("+", ""))
                );
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        Map<String, Object> trends = new HashMap<>();
        trends.put("gainers", gainers.stream().limit(5).collect(Collectors.toList()));
        trends.put("losers", losers.stream().limit(5).collect(Collectors.toList()));
        
        return trends;
    }

    /**
     * Mock data for when API is unavailable
     */
    private List<Map<String, Object>> getMockMarketData(String commodity) {
        List<Map<String, Object>> mockData = new ArrayList<>();

        if (commodity == null || commodity.isEmpty() || "wheat".equalsIgnoreCase(commodity)) {
            mockData.add(createMockRecord("Wheat", "Azadpur Mandi", "Delhi", "Delhi", 2750.0, 2850.0, 2800.0, "+5.2%"));
            mockData.add(createMockRecord("Wheat", "Khanna Mandi", "Ludhiana", "Punjab", 2680.0, 2780.0, 2730.0, "+4.8%"));
            mockData.add(createMockRecord("Wheat", "Agra Mandi", "Agra", "UP", 2590.0, 2690.0, 2640.0, "+3.9%"));
        }

        if (commodity == null || commodity.isEmpty() || "rice".equalsIgnoreCase(commodity)) {
            mockData.add(createMockRecord("Rice", "Koyambedu", "Chennai", "Tamil Nadu", 3200.0, 3400.0, 3300.0, "+3.1%"));
            mockData.add(createMockRecord("Rice", "Raipur Mandi", "Raipur", "CG", 3150.0, 3350.0, 3250.0, "+2.8%"));
        }

        if (commodity == null || commodity.isEmpty() || "cotton".equalsIgnoreCase(commodity)) {
            mockData.add(createMockRecord("Cotton", "Guntur", "Guntur", "AP", 8200.0, 8400.0, 8300.0, "-2.3%"));
            mockData.add(createMockRecord("Cotton", "Rajkot", "Rajkot", "Gujarat", 8150.0, 8350.0, 8250.0, "-1.9%"));
        }

        if (commodity == null || commodity.isEmpty() || "onion".equalsIgnoreCase(commodity)) {
            mockData.add(createMockRecord("Onion", "Lasalgaon", "Nashik", "Maharashtra", 2450.0, 2650.0, 2550.0, "+8.3%"));
        }

        if (commodity == null || commodity.isEmpty() || "potato".equalsIgnoreCase(commodity)) {
            mockData.add(createMockRecord("Potato", "Agra", "Agra", "UP", 1850.0, 2050.0, 1950.0, "-1.5%"));
        }

        return mockData;
    }

    private Map<String, Object> createMockRecord(String commodity, String market, String district, 
                                                String state, double min, double max, double modal, String change) {
        Map<String, Object> record = new HashMap<>();
        record.put("commodity", commodity);
        record.put("market", market);
        record.put("district", district);
        record.put("state", state);
        record.put("min_price", min);
        record.put("max_price", max);
        record.put("modal_price", modal);
        record.put("change", change);
        record.put("arrival_date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        record.put("timestamp", System.currentTimeMillis());
        return record;
    }
}