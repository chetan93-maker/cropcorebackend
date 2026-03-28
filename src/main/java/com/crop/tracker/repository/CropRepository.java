package com.crop.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crop.tracker.entity.CropEntity;

@Repository
public interface CropRepository extends JpaRepository<CropEntity, Long> {
    
    @Query("SELECT SUM(c.income) FROM CropEntity c")
    Double totalIncome();
    
    @Query("SELECT SUM(c.income) FROM CropEntity c WHERE MONTH(c.createdDate) = MONTH(CURRENT_DATE)")
    Double monthlyIncome();
    
    // NEW: Get crops by farmer ID
    List<CropEntity> findByFarmerId(Long farmerId);
    
    // NEW: Get total income by farmer
    @Query("SELECT SUM(c.income) FROM CropEntity c WHERE c.farmer.id = :farmerId")
    Double totalIncomeByFarmer(@Param("farmerId") Long farmerId);
    
    // NEW: Get monthly income by farmer
    @Query("SELECT SUM(c.income) FROM CropEntity c WHERE c.farmer.id = :farmerId AND MONTH(c.createdDate) = MONTH(CURRENT_DATE)")
    Double monthlyIncomeByFarmer(@Param("farmerId") Long farmerId);
    
    // NEW: Count crops by farmer
    Long countByFarmerId(Long farmerId);
}