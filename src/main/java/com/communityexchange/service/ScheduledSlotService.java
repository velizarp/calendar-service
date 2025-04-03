package com.communityexchange.service;

import com.communityexchange.model.dto.ScheduledSlotDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ScheduledSlotService {
    
    ScheduledSlotDto createScheduledSlot(ScheduledSlotDto scheduledSlotDto);
    
    ScheduledSlotDto getScheduledSlotById(UUID id);
    
    ScheduledSlotDto getScheduledSlotByExchangeId(UUID exchangeId);
    
    List<ScheduledSlotDto> getScheduledSlotsByUserId(UUID userId);
    
    List<ScheduledSlotDto> getScheduledSlotsByUserIdAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end);
    
    ScheduledSlotDto updateScheduledSlot(UUID id, ScheduledSlotDto scheduledSlotDto);
    
    ScheduledSlotDto confirmScheduledSlot(UUID id);
    
    void deleteScheduledSlot(UUID id);
}