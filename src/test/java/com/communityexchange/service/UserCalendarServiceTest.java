package com.communityexchange.service;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.UserCalendarDto;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        when(availabilityRepository.findByUserCalendar(userCalendar)).thenReturn(java.util.Collections.emptyList());
        
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
}