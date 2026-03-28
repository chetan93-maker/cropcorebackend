package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    // Helper method to safely convert any Number to Double
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

    // Helper method to safely convert any Number to Integer
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

    @Cacheable(value = "weather", key = "#city")
    public Map<String, Object> getWeather(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build()
                .toString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return convertWeatherData(response);
        } catch (Exception e) {
            e.printStackTrace();
            return getMockWeather(city);
        }
    }

    @Cacheable(value = "forecast", key = "#city")
    public Map<String, Object> getForecast(String city) {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl + "/forecast")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("cnt", "5")
                .build()
                .toString();

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return convertForecastData(response);
        } catch (Exception e) {
            e.printStackTrace();
            return getMockForecast(city);
        }
    }

    private Map<String, Object> convertWeatherData(Map<String, Object> data) {
        if (data == null) return null;
        
        // Convert numeric values safely
        Map<String, Object> main = (Map<String, Object>) data.get("main");
        if (main != null) {
            main.put("temp", safeConvertToDouble(main.get("temp")));
            main.put("feels_like", safeConvertToDouble(main.get("feels_like")));
            main.put("temp_min", safeConvertToDouble(main.get("temp_min")));
            main.put("temp_max", safeConvertToDouble(main.get("temp_max")));
            main.put("pressure", safeConvertToInteger(main.get("pressure")));
            main.put("humidity", safeConvertToInteger(main.get("humidity")));
        }
        
        Map<String, Object> wind = (Map<String, Object>) data.get("wind");
        if (wind != null) {
            wind.put("speed", safeConvertToDouble(wind.get("speed")));
            wind.put("deg", safeConvertToInteger(wind.get("deg")));
        }
        
        return data;
    }

    private Map<String, Object> convertForecastData(Map<String, Object> data) {
        if (data == null) return null;
        
        java.util.List<Map<String, Object>> list = (java.util.List<Map<String, Object>>) data.get("list");
        if (list != null) {
            for (Map<String, Object> item : list) {
                Map<String, Object> main = (Map<String, Object>) item.get("main");
                if (main != null) {
                    main.put("temp", safeConvertToDouble(main.get("temp")));
                }
            }
        }
        
        return data;
    }

    public String getWeatherCondition(String city) {
        try {
            Map<String, Object> weather = getWeather(city);
            Map<String, Object> main = (Map<String, Object>) weather.get("main");
            double temp = safeConvertToDouble(main.get("temp"));
            
            java.util.List<Map<String, Object>> weatherList = 
                (java.util.List<Map<String, Object>>) weather.get("weather");
            String condition = (String) weatherList.get(0).get("main");
            
            if (temp > 35) return "hot";
            if (condition.equalsIgnoreCase("Rain")) return "rain";
            if (temp > 25 && condition.equalsIgnoreCase("Clear")) return "clear";
            return "moderate";
            
        } catch (Exception e) {
            return "moderate";
        }
    }

    private Map<String, Object> getMockWeather(String city) {
        return Map.of(
            "name", city,
            "main", Map.of(
                "temp", 28.5,
                "feels_like", 30.2,
                "humidity", 75,
                "pressure", 1012
            ),
            "weather", java.util.List.of(Map.of(
                "main", "Clear",
                "description", "clear sky",
                "icon", "01d"
            )),
            "wind", Map.of("speed", 3.6),
            "sys", Map.of("country", "IN")
        );
    }

    private Map<String, Object> getMockForecast(String city) {
        return Map.of(
            "list", java.util.List.of(
                Map.of("dt", System.currentTimeMillis() + 86400000, "main", Map.of("temp", 29.0)),
                Map.of("dt", System.currentTimeMillis() + 172800000, "main", Map.of("temp", 30.0)),
                Map.of("dt", System.currentTimeMillis() + 259200000, "main", Map.of("temp", 28.0)),
                Map.of("dt", System.currentTimeMillis() + 345600000, "main", Map.of("temp", 27.0)),
                Map.of("dt", System.currentTimeMillis() + 432000000, "main", Map.of("temp", 31.0))
            )
        );
    }
}