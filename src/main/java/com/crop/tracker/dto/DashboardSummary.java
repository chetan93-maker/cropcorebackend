package com.crop.tracker.dto;

public class DashboardSummary {

    private long totalCrops;
    private double totalIncome;
    private double totalExpenses;
    private double totalProfit;
    private double monthlyExpense;
    private double monthlyIncome;

    public DashboardSummary() {}

    public DashboardSummary(long totalCrops, double totalIncome,
                            double totalExpenses, double totalProfit,
                            double monthlyExpense, double monthlyIncome) {
        this.totalCrops = totalCrops;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.totalProfit = totalProfit;
        this.monthlyExpense = monthlyExpense;
        this.monthlyIncome = monthlyIncome;
    }

    // getters setters

    public long getTotalCrops() { return totalCrops; }
    public void setTotalCrops(long totalCrops) { this.totalCrops = totalCrops; }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getTotalProfit() { return totalProfit; }
    public void setTotalProfit(double totalProfit) { this.totalProfit = totalProfit; }

    public double getMonthlyExpense() { return monthlyExpense; }
    public void setMonthlyExpense(double monthlyExpense) { this.monthlyExpense = monthlyExpense; }

    public double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
}
