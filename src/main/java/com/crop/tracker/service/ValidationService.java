package com.crop.tracker.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class ValidationService {
    
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    
    public boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }
    
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public String sanitizeInput(String input) {
        if (input == null) return null;
        // Remove SQL injection patterns
        String sanitized = input.replaceAll("([';])", "");
        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 500);
        }
        return sanitized.trim();
    }
    
    public boolean isValidAmount(double amount) {
        return amount > 0 && amount < 10000000;
    }
}