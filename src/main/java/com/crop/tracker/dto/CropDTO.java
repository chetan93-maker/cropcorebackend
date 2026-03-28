package com.crop.tracker.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.crop.tracker.entity.CropEntity;
import com.crop.tracker.entity.ExpenseEntity;

public class CropDTO {
    private Long id;
    private String cropName;
    private String season;
    private double income;
    private LocalDate createdDate;
    private Long farmerId;
    private String farmerName;
    private List<ExpenseSummaryDTO> expenses;
    private double totalExpenses;
    private double profit;

    public CropDTO() {}

    public CropDTO(CropEntity crop) {
        this.id = crop.getId();
        this.cropName = crop.getCropName();
        this.season = crop.getSeason();
        this.income = crop.getIncome();
        this.createdDate = crop.getCreatedDate();
        
        if (crop.getFarmer() != null) {
            this.farmerId = crop.getFarmer().getId();
            this.farmerName = crop.getFarmer().getName();
        }
        
        if (crop.getExpenses() != null && !crop.getExpenses().isEmpty()) {
            this.expenses = crop.getExpenses().stream()
                .map(ExpenseSummaryDTO::new)
                .collect(Collectors.toList());
            this.totalExpenses = crop.getExpenses().stream()
                .mapToDouble(ExpenseEntity::getAmount)
                .sum();
        }
        
        this.profit = this.income - this.totalExpenses;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String farmerName) { this.farmerName = farmerName; }

    public List<ExpenseSummaryDTO> getExpenses() { return expenses; }
    public void setExpenses(List<ExpenseSummaryDTO> expenses) { this.expenses = expenses; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }
}