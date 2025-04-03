package com.communityexchange.repository;

import com.communityexchange.model.entity.ScheduledSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduledSlotRepository extends JpaRepository<ScheduledSlot, UUID> {
    
    List<ScheduledSlot> findByUserId(UUID userId);
    
    Optional<ScheduledSlot> findByExchangeId(UUID exchangeId);
    
    List<ScheduledSlot> findByUserIdAndStartTimeBetween(UUID userId, LocalDateTime start, LocalDateTime end);
    
    List<ScheduledSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}