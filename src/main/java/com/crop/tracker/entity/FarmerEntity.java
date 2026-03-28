package com.crop.tracker.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "farmers")
public class FarmerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false, length = 10)
    private String mobile;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // ============ NEW FIELDS FOR ENHANCED FEATURES ============

    // Location for weather and market data
    private String location;  // City name for weather
    private String state;     // State for market data
    private String district;  // District for localized advice
    private String village;   // Village name
    
    // Farm details
    private Double farmSize;  // Farm size in acres
    private String soilType;  // Soil type (clay, loamy, sandy, etc.)
    private String irrigationSource; // Well, canal, borewell, etc.
    
    // Preferred crops (stored as JSON string)
    @Column(length = 1000)
    private String preferredCrops; // JSON array of crop names
    
    // Chatbot preferences
    private Boolean chatbotEnabled = true;
    private String chatLanguage = "en"; // en, hi, mr, ta, te, etc.
    private Integer chatContextDays = 30; // Keep chat history for 30 days
    
    // Notification preferences
    private Boolean weatherAlerts = true;
    private Boolean marketAlerts = true;
    private Boolean cropReminders = true;
    
    // Chat history count
    private Integer totalChats = 0;
    private LocalDateTime lastChatAt;
    
    // ============ EXISTING RELATIONSHIPS ============

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<CropEntity> crops = new ArrayList<>();

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatHistoryEntity> chatHistory = new ArrayList<>();

    // ============ TIMESTAMP FIELDS ============

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    // ============ ACCOUNT STATUS ============

    private Boolean isActive = true;
    private Boolean isVerified = false;
    private String verificationToken;
    private LocalDateTime verifiedAt;

    // ============ CONSTRUCTORS ============

    public FarmerEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FarmerEntity(String name, String email, String mobile, String password) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isVerified = false;
        this.chatbotEnabled = true;
        this.chatLanguage = "en";
        this.chatContextDays = 30;
    }

    // ============ HELPER METHODS ============

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void incrementChatCount() {
        this.totalChats = (this.totalChats == null ? 0 : this.totalChats) + 1;
        this.lastChatAt = LocalDateTime.now();
    }

    // Add preferred crop
    public void addPreferredCrop(String crop) {
        if (this.preferredCrops == null || this.preferredCrops.isEmpty()) {
            this.preferredCrops = "[\"" + crop + "\"]";
        } else {
            // Remove closing bracket and add new crop
            String crops = this.preferredCrops.substring(0, this.preferredCrops.length() - 1);
            this.preferredCrops = crops + ",\"" + crop + "\"]";
        }
    }

    // Get preferred crops as array
    public String[] getPreferredCropsArray() {
        if (this.preferredCrops == null || this.preferredCrops.isEmpty()) {
            return new String[0];
        }
        // Simple parsing - in production use proper JSON parser
        return this.preferredCrops
            .replace("[", "")
            .replace("]", "")
            .replace("\"", "")
            .split(",");
    }

    // Get full location string
    public String getFullLocation() {
        StringBuilder full = new StringBuilder();
        if (village != null && !village.isEmpty()) full.append(village).append(", ");
        if (district != null && !district.isEmpty()) full.append(district).append(", ");
        if (state != null && !state.isEmpty()) full.append(state);
        if (location != null && !location.isEmpty() && full.length() == 0) full.append(location);
        return full.toString();
    }

    // ============ GETTERS AND SETTERS ============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public Double getFarmSize() {
        return farmSize;
    }

    public void setFarmSize(Double farmSize) {
        this.farmSize = farmSize;
    }

    public String getSoilType() {
        return soilType;
    }

    public void setSoilType(String soilType) {
        this.soilType = soilType;
    }

    public String getIrrigationSource() {
        return irrigationSource;
    }

    public void setIrrigationSource(String irrigationSource) {
        this.irrigationSource = irrigationSource;
    }

    public String getPreferredCrops() {
        return preferredCrops;
    }

    public void setPreferredCrops(String preferredCrops) {
        this.preferredCrops = preferredCrops;
    }

    public Boolean getChatbotEnabled() {
        return chatbotEnabled;
    }

    public void setChatbotEnabled(Boolean chatbotEnabled) {
        this.chatbotEnabled = chatbotEnabled;
    }

    public String getChatLanguage() {
        return chatLanguage;
    }

    public void setChatLanguage(String chatLanguage) {
        this.chatLanguage = chatLanguage;
    }

    public Integer getChatContextDays() {
        return chatContextDays;
    }

    public void setChatContextDays(Integer chatContextDays) {
        this.chatContextDays = chatContextDays;
    }

    public Boolean getWeatherAlerts() {
        return weatherAlerts;
    }

    public void setWeatherAlerts(Boolean weatherAlerts) {
        this.weatherAlerts = weatherAlerts;
    }

    public Boolean getMarketAlerts() {
        return marketAlerts;
    }

    public void setMarketAlerts(Boolean marketAlerts) {
        this.marketAlerts = marketAlerts;
    }

    public Boolean getCropReminders() {
        return cropReminders;
    }

    public void setCropReminders(Boolean cropReminders) {
        this.cropReminders = cropReminders;
    }

    public Integer getTotalChats() {
        return totalChats;
    }

    public void setTotalChats(Integer totalChats) {
        this.totalChats = totalChats;
    }

    public LocalDateTime getLastChatAt() {
        return lastChatAt;
    }

    public void setLastChatAt(LocalDateTime lastChatAt) {
        this.lastChatAt = lastChatAt;
    }

    public List<CropEntity> getCrops() {
        return crops;
    }

    public void setCrops(List<CropEntity> crops) {
        this.crops = crops;
    }

    public List<ChatHistoryEntity> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChatHistoryEntity> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    // ============ TOSTRING METHOD ============

    @Override
    public String toString() {
        return "FarmerEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", location='" + location + '\'' +
                ", state='" + state + '\'' +
                ", farmSize=" + farmSize +
                ", soilType='" + soilType + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                '}';
    }
}