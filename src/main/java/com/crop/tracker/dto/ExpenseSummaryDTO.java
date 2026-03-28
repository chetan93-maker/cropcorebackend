package com.crop.tracker.dto;

import java.time.LocalDate;

import com.crop.tracker.entity.ExpenseEntity;

public class ExpenseSummaryDTO {
    private Long id;
    private String type;
    private double amount;
    private LocalDate expenseDate;

    public ExpenseSummaryDTO() {}

    public ExpenseSummaryDTO(ExpenseEntity expense) {
        this.id = expense.getId();
        this.type = expense.getType();
        this.amount = expense.getAmount();
        this.expenseDate = expense.getExpenseDate();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
}