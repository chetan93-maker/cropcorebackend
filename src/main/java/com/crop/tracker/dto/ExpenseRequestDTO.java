package com.crop.tracker.dto;

import java.time.LocalDate;

public class ExpenseRequestDTO {
    private String type;
    private double amount;
    private LocalDate expenseDate;
    private Long cropId;

    // Default constructor
    public ExpenseRequestDTO() {}

    // Parameterized constructor
    public ExpenseRequestDTO(String type, double amount, LocalDate expenseDate, Long cropId) {
        this.type = type;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.cropId = cropId;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public Long getCropId() {
        return cropId;
    }

    public void setCropId(Long cropId) {
        this.cropId = cropId;
    }

    @Override
    public String toString() {
        return "ExpenseRequestDTO{" +
                "type='" + type + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", cropId=" + cropId +
                '}';
    }
}