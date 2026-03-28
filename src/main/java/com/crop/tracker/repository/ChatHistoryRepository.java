package com.crop.tracker.repository;

import com.crop.tracker.entity.ChatHistoryEntity;
import com.crop.tracker.entity.FarmerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistoryEntity, Long> {
    
    List<ChatHistoryEntity> findByFarmerOrderByTimestampDesc(FarmerEntity farmer);
    
    List<ChatHistoryEntity> findByFarmerAndTimestampAfterOrderByTimestampDesc(
        FarmerEntity farmer, LocalDateTime timestamp);
    
    @Query("SELECT COUNT(c) FROM ChatHistoryEntity c WHERE c.farmer.id = :farmerId")
    long countByFarmerId(@Param("farmerId") Long farmerId);
}