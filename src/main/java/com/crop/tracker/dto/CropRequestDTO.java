package com.crop.tracker.dto;

import java.time.LocalDate;

public class CropRequestDTO {
    private String cropName;
    private String season;
    private double income;
    private LocalDate createdDate;
    private Long farmerId;

    // Getters and Setters
    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public double getIncome() { return income; }
    public void setIncome(double income) { this.income = income; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public Long getFarmerId() { return farmerId; }
    public void setFarmerId(Long farmerId) { this.farmerId = farmerId; }
}