package com.crop.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile number must be 10 digits starting with 6-9")
    private String mobile;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;  // Make sure this field exists
    
    private String location;

    // Default constructor
    public RegisterRequestDTO() {}

    // Constructor with all fields
    public RegisterRequestDTO(String name, String email, String mobile, String password, String location) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.location = location;
    }

    // Getters and Setters - MAKE SURE ALL HAVE GETTERS/SETTERS
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;  // THIS IS CRITICAL - must return password
    }

    public void setPassword(String password) {
        this.password = password;  // THIS IS CRITICAL - must set password
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RegisterRequestDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", location='" + location + '\'' +
                '}';  // Don't include password in toString for security
    }
}