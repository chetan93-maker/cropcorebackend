package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    public void sendOTPEmail(String to, String otp, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("🔐 CropCore - Password Reset OTP");
            
            String text = String.format(
                "Hello %s,\n\n" +
                "Your OTP for password reset is: %s\n\n" +
                "This OTP is valid for 5 minutes.\n\n" +
                "If you didn't request this, please ignore this email.\n\n" +
                "Thank you,\n" +
                "CropCore Team",
                name, otp
            );
            
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("✅ OTP Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send OTP email: " + e.getMessage());
            // Don't throw exception to avoid breaking the flow
        }
    }
    
    // ADD THIS METHOD - Welcome email on registration
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("🌾 Welcome to CropCore!");
            
            String text = String.format(
                "Hello %s,\n\n" +
                "Welcome to CropCore! We're excited to have you on board.\n\n" +
                "With CropCore, you can:\n" +
                "✓ Track your crops and seasons\n" +
                "✓ Manage expenses efficiently\n" +
                "✓ Analyze profits in real-time\n" +
                "✓ Plan your farming activities\n\n" +
                "Get started by adding your first crop!\n\n" +
                "If you have any questions, feel free to contact our support team.\n\n" +
                "Happy Farming!\n" +
                "The CropCore Team\n" +
                "🌱 Growing Together",
                name
            );
            
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("✅ Welcome Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            // Don't throw exception - registration should succeed even if email fails
        }
    }
    
    // Optional: Add method for password change notification
    public void sendPasswordChangeNotification(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("🔐 CropCore - Password Changed Successfully");
            
            String text = String.format(
                "Hello %s,\n\n" +
                "Your CropCore account password has been successfully changed.\n\n" +
                "If you did not make this change, please contact our support team immediately.\n\n" +
                "Thank you,\n" +
                "CropCore Team",
                name
            );
            
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("✅ Password change notification sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password change notification: " + e.getMessage());
        }
    }
}