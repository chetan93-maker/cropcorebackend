package com.crop.tracker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crop.tracker.entity.ExpenseEntity;

@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    List<ExpenseEntity> findByExpenseDateBetween(LocalDate from, LocalDate to);
    
    List<ExpenseEntity> findByCropId(Long cropId);
    
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.crop.id = :cropId")
    Double sumByCropId(@Param("cropId") Long cropId);  // FIXED: Renamed from sum() to sumByCropId()
    
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e")
    Double totalExpense();

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE MONTH(e.expenseDate) = MONTH(CURRENT_DATE)")
    Double monthlyExpense();
    
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.crop.farmer.id = :farmerId")
    Double totalExpenseByFarmer(@Param("farmerId") Long farmerId);
    
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.crop.farmer.id = :farmerId AND MONTH(e.expenseDate) = MONTH(CURRENT_DATE)")
    Double monthlyExpenseByFarmer(@Param("farmerId") Long farmerId);
    
    @Query("SELECT e FROM ExpenseEntity e WHERE e.crop.farmer.id = :farmerId ORDER BY e.expenseDate DESC")
    List<ExpenseEntity> findByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query("SELECT e FROM ExpenseEntity e WHERE e.crop.farmer.id = :farmerId AND e.expenseDate BETWEEN :from AND :to ORDER BY e.expenseDate DESC")
    List<ExpenseEntity> findByFarmerAndDateRange(@Param("farmerId") Long farmerId, @Param("from") LocalDate from, @Param("to") LocalDate to);  // ADDED this method
    
    @Query(value = "SELECT * FROM expenses e WHERE e.crop_id IN (SELECT c.id FROM crops c WHERE c.farmer_id = :farmerId) ORDER BY e.expense_date DESC LIMIT 5", nativeQuery = true)
    List<ExpenseEntity> findRecentByFarmerId(@Param("farmerId") Long farmerId);
    
    @Query(value = "SELECT * FROM expenses e WHERE e.crop_id IN (SELECT c.id FROM crops c WHERE c.farmer_id = :farmerId) ORDER BY e.expense_date DESC LIMIT 5", nativeQuery = true)
    List<ExpenseEntity> findTop5ByFarmerIdOrderByExpenseDateDesc(@Param("farmerId") Long farmerId);
}