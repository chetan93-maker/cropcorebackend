package com.crop.tracker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "crops")
public class CropEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cropName;

    private String season;

    private double income;

    // ✅ ADDED FIELD (for dashboard)
    private LocalDate createdDate;

    // ✅ Many crops → one farmer
    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private FarmerEntity farmer;

    // ✅ One crop → many expenses
    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL)
    @JsonManagedReference 
    private List<ExpenseEntity> expenses;

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropName() {
        return cropName;
    }

    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    // ✅ NEW GETTER
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    // ✅ NEW SETTER
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public FarmerEntity getFarmer() {
        return farmer;
    }

    public void setFarmer(FarmerEntity farmer) {
        this.farmer = farmer;
    }

    public List<ExpenseEntity> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseEntity> expenses) {
        this.expenses = expenses;
    }
}
