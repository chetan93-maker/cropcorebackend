package com.crop.tracker.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPService {

    // In-memory storage for OTPs (use Redis in production)
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();
    
    public String generateOTP(String mobile) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store OTP with 5 minute expiry (300,000 ms)
        otpStorage.put(mobile, new OTPData(otp, System.currentTimeMillis() + 300000));
        
        System.out.println("✅ OTP generated for " + mobile + ": " + otp);
        return otp;
    }
    
    public boolean verifyOTP(String mobile, String otp) {
        OTPData data = otpStorage.get(mobile);
        
        if (data == null) {
            System.out.println("❌ No OTP found for mobile: " + mobile);
            return false;
        }
        
        // Check if OTP expired
        if (System.currentTimeMillis() > data.expiryTime) {
            otpStorage.remove(mobile);
            System.out.println("❌ OTP expired for mobile: " + mobile);
            return false;
        }
        
        // Verify OTP
        if (data.otp.equals(otp)) {
            otpStorage.remove(mobile);
            System.out.println("✅ OTP verified for mobile: " + mobile);
            return true;
        }
        
        System.out.println("❌ Invalid OTP for mobile: " + mobile + ". Expected: " + data.otp + ", Received: " + otp);
        return false;
    }
    
    // Clean up expired OTPs (can be called by a scheduler)
    public void cleanupExpiredOTPs() {
        long now = System.currentTimeMillis();
        otpStorage.entrySet().removeIf(entry -> now > entry.getValue().expiryTime);
        System.out.println("🧹 Cleaned up expired OTPs");
    }
    
    // Inner class to store OTP with expiry
    private static class OTPData {
        String otp;
        long expiryTime;
        
        OTPData(String otp, long expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }
}