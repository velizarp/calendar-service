package com.communityexchange.service;

import com.communityexchange.model.dto.AvailabilityDto;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface AvailabilityService {
    
    AvailabilityDto createAvailability(AvailabilityDto availabilityDto);
    
    AvailabilityDto getAvailabilityById(UUID id);
    
    List<AvailabilityDto> getAvailabilitiesByUserCalendar(UUID userCalendarId);
    
    List<AvailabilityDto> getAvailabilitiesByUserCalendarAndDayOfWeek(UUID userCalendarId, DayOfWeek dayOfWeek);
    
    AvailabilityDto updateAvailability(UUID id, AvailabilityDto availabilityDto);
    
    void deleteAvailability(UUID id);
}