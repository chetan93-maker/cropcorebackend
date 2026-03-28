package com.crop.tracker.dto;

import java.time.LocalDate;

import com.crop.tracker.entity.ExpenseEntity;

public class ExpenseDTO {
    private Long id;
    private String type;
    private double amount;
    private LocalDate expenseDate;
    private Long cropId;
    private String cropName;

    public ExpenseDTO() {}

    public ExpenseDTO(ExpenseEntity expense) {
        this.id = expense.getId();
        this.type = expense.getType();
        this.amount = expense.getAmount();
        this.expenseDate = expense.getExpenseDate();
        
        if (expense.getCrop() != null) {
            this.cropId = expense.getCrop().getId();
            this.cropName = expense.getCrop().getCropName();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }
}