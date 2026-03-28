package com.crop.tracker.dto;

public class LoginResponseDTO {
    private String message;
    private String token;
    private Long farmerId;
    private String farmerName;
    private String email;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String message, String token, Long farmerId, 
                           String farmerName, String email) {
        this.message = message;
        this.token = token;
        this.farmerId = farmerId;
        this.farmerName = farmerName;
        this.email = email;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}