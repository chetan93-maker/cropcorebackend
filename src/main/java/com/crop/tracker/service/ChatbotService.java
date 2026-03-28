package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiBaseUrl;

    @Autowired(required = false)
    private MarketService marketService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Cacheable(value = "chatResponses", key = "#query")
    public String getChatResponse(String query, String mobile) {
        System.out.println("=== CHATBOT SERVICE ===");
        System.out.println("Query: " + query);
        System.out.println("Mobile: " + mobile);
        System.out.println("Gemini API Key present: " + (geminiApiKey != null && !geminiApiKey.isEmpty()));
        System.out.println("Gemini API Base URL: " + geminiApiBaseUrl);
        
        return callGeminiAI(query);
    }

    private String callGeminiAI(String query) {
        // CORRECT model names from your API response (they already include "models/")
        String[] models = {
            "models/gemini-2.5-flash",      // Latest flash model
            "models/gemini-2.5-pro",        // Latest pro model
            "models/gemini-2.0-flash",      // Stable flash model
            "models/gemini-2.0-flash-001",  // Specific version
            "models/gemini-2.0-flash-lite", // Lite version
        };
        
        for (String model : models) {
            try {
                System.out.println("\n--- Trying Gemini API with " + model + " ---");
                
                // Create request body for Gemini API
                Map<String, Object> requestBody = new HashMap<>();
                List<Map<String, Object>> contents = new ArrayList<>();
                Map<String, Object> content = new HashMap<>();
                List<Map<String, String>> parts = new ArrayList<>();
                Map<String, String> part = new HashMap<>();
                
                // Enhanced prompt for better responses in Hinglish
                String prompt = String.format(
                    "You are a helpful farming assistant for Indian farmers. " +
                    "Answer the following question in Hinglish (Hindi + English mix) so that farmers can easily understand. " +
                    "Provide detailed, practical advice with actionable steps. Use simple language.\n\n" +
                    "Question: %s\n\n" +
                    "Answer in a friendly, helpful manner:",
                    query
                );
                
                part.put("text", prompt);
                parts.add(part);
                content.put("parts", parts);
                contents.add(content);
                requestBody.put("contents", contents);

                // Add generation config for better responses
                Map<String, Object> generationConfig = new HashMap<>();
                generationConfig.put("temperature", 0.7);
                generationConfig.put("maxOutputTokens", 2048);
                generationConfig.put("topP", 0.95);
                generationConfig.put("topK", 40);
                requestBody.put("generationConfig", generationConfig);

                // Set headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                // FIXED: Build URL correctly - model already includes "models/" prefix
                String url = geminiApiBaseUrl + "/" + model + ":generateContent?key=" + geminiApiKey;
                System.out.println("Request URL: " + url.replace(geminiApiKey, "HIDDEN"));
                
                // Make API call
                ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
                );

                System.out.println("Response status: " + response.getStatusCode());
                
                // Parse response
                if (response.getBody() != null) {
                    Map<String, Object> responseBody = response.getBody();
                    
                    if (responseBody.containsKey("candidates")) {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
                        if (candidates != null && !candidates.isEmpty()) {
                            Map<String, Object> firstCandidate = candidates.get(0);
                            
                            // Check for finish reason (optional)
                            if (firstCandidate.containsKey("finishReason")) {
                                String finishReason = (String) firstCandidate.get("finishReason");
                                System.out.println("Finish reason: " + finishReason);
                            }
                            
                            Map<String, Object> content_response = (Map<String, Object>) firstCandidate.get("content");
                            if (content_response != null) {
                                List<Map<String, Object>> parts_response = (List<Map<String, Object>>) content_response.get("parts");
                                if (parts_response != null && !parts_response.isEmpty()) {
                                    String responseText = (String) parts_response.get(0).get("text");
                                    System.out.println("✅ SUCCESS with " + model);
                                    System.out.println("Response length: " + responseText.length() + " characters");
                                    return responseText;
                                }
                            }
                        }
                    }
                }
                
            } catch (Exception e) {
                System.out.println("❌ Failed with " + model + ": " + e.getMessage());
            }
        }
        
        // If all models fail, use fallback
        System.out.println("❌ All Gemini models failed, using fallback response");
        return getFallbackResponse(query);
    }

    private String getFallbackResponse(String query) {
        String q = query.toLowerCase();
        
        if (q.contains("aphid") || q.contains("माहू") || q.contains("एफिड")) {
            return "🌱 **Aphid Control in Hinglish:**\n\n" +
                   "माहू (एफिड) से बचाव के लिए:\n\n" +
                   "1. **नीम का तेल (Neem Oil)** - 5ml नीम तेल + 1 लीटर पानी में मिलाकर छिड़काव करें\n" +
                   "2. **लहसुन-मिर्च स्प्रे** - 10 लहसुन की कलियां + 5 हरी मिर्च पीसकर 1 लीटर पानी में मिलाएं\n" +
                   "3. **लेडीबर्ड (गुबरैला)** - इन कीटों को बगीचे में छोड़ें, ये माहू को खाते हैं\n" +
                   "4. **सह-फसल (Companion Planting)** - प्याज, लहसुन के साथ अन्य फसलें लगाएं\n\n" +
                   "नियमित निरीक्षण करें और शुरुआती दौर में ही उपाय करें।";
        }
        else if (q.contains("summer") && q.contains("crop")) {
            return "🌞 **Summer Crops in Hinglish:**\n\n" +
                   "• तरबूज (Watermelon), खरबूज (Muskmelon)\n" +
                   "• खीरा (Cucumber), करेला (Bitter Gourd)\n" +
                   "• मक्का (Maize), मूंगफली (Groundnut)\n" +
                   "• कपास (Cotton), सूरजमुखी (Sunflower)\n\n" +
                   "💧 पानी की उचित व्यवस्था करें और मल्चिंग का उपयोग करें।";
        }
        else if (q.contains("winter") && q.contains("crop")) {
            return "❄️ **Winter Crops in Hinglish:**\n\n" +
                   "• गेहूं (Wheat), जौ (Barley)\n" +
                   "• सरसों (Mustard), चना (Gram)\n" +
                   "• मटर (Peas), आलू (Potato)\n" +
                   "• प्याज (Onion), लहसुन (Garlic)\n\n" +
                   "📅 अक्टूबर-नवंबर में बुवाई करें और पाले से बचाव करें।";
        }
        else if (q.contains("monsoon") && q.contains("crop")) {
            return "☔ **Monsoon Crops in Hinglish:**\n\n" +
                   "• धान (Rice), मक्का (Maize)\n" +
                   "• कपास (Cotton), गन्ना (Sugarcane)\n" +
                   "• हल्दी (Turmeric), अदरक (Ginger)\n" +
                   "• सोयाबीन (Soybean), मूंगफली (Groundnut)\n\n" +
                   "⚠️ जल निकासी का उचित प्रबंधन करें।";
        }
        else if (q.contains("fertilizer") && q.contains("cotton")) {
            return "🧪 **Cotton Fertilizer in Hinglish:**\n\n" +
                   "• **नाइट्रोजन (N):** 120-150 kg/ha (तीन हिस्सों में - बुवाई, फूल आने, बॉल बनने के समय)\n" +
                   "• **फास्फोरस (P):** 60-80 kg/ha\n" +
                   "• **पोटाश (K):** 60-80 kg/ha\n\n" +
                   "मिट्टी की जांच करवाकर सही मात्रा का प्रयोग करें।";
        }
        else if (q.contains("fertilizer") && q.contains("wheat")) {
            return "🌾 **Wheat Fertilizer in Hinglish:**\n\n" +
                   "• **नाइट्रोजन (N):** 120-150 kg/ha (आधा बुवाई के समय, आधा पहली सिंचाई पर)\n" +
                   "• **फास्फोरस (P):** 60-80 kg/ha\n" +
                   "• **पोटाश (K):** 40-50 kg/ha";
        }
        else if (q.contains("increase yield") || q.contains("पैदावार")) {
            return "📈 **Increase Crop Yield in Hinglish:**\n\n" +
                   "1. उन्नत किस्म के बीज use करें\n" +
                   "2. मिट्टी की जांच करवाएं\n" +
                   "3. फसल चक्र अपनाएं\n" +
                   "4. कीट प्रबंधन करें\n" +
                   "5. सिंचाई का उचित प्रबंधन करें\n" +
                   "6. खरपतवार निकालते रहें\n" +
                   "7. समय पर खाद डालें\n" +
                   "8. फसल सुरक्षा पर ध्यान दें";
        }
        else if (q.contains("organic")) {
            return "🌱 **Organic Farming Tips in Hinglish:**\n\n" +
                   "• कम्पोस्ट और गोबर की खाद use करें\n" +
                   "• फसल चक्र अपनाएं\n" +
                   "• हरी खाद का प्रयोग करें\n" +
                   "• नीम आधारित कीटनाशक use करें\n" +
                   "• लाभदायक कीटों को प्रोत्साहित करें\n" +
                   "• मल्चिंग करें";
        }
        else if (q.contains("irrigation") || q.contains("पानी") || q.contains("सिंचाई")) {
            return "💧 **Water Management Tips in Hinglish:**\n\n" +
                   "• ड्रिप सिंचाई से 90% पानी बचता है\n" +
                   "• स्प्रिंकलर से 75% पानी बचता है\n" +
                   "• सुबह जल्दी पानी दें\n" +
                   "• मल्चिंग से पानी कम लगता है\n" +
                   "• वर्षा जल संचयन करें";
        }
        else {
            return "🤗 **नमस्ते! मैं आपका किसान सहायक हूं।**\n\n" +
                   "कृपया अपना सवाल पूछें:\n" +
                   "🌾 फसलों के बारे में (Crops)\n" +
                   "🧪 खाद और उर्वरक के बारे में (Fertilizers)\n" +
                   "🐛 कीट नियंत्रण के बारे में (Pest Control)\n" +
                   "🌦️ मौसम के बारे में (Weather)\n" +
                   "💧 सिंचाई के बारे में (Irrigation)\n\n" +
                   "**Example Questions:**\n" +
                   "• गर्मी में कौन सी फसल लगाएं?\n" +
                   "• कपास में खाद कैसे डालें?\n" +
                   "• माहू कीट से कैसे बचें?\n" +
                   "• गेहूं की पैदावार कैसे बढ़ाएं?\n" +
                   "• धान की सिंचाई कैसे करें?";
        }
    }

    public List<String> getSuggestions(String location, String weather, double marketPrice) {
        List<String> suggestions = new ArrayList<>();
        
        // Weather-based suggestions in Hinglish
        if (weather != null) {
            if (weather.contains("rain") || weather.equalsIgnoreCase("rain")) {
                suggestions.add("🌧️ बारिश की संभावना - कीटनाशक का छिड़काव टालें और जल निकासी की जांच करें");
            } else if (weather.contains("hot") || weather.contains("35")) {
                suggestions.add("🌡️ तापमान अधिक है - फसलों की सिंचाई सुनिश्चित करें");
            } else if (weather.contains("clear") || weather.contains("sun")) {
                suggestions.add("☀️ मौसम साफ है - कटाई के लिए अच्छा दिन");
            } else if (weather.contains("cloud")) {
                suggestions.add("☁️ बादल छाए हुए हैं - नर्सरी लगाने का सही समय");
            }
        }
        
        // Market price-based suggestions
        if (marketPrice > 3000) {
            suggestions.add("📈 गेहूं के भाव ऊंचे हैं - बिक्री पर विचार करें");
        } else if (marketPrice > 2500) {
            suggestions.add("📊 गेहूं के भाव स्थिर हैं - बिक्री का अच्छा समय");
        } else if (marketPrice > 2000) {
            suggestions.add("📉 गेहूं के भाव कम हैं - कुछ दिन प्रतीक्षा करें");
        }
        
        // General farming suggestions
        suggestions.add("🌱 नियमित रूप से फसल निरीक्षण करें");
        suggestions.add("🧪 मिट्टी की जांच हर साल करवाएं");
        
        return suggestions;
    }
}