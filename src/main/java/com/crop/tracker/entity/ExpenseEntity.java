package com.crop.tracker.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "expenses")
public class ExpenseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ expense type (seed, fertilizer, labour etc.)
    private String type;

    // ✅ amount spent
    private double amount;

    // ✅ expense date
    private LocalDate expenseDate;

    // ✅ Many expenses → one crop
    @ManyToOne
    @JoinColumn(name = "crop_id")
    @JsonBackReference
    private CropEntity crop;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {              // ⭐ FIX
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

    public LocalDate getExpenseDate() {   // ⭐ FIX
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public CropEntity getCrop() {
        return crop;
    }

    public void setCrop(CropEntity crop) {
        this.crop = crop;
    }
}
