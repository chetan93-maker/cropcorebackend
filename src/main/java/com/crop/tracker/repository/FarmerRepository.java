package com.crop.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crop.tracker.entity.FarmerEntity;

@Repository
public interface FarmerRepository extends JpaRepository<FarmerEntity, Long> {

    // Fix: Use Optional with proper query
    @Query("SELECT f FROM FarmerEntity f WHERE f.email = :email")
    Optional<FarmerEntity> findByEmail(@Param("email") String email);
    
    // Fix: Use Optional with proper query
    @Query("SELECT f FROM FarmerEntity f WHERE f.mobile = :mobile")
    Optional<FarmerEntity> findByMobile(@Param("mobile") String mobile);
    
    // Add count methods for validation
    @Query("SELECT COUNT(f) FROM FarmerEntity f WHERE f.email = :email")
    long countByEmail(@Param("email") String email);
    
    @Query("SELECT COUNT(f) FROM FarmerEntity f WHERE f.mobile = :mobile")
    long countByMobile(@Param("mobile") String mobile);
    
    // Check if email exists (returns boolean)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FarmerEntity f WHERE f.email = :email")
    boolean existsByEmail(@Param("email") String email);
    
    // Check if mobile exists (returns boolean)
    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FarmerEntity f WHERE f.mobile = :mobile")
    boolean existsByMobile(@Param("mobile") String mobile);
}