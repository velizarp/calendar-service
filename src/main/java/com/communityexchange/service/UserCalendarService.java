package com.communityexchange.service;

import com.communityexchange.model.dto.AvailabilitySlotDto;
import com.communityexchange.model.dto.UserCalendarDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserCalendarService {
    
    UserCalendarDto createUserCalendar(UUID userId);
    
    UserCalendarDto getUserCalendar(UUID userId);
    
    List<AvailabilitySlotDto> getAvailableSlots(UUID userId, LocalDateTime start, LocalDateTime end);
    
    void deleteUserCalendar(UUID userId);
}