package com.crop.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.crop.tracker.dto.RegisterRequestDTO;
import com.crop.tracker.dto.LoginRequestDTO;
import com.crop.tracker.entity.FarmerEntity;
import com.crop.tracker.repository.FarmerRepository;
import com.crop.tracker.security.JwtUtil;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/farmers") // ✅🔥 VERY IMPORTANT (fixes 404)
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FarmerController {

    @Autowired
    FarmerRepository repo;

    @Autowired
    JwtUtil jwtUtil;

    // =====================================================
    // ✅ REGISTER
    // POST: /farmers/register
    // =====================================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                response.put("error", "Password cannot be null or empty");
                return ResponseEntity.badRequest().body(response);
            }

            if (repo.findByEmail(request.getEmail()).isPresent()) {
                response.put("error", "Email already registered!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            if (repo.findByMobile(request.getMobile()).isPresent()) {
                response.put("error", "Mobile number already registered!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            FarmerEntity farmer = new FarmerEntity();
            farmer.setName(request.getName());
            farmer.setEmail(request.getEmail());
            farmer.setMobile(request.getMobile());
            farmer.setPassword(request.getPassword()); // ⚠️ later use BCrypt
            farmer.setLocation(request.getLocation());

            FarmerEntity saved = repo.save(farmer);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("id", saved.getId());
            successResponse.put("name", saved.getName());
            successResponse.put("email", saved.getEmail());
            successResponse.put("mobile", saved.getMobile());
            successResponse.put("location", saved.getLocation());
            successResponse.put("message", "Registration successful!");

            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =====================================================
    // ✅ LOGIN
    // POST: /farmers/login
    // =====================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<FarmerEntity> farmerOpt = repo.findByEmail(request.getEmail());

            if (farmerOpt.isEmpty()) {
                response.put("error", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            FarmerEntity farmer = farmerOpt.get();

            if (!farmer.getPassword().equals(request.getPassword())) {
                response.put("error", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = jwtUtil.generateToken(farmer.getMobile());

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Login successful");
            successResponse.put("token", token);
            successResponse.put("farmerId", farmer.getId());
            successResponse.put("farmerName", farmer.getName());
            successResponse.put("email", farmer.getEmail());
            successResponse.put("mobile", farmer.getMobile());
            successResponse.put("location", farmer.getLocation());

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =====================================================
    // ✅ CHECK MOBILE (FOR FORGOT PASSWORD)
    // GET: /farmers/check-mobile/{mobile}
    // =====================================================
    @GetMapping("/check-mobile/{mobile}")
    public ResponseEntity<?> checkMobile(@PathVariable String mobile) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<FarmerEntity> farmerOpt = repo.findByMobile(mobile);

            if (farmerOpt.isPresent()) {
                response.put("exists", true);
                response.put("email", farmerOpt.get().getEmail());
            } else {
                response.put("exists", false);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Failed to check mobile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // =====================================================
    // ✅ RESET PASSWORD
    // POST: /farmers/reset-password
    // =====================================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String mobile = request.get("mobile");
            String newPassword = request.get("newPassword");

            if (mobile == null || newPassword == null) {
                response.put("error", "Mobile and newPassword are required");
                return ResponseEntity.badRequest().body(response);
            }

            Optional<FarmerEntity> farmerOpt = repo.findByMobile(mobile);

            if (farmerOpt.isEmpty()) {
                response.put("error", "Mobile number not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            FarmerEntity farmer = farmerOpt.get();
            farmer.setPassword(newPassword); // ⚠️ later use BCrypt
            repo.save(farmer);

            response.put("message", "Password reset successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Password reset failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}