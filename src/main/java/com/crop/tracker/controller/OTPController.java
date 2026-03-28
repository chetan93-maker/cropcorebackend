package com.crop.tracker.controller;

import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.service.EmailService;
import com.crop.tracker.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/otp")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class OTPController {

    @Autowired
    private OTPService otpService;
    
    @Autowired
    private FarmerRepository farmerRepository;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String mobile = request.get("mobile");
            
            // Validate mobile number
            if (mobile == null || mobile.trim().isEmpty()) {
                response.put("error", "Mobile number is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (mobile.length() != 10 || !mobile.matches("\\d+")) {
                response.put("error", "Invalid mobile number. Please enter 10 digits.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check if farmer exists with this mobile
            FarmerEntity farmer = farmerRepository.findByMobile(mobile).orElse(null);
            
            if (farmer == null) {
                response.put("error", "Mobile number not registered");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // Generate OTP
            String otp = otpService.generateOTP(mobile);
            
            // Send OTP via email
            try {
                emailService.sendOTPEmail(farmer.getEmail(), otp, farmer.getName());
                System.out.println("✅ OTP sent to email: " + farmer.getEmail() + " for mobile: " + mobile);
            } catch (Exception e) {
                System.err.println("❌ Email sending failed: " + e.getMessage());
                // Continue even if email fails - OTP is still generated
            }
            
            // Success response
            response.put("success", true);
            response.put("message", "OTP sent successfully");
            response.put("otp", otp); // REMOVE THIS IN PRODUCTION
            response.put("mobile", mobile);
            response.put("email", maskEmail(farmer.getEmail()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", "Failed to send OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String mobile = request.get("mobile");
            String otp = request.get("otp");
            
            // Validate inputs
            if (mobile == null || otp == null) {
                response.put("error", "Mobile and OTP are required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verify OTP
            boolean isValid = otpService.verifyOTP(mobile, otp);
            
            if (isValid) {
                response.put("success", true);
                response.put("verified", true);
                response.put("message", "OTP verified successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("verified", false);
                response.put("error", "Invalid or expired OTP");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
        } catch (Exception e) {
            response.put("error", "Failed to verify OTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Helper method to mask email for privacy
    private String maskEmail(String email) {
        if (email == null) return null;
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return email;
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 2) return email;
        
        String maskedLocal = localPart.substring(0, 2) + 
                             "*".repeat(localPart.length() - 2);
        
        return maskedLocal + domain;
    }
}