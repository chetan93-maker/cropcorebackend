package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crop.tracker.dto.DashboardSummary;
import com.crop.tracker.repository.CropRepository;
import com.crop.tracker.repository.ExpenseRepository;

@Service
public class DashboardService {

    @Autowired
    private CropRepository cropRepo;

    @Autowired
    private ExpenseRepository expenseRepo;

    public DashboardSummary getSummary() {
        // Get totals for ALL farmers (original)
        long totalCrops = cropRepo.count();
        Double income = cropRepo.totalIncome();
        Double expense = expenseRepo.totalExpense();
        Double monthExp = expenseRepo.monthlyExpense();
        Double monthInc = cropRepo.monthlyIncome();

        double totalIncome = income == null ? 0 : income;
        double totalExpenses = expense == null ? 0 : expense;
        double monthlyExpense = monthExp == null ? 0 : monthExp;
        double monthlyIncome = monthInc == null ? 0 : monthInc;
        double profit = totalIncome - totalExpenses;

        return new DashboardSummary(
                totalCrops, totalIncome, totalExpenses, profit,
                monthlyExpense, monthlyIncome
        );
    }
    
    // NEW: Get summary for specific farmer
    public DashboardSummary getSummaryByFarmer(Long farmerId) {
        long totalCrops = cropRepo.countByFarmerId(farmerId);
        Double income = cropRepo.totalIncomeByFarmer(farmerId);
        Double expense = expenseRepo.totalExpenseByFarmer(farmerId);
        Double monthExp = expenseRepo.monthlyExpenseByFarmer(farmerId);
        Double monthInc = cropRepo.monthlyIncomeByFarmer(farmerId);

        double totalIncome = income == null ? 0 : income;
        double totalExpenses = expense == null ? 0 : expense;
        double monthlyExpense = monthExp == null ? 0 : monthExp;
        double monthlyIncome = monthInc == null ? 0 : monthInc;
        double profit = totalIncome - totalExpenses;

        return new DashboardSummary(
                totalCrops, 
                totalIncome, 
                totalExpenses, 
                profit,
                monthlyExpense, 
                monthlyIncome
        );
    }
}