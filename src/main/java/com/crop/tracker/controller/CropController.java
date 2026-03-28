package com.crop.tracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crop.tracker.dto.CropDTO;
import com.crop.tracker.dto.CropRequestDTO;
import com.crop.tracker.entity.CropEntity;
import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.CropRepository;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.service.CropService;
import com.crop.tracker.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/crop")
@CrossOrigin("*")
public class CropController {

    @Autowired
    CropRepository repo;

    @Autowired
    FarmerRepository farmerRepo;

    @Autowired
    CropService service;
    
    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody CropRequestDTO request, HttpServletRequest httpRequest) {
        try {
            // Get farmer ID from token
            String token = extractToken(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            CropEntity crop = new CropEntity();
            crop.setCropName(request.getCropName());
            crop.setSeason(request.getSeason());
            crop.setIncome(request.getIncome());
            crop.setCreatedDate(request.getCreatedDate());
            crop.setFarmer(farmer);
            
            CropEntity saved = repo.save(crop);
            return ResponseEntity.ok(new CropDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(HttpServletRequest httpRequest) {
        try {
            // Get farmer ID from token
            String token = extractToken(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            // Get only this farmer's crops
            List<CropDTO> crops = repo.findByFarmerId(farmer.getId()).stream()
                .map(CropDTO::new)
                .collect(Collectors.toList());
                
            return ResponseEntity.ok(crops);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CropRequestDTO updated, HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            CropEntity crop = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Crop not found"));
            
            // Verify this crop belongs to the farmer
            if (!crop.getFarmer().getId().equals(farmer.getId())) {
                return ResponseEntity.status(403).body("You don't have permission to update this crop");
            }

            crop.setCropName(updated.getCropName());
            crop.setIncome(updated.getIncome());
            crop.setSeason(updated.getSeason());

            CropEntity saved = repo.save(crop);
            return ResponseEntity.ok(new CropDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            CropEntity crop = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Crop not found"));
            
            // Verify this crop belongs to the farmer
            if (!crop.getFarmer().getId().equals(farmer.getId())) {
                return ResponseEntity.status(403).body("You don't have permission to delete this crop");
            }
            
            repo.deleteById(id);
            return ResponseEntity.ok("Crop Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired");
        }
    }

    @GetMapping("/profit/{id}")
    public ResponseEntity<?> profit(@PathVariable Long id, HttpServletRequest httpRequest) {
        try {
            String token = extractToken(httpRequest);
            if (token == null) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            
            String mobile = jwtUtil.extractMobile(token);
            FarmerEntity farmer = farmerRepo.findByMobile(mobile)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            CropEntity crop = repo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Crop not found"));
            
            // Verify this crop belongs to the farmer
            if (!crop.getFarmer().getId().equals(farmer.getId())) {
                return ResponseEntity.status(403).body("You don't have permission to view this crop");
            }
            
            double profit = service.profit(id);
            return ResponseEntity.ok(profit);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Session expired");
        }
    }
    
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}