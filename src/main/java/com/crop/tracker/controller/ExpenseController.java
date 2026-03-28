package com.crop.tracker.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crop.tracker.dto.ExpenseDTO;
import com.crop.tracker.dto.ExpenseRequestDTO;
import com.crop.tracker.entity.CropEntity;
import com.crop.tracker.entity.ExpenseEntity;
import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.CropRepository;
import com.crop.tracker.repository.ExpenseRepository;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/expense")
@CrossOrigin("*")
public class ExpenseController {

	@Autowired
	ExpenseRepository repo;

	@Autowired
	CropRepository cropRepo;

	@Autowired
	FarmerRepository farmerRepo;

	@Autowired
	JwtUtil jwtUtil;

	@PostMapping("/add")
	public ResponseEntity<?> add(@RequestBody ExpenseRequestDTO request, HttpServletRequest httpRequest) {
		try {
			String token = extractToken(httpRequest);
			if (token == null) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			String mobile = jwtUtil.extractMobile(token);
			FarmerEntity farmer = farmerRepo.findByMobile(mobile)
					.orElseThrow(() -> new RuntimeException("Farmer not found"));

			CropEntity crop = cropRepo.findById(request.getCropId())
					.orElseThrow(() -> new RuntimeException("Crop not found"));

			// Verify this crop belongs to the farmer
			if (!crop.getFarmer().getId().equals(farmer.getId())) {
				return ResponseEntity.status(403).body("You don't have permission to add expense to this crop");
			}

			ExpenseEntity expense = new ExpenseEntity();
			expense.setType(request.getType());
			expense.setAmount(request.getAmount());
			expense.setExpenseDate(request.getExpenseDate());
			expense.setCrop(crop);

			ExpenseEntity saved = repo.save(expense);
			return ResponseEntity.ok(new ExpenseDTO(saved));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body("Session expired: " + e.getMessage());
		}
	}

	@GetMapping("/date")
	public ResponseEntity<?> byDate(@RequestParam LocalDate from, @RequestParam LocalDate to,
			HttpServletRequest httpRequest) {
		try {
			String token = extractToken(httpRequest);
			if (token == null) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			String mobile = jwtUtil.extractMobile(token);
			FarmerEntity farmer = farmerRepo.findByMobile(mobile)
					.orElseThrow(() -> new RuntimeException("Farmer not found"));

			List<ExpenseDTO> expenses = repo.findByFarmerAndDateRange(farmer.getId(), from, to).stream()
					.map(ExpenseDTO::new).collect(Collectors.toList());

			return ResponseEntity.ok(expenses);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body("Session expired");
		}
	}

	@GetMapping("/crop/{cropId}")
	public ResponseEntity<?> getByCrop(@PathVariable Long cropId, HttpServletRequest httpRequest) {
		try {
			String token = extractToken(httpRequest);
			if (token == null) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			String mobile = jwtUtil.extractMobile(token);
			FarmerEntity farmer = farmerRepo.findByMobile(mobile)
					.orElseThrow(() -> new RuntimeException("Farmer not found"));

			CropEntity crop = cropRepo.findById(cropId).orElseThrow(() -> new RuntimeException("Crop not found"));

			// Verify this crop belongs to the farmer
			if (!crop.getFarmer().getId().equals(farmer.getId())) {
				return ResponseEntity.status(403).body("You don't have permission to view these expenses");
			}

			List<ExpenseDTO> expenses = repo.findByCropId(cropId).stream().map(ExpenseDTO::new)
					.collect(Collectors.toList());

			return ResponseEntity.ok(expenses);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body("Session expired");
		}
	}

	// FIXED: Update expense method
	@PutMapping("/update/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ExpenseRequestDTO updated,
			HttpServletRequest httpRequest) {
		try {
			String token = extractToken(httpRequest);
			if (token == null) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			String mobile = jwtUtil.extractMobile(token);
			FarmerEntity farmer = farmerRepo.findByMobile(mobile)
					.orElseThrow(() -> new RuntimeException("Farmer not found"));

			ExpenseEntity expense = repo.findById(id)
					.orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

			// Verify this expense belongs to the farmer's crop
			if (!expense.getCrop().getFarmer().getId().equals(farmer.getId())) {
				return ResponseEntity.status(403).body("You don't have permission to update this expense");
			}

			// Update fields
			expense.setType(updated.getType());
			expense.setAmount(updated.getAmount());
			expense.setExpenseDate(updated.getExpenseDate());

			// If cropId is provided and different, update crop
			if (updated.getCropId() != null && !updated.getCropId().equals(expense.getCrop().getId())) {
				CropEntity newCrop = cropRepo.findById(updated.getCropId())
						.orElseThrow(() -> new RuntimeException("Crop not found with id: " + updated.getCropId()));

				// Verify new crop belongs to the farmer
				if (!newCrop.getFarmer().getId().equals(farmer.getId())) {
					return ResponseEntity.status(403).body("You don't have permission to use this crop");
				}

				expense.setCrop(newCrop);
			}

			ExpenseEntity saved = repo.save(expense);
			return ResponseEntity.ok(new ExpenseDTO(saved));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body("Session expired: " + e.getMessage());
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

			ExpenseEntity expense = repo.findById(id)
					.orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

			// Verify this expense belongs to the farmer's crop
			if (!expense.getCrop().getFarmer().getId().equals(farmer.getId())) {
				return ResponseEntity.status(403).body("You don't have permission to delete this expense");
			}

			repo.deleteById(id);
			return ResponseEntity.ok("Expense Deleted Successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(401).body("Session expired");
		}
	}

	@GetMapping("/recent")
	public ResponseEntity<?> getRecentExpenses(HttpServletRequest httpRequest) {
	    try {
	        String token = extractToken(httpRequest);
	        if (token == null) {
	            return ResponseEntity.status(401).body("Unauthorized");
	        }
	        
	        String mobile = jwtUtil.extractMobile(token);
	        FarmerEntity farmer = farmerRepo.findByMobile(mobile)
	                .orElseThrow(() -> new RuntimeException("Farmer not found"));
	        
	        // You'll need to add this method to your ExpenseRepository
	        List<ExpenseDTO> expenses = repo.findTop5ByFarmerIdOrderByExpenseDateDesc(farmer.getId()).stream()
	            .map(ExpenseDTO::new)
	            .collect(Collectors.toList());
	            
	        return ResponseEntity.ok(expenses);
	    } catch (Exception e) {
	        return ResponseEntity.status(401).body("Session expired");
	    }
	}
	@GetMapping("/all")
	public ResponseEntity<?> getAllExpenses(HttpServletRequest httpRequest) {
		try {
			String token = extractToken(httpRequest);
			if (token == null) {
				return ResponseEntity.status(401).body("Unauthorized");
			}

			String mobile = jwtUtil.extractMobile(token);
			FarmerEntity farmer = farmerRepo.findByMobile(mobile)
					.orElseThrow(() -> new RuntimeException("Farmer not found"));

			List<ExpenseDTO> expenses = repo.findByFarmerId(farmer.getId()).stream().map(ExpenseDTO::new)
					.collect(Collectors.toList());

			return ResponseEntity.ok(expenses);
		} catch (Exception e) {
			e.printStackTrace();
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