package com.communityexchange.service;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilitySlotDto;
import com.communityexchange.model.dto.UserCalendarDto;
import com.communityexchange.model.entity.Availability;
import com.communityexchange.model.entity.ScheduledSlot;
import com.communityexchange.model.entity.UserCalendar;
import com.communityexchange.repository.AvailabilityRepository;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.repository.UserCalendarRepository;
import com.communityexchange.service.impl.UserCalendarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCalendarServiceTest {
    
    @Mock
    private UserCalendarRepository userCalendarRepository;
    
    @Mock
    private AvailabilityRepository availabilityRepository;
    
    @Mock
    private ScheduledSlotRepository scheduledSlotRepository;
    
    @Mock
    private ModelMapper modelMapper;
    
    @InjectMocks
    private UserCalendarServiceImpl userCalendarService;
    
    private UUID userId;
    private UserCalendar userCalendar;
    private UserCalendarDto userCalendarDto;
    private Availability availability;
    private ScheduledSlot scheduledSlot;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        userId = UUID.randomUUID();
        
        userCalendar = new UserCalendar();
        userCalendar.setId(UUID.randomUUID());
        userCalendar.setUserId(userId);
        userCalendar.setCreatedAt(LocalDateTime.now());
        userCalendar.setUpdatedAt(LocalDateTime.now());
        
        userCalendarDto = new UserCalendarDto();
        userCalendarDto.setId(userCalendar.getId());
        userCalendarDto.setUserId(userCalendar.getUserId());
        
        // Setup availability
        availability = new Availability();
        availability.setId(UUID.randomUUID());
        availability.setUserCalendar(userCalendar);
        availability.setDayOfWeek(DayOfWeek.MONDAY);
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
        availability.setRecurring(true);
        availability.setActive(true);
        
        // Setup scheduled slot
        scheduledSlot = new ScheduledSlot();
        scheduledSlot.setId(UUID.randomUUID());
        scheduledSlot.setUserId(userId);
        scheduledSlot.setExchangeId(UUID.randomUUID());
        LocalDateTime now = LocalDateTime.now();
        scheduledSlot.setStartTime(now.plusDays(1).withHour(12).withMinute(0));
        scheduledSlot.setEndTime(now.plusDays(1).withHour(13).withMinute(0));
        scheduledSlot.setTitle("Test Meeting");
        scheduledSlot.setConfirmed(true);
    }
    
    @Test
    void createUserCalendar_ShouldCreateCalendarSuccessfully() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userCalendarRepository.save(any(UserCalendar.class))).thenReturn(userCalendar);
        when(modelMapper.map(any(UserCalendar.class), eq(UserCalendarDto.class))).thenReturn(userCalendarDto);
        
        // Act
        UserCalendarDto result = userCalendarService.createUserCalendar(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userCalendarDto.getUserId(), result.getUserId());
        verify(userCalendarRepository).save(any(UserCalendar.class));
    }
    
    @Test
    void createUserCalendar_WithExistingCalendar_ShouldThrowException() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> userCalendarService.createUserCalendar(userId));
        verify(userCalendarRepository, never()).save(any(UserCalendar.class));
    }
    
    @Test
    void getUserCalendar_ShouldReturnCalendarSuccessfully() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        when(modelMapper.map(any(UserCalendar.class), eq(UserCalendarDto.class))).thenReturn(userCalendarDto);
        when(availabilityRepository.findByUserCalendar(userCalendar)).thenReturn(Collections.emptyList());
        
        // Act
        UserCalendarDto result = userCalendarService.getUserCalendar(userId);
        
        // Assert
        assertNotNull(result);
        assertEquals(userCalendarDto.getUserId(), result.getUserId());
    }
    
    @Test
    void getUserCalendar_WithNonexistentCalendar_ShouldThrowException() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userCalendarService.getUserCalendar(userId));
    }
    
    @Test
    void deleteUserCalendar_ShouldDeleteCalendarSuccessfully() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        
        // Act
        userCalendarService.deleteUserCalendar(userId);
        
        // Assert
        verify(userCalendarRepository).delete(userCalendar);
    }
    
    @Test
    void deleteUserCalendar_WithNonexistentCalendar_ShouldThrowException() {
        // Arrange
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userCalendarService.deleteUserCalendar(userId));
        verify(userCalendarRepository, never()).delete(any(UserCalendar.class));
    }
    
    @Test
    void getAvailableSlots_WithAvailabilities_ShouldReturnAvailableSlots() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        when(availabilityRepository.findByUserCalendarAndIsActiveTrue(userCalendar)).thenReturn(availabilities);
        when(scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(Collections.emptyList());
        
        // Act
        List<AvailabilitySlotDto> result = userCalendarService.getAvailableSlots(userId, start, end);
        
        // Assert
        assertNotNull(result);
        // There should be at least one available slot for each day of the week that matches the availability
        assertFalse(result.isEmpty());
        
        // Check if the slots have the correct properties
        for (AvailabilitySlotDto slot : result) {
            assertEquals(userId, slot.getUserId());
            assertTrue(slot.getStartTime().isAfter(start));
            assertTrue(slot.getEndTime().isBefore(end));
            assertEquals(availability.getStartTime().getHour(), slot.getStartTime().getHour());
            assertEquals(availability.getStartTime().getMinute(), slot.getStartTime().getMinute());
            assertEquals(availability.getEndTime().getHour(), slot.getEndTime().getHour());
            assertEquals(availability.getEndTime().getMinute(), slot.getEndTime().getMinute());
        }
        
        verify(userCalendarRepository).findByUserId(userId);
        verify(availabilityRepository).findByUserCalendarAndIsActiveTrue(userCalendar);
        verify(scheduledSlotRepository).findByUserIdAndStartTimeBetween(userId, start, end);
    }
    
    @Test
    void getAvailableSlots_WithOverlappingScheduledSlots_ShouldExcludeOverlappingSlots() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        List<Availability> availabilities = new ArrayList<>();
        availabilities.add(availability);
        
        List<ScheduledSlot> scheduledSlots = new ArrayList<>();
        scheduledSlots.add(scheduledSlot);
        
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        when(availabilityRepository.findByUserCalendarAndIsActiveTrue(userCalendar)).thenReturn(availabilities);
        when(scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(scheduledSlots);
        
        // Act
        List<AvailabilitySlotDto> result = userCalendarService.getAvailableSlots(userId, start, end);
        
        // Assert
        assertNotNull(result);
        
        // Check that none of the available slots overlap with scheduled slots
        for (AvailabilitySlotDto slot : result) {
            for (ScheduledSlot scheduled : scheduledSlots) {
                // Check if both start before the other ends (i.e., they overlap)
                boolean overlaps = slot.getStartTime().isBefore(scheduled.getEndTime()) && 
                                  scheduled.getStartTime().isBefore(slot.getEndTime());
                assertFalse(overlaps, "Available slot should not overlap with scheduled slot");
            }
        }
        
        verify(userCalendarRepository).findByUserId(userId);
        verify(availabilityRepository).findByUserCalendarAndIsActiveTrue(userCalendar);
        verify(scheduledSlotRepository).findByUserIdAndStartTimeBetween(userId, start, end);
    }
    
    @Test
    void getAvailableSlots_WithNoAvailabilities_ShouldReturnEmptyList() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.of(userCalendar));
        when(availabilityRepository.findByUserCalendarAndIsActiveTrue(userCalendar)).thenReturn(Collections.emptyList());
        when(scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(Collections.emptyList());
        
        // Act
        List<AvailabilitySlotDto> result = userCalendarService.getAvailableSlots(userId, start, end);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userCalendarRepository).findByUserId(userId);
        verify(availabilityRepository).findByUserCalendarAndIsActiveTrue(userCalendar);
        verify(scheduledSlotRepository).findByUserIdAndStartTimeBetween(userId, start, end);
    }
    
    @Test
    void getAvailableSlots_WithNonExistentCalendar_ShouldThrowException() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        when(userCalendarRepository.findByUserId(userId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userCalendarService.getAvailableSlots(userId, start, end));
        
        verify(userCalendarRepository).findByUserId(userId);
        verify(availabilityRepository, never()).findByUserCalendarAndIsActiveTrue(any());
        verify(scheduledSlotRepository, never()).findByUserIdAndStartTimeBetween(any(), any(), any());
    }
}