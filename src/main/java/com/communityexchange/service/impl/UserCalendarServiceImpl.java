package com.communityexchange.service.impl;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.model.dto.AvailabilitySlotDto;
import com.communityexchange.model.dto.UserCalendarDto;
import com.communityexchange.model.entity.Availability;
import com.communityexchange.model.entity.ScheduledSlot;
import com.communityexchange.model.entity.UserCalendar;
import com.communityexchange.repository.AvailabilityRepository;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.repository.UserCalendarRepository;
import com.communityexchange.service.UserCalendarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCalendarServiceImpl implements UserCalendarService {
    
    private final UserCalendarRepository userCalendarRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ScheduledSlotRepository scheduledSlotRepository;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional
    public UserCalendarDto createUserCalendar(UUID userId) {
        if (userCalendarRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Calendar already exists for this user");
        }
        
        UserCalendar userCalendar = new UserCalendar();
        userCalendar.setUserId(userId);
        userCalendar.setCreatedAt(LocalDateTime.now());
        userCalendar.setUpdatedAt(LocalDateTime.now());
        
        UserCalendar savedCalendar = userCalendarRepository.save(userCalendar);
        
        return mapToDto(savedCalendar);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserCalendarDto getUserCalendar(UUID userId) {
        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found for user ID: " + userId));
        
        UserCalendarDto userCalendarDto = mapToDto(userCalendar);
        
        List<AvailabilityDto> availabilityDtos = availabilityRepository.findByUserCalendar(userCalendar)
                .stream()
                .map(availability -> modelMapper.map(availability, AvailabilityDto.class))
                .collect(Collectors.toList());
        
        userCalendarDto.setAvailabilities(availabilityDtos);
        
        return userCalendarDto;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AvailabilitySlotDto> getAvailableSlots(UUID userId, LocalDateTime start, LocalDateTime end) {
        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found for user ID: " + userId));
        
        List<Availability> availabilities = availabilityRepository.findByUserCalendarAndIsActiveTrue(userCalendar);
        List<ScheduledSlot> scheduledSlots = scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end);
        
        return generateAvailableSlots(availabilities, scheduledSlots, start, end, userId);
    }
    
    @Override
    @Transactional
    public void deleteUserCalendar(UUID userId) {
        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found for user ID: " + userId));
        
        userCalendarRepository.delete(userCalendar);
    }
    
    private List<AvailabilitySlotDto> generateAvailableSlots(
            List<Availability> availabilities,
            List<ScheduledSlot> scheduledSlots,
            LocalDateTime start,
            LocalDateTime end,
            UUID userId) {
        
        List<AvailabilitySlotDto> availableSlots = new ArrayList<>();
        LocalDateTime current = start;
        
        while (current.isBefore(end)) {
            DayOfWeek dayOfWeek = current.getDayOfWeek();
            
            for (Availability availability : availabilities) {
                if (availability.getDayOfWeek() == dayOfWeek) {
                    LocalDateTime slotStart = current.with(availability.getStartTime());
                    LocalDateTime slotEnd = current.with(availability.getEndTime());
                    
                    // Check if the slot is within the requested time range
                    if (slotStart.isAfter(start) && slotEnd.isBefore(end)) {
                        // Check if the slot doesn't overlap with scheduled slots
                        boolean isAvailable = scheduledSlots.stream()
                                .noneMatch(slot -> 
                                        (slotStart.isBefore(slot.getEndTime()) && slotEnd.isAfter(slot.getStartTime())));
                        
                        if (isAvailable) {
                            AvailabilitySlotDto slot = new AvailabilitySlotDto();
                            slot.setStartTime(slotStart);
                            slot.setEndTime(slotEnd);
                            slot.setUserId(userId);
                            availableSlots.add(slot);
                        }
                    }
                }
            }
            
            // Move to the next day
            current = current.plusDays(1).with(LocalTime.MIN);
        }
        
        return availableSlots;
    }
    
    private UserCalendarDto mapToDto(UserCalendar userCalendar) {
        return modelMapper.map(userCalendar, UserCalendarDto.class);
    }
}