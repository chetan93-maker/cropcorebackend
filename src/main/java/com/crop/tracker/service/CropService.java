package com.crop.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crop.tracker.entity.CropEntity;
import com.crop.tracker.repository.CropRepository;
import com.crop.tracker.repository.ExpenseRepository;

@Service
public class CropService {

    @Autowired
    CropRepository cropRepo;

    @Autowired
    ExpenseRepository expenseRepo;

    public double profit(Long cropId) {
        CropEntity crop = cropRepo.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        Double exp = expenseRepo.sumByCropId(cropId);  // FIXED: Changed from sum() to sumByCropId()

        return crop.getIncome() - (exp == null ? 0 : exp);
    }
}