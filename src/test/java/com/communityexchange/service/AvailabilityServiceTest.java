package com.communityexchange.service;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.model.entity.Availability;
import com.communityexchange.model.entity.UserCalendar;
import com.communityexchange.repository.AvailabilityRepository;
import com.communityexchange.repository.UserCalendarRepository;
import com.communityexchange.service.impl.AvailabilityServiceImpl;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityServiceTest {

    @Mock
    private AvailabilityRepository availabilityRepository;

    @Mock
    private UserCalendarRepository userCalendarRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private UserCalendar userCalendar;
    private Availability availability;
    private AvailabilityDto availabilityDto;
    private final UUID userCalendarId = UUID.randomUUID();
    private final UUID availabilityId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup test data
        userCalendar = new UserCalendar();
        userCalendar.setId(userCalendarId);
        userCalendar.setUserId(userId);
        userCalendar.setCreatedAt(LocalDateTime.now());
        userCalendar.setUpdatedAt(LocalDateTime.now());

        availability = new Availability();
        availability.setId(availabilityId);
        availability.setUserCalendar(userCalendar);
        availability.setDayOfWeek(DayOfWeek.MONDAY);
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(17, 0));
        availability.setRecurring(true);
        availability.setActive(true);

        availabilityDto = new AvailabilityDto();
        availabilityDto.setId(availabilityId);
        availabilityDto.setUserCalendarId(userCalendarId);
        availabilityDto.setDayOfWeek(DayOfWeek.MONDAY);
        availabilityDto.setStartTime(LocalTime.of(9, 0));
        availabilityDto.setEndTime(LocalTime.of(17, 0));
        availabilityDto.setRecurring(true);
        availabilityDto.setActive(true);
    }

    @Test
    void createAvailability_ShouldCreateAvailabilitySuccessfully() {
        // Arrange
        when(userCalendarRepository.findById(userCalendarId)).thenReturn(Optional.of(userCalendar));
        when(modelMapper.map(availabilityDto, Availability.class)).thenReturn(availability);
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);
        when(modelMapper.map(availability, AvailabilityDto.class)).thenReturn(availabilityDto);

        // Act
        AvailabilityDto result = availabilityService.createAvailability(availabilityDto);

        // Assert
        assertNotNull(result);
        assertEquals(availabilityDto.getId(), result.getId());
        assertEquals(availabilityDto.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(availabilityDto.getStartTime(), result.getStartTime());
        assertEquals(availabilityDto.getEndTime(), result.getEndTime());
        assertTrue(result.isRecurring());
        assertTrue(result.isActive());
        verify(userCalendarRepository).findById(userCalendarId);
        verify(availabilityRepository).save(any(Availability.class));
    }

    @Test
    void createAvailability_WithNonexistentUserCalendar_ShouldThrowException() {
        // Arrange
        when(userCalendarRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> availabilityService.createAvailability(availabilityDto));
        verify(userCalendarRepository).findById(userCalendarId);
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void getAvailabilityById_WithValidId_ShouldReturnAvailability() {
        // Arrange
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));
        when(modelMapper.map(availability, AvailabilityDto.class)).thenReturn(availabilityDto);

        // Act
        AvailabilityDto result = availabilityService.getAvailabilityById(availabilityId);

        // Assert
        assertNotNull(result);
        assertEquals(availabilityDto.getDayOfWeek(), result.getDayOfWeek());
        assertEquals(availabilityDto.getStartTime(), result.getStartTime());
        verify(availabilityRepository).findById(availabilityId);
    }

    @Test
    void getAvailabilityById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(availabilityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> availabilityService.getAvailabilityById(UUID.randomUUID()));
        verify(availabilityRepository).findById(any(UUID.class));
    }

    @Test
    void getAvailabilitiesByUserCalendar_ShouldReturnAvailabilities() {
        // Arrange
        List<Availability> availabilities = Collections.singletonList(availability);
        when(userCalendarRepository.findById(userCalendarId)).thenReturn(Optional.of(userCalendar));
        when(availabilityRepository.findByUserCalendar(userCalendar)).thenReturn(availabilities);
        when(modelMapper.map(availability, AvailabilityDto.class)).thenReturn(availabilityDto);

        // Act
        List<AvailabilityDto> result = availabilityService.getAvailabilitiesByUserCalendar(userCalendarId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availabilityDto.getDayOfWeek(), result.get(0).getDayOfWeek());
        assertEquals(availabilityDto.getStartTime(), result.get(0).getStartTime());
        verify(userCalendarRepository).findById(userCalendarId);
        verify(availabilityRepository).findByUserCalendar(userCalendar);
    }

    @Test
    void getAvailabilitiesByUserCalendar_WithNonexistentUserCalendar_ShouldThrowException() {
        // Arrange
        when(userCalendarRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> availabilityService.getAvailabilitiesByUserCalendar(UUID.randomUUID()));
        verify(userCalendarRepository).findById(any(UUID.class));
        verify(availabilityRepository, never()).findByUserCalendar(any(UserCalendar.class));
    }

    @Test
    void getAvailabilitiesByUserCalendarAndDayOfWeek_ShouldReturnAvailabilities() {
        // Arrange
        List<Availability> availabilities = Collections.singletonList(availability);
        when(userCalendarRepository.findById(userCalendarId)).thenReturn(Optional.of(userCalendar));
        when(availabilityRepository.findByUserCalendarAndDayOfWeek(userCalendar, DayOfWeek.MONDAY)).thenReturn(availabilities);
        when(modelMapper.map(availability, AvailabilityDto.class)).thenReturn(availabilityDto);

        // Act
        List<AvailabilityDto> result = availabilityService.getAvailabilitiesByUserCalendarAndDayOfWeek(userCalendarId, DayOfWeek.MONDAY);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(availabilityDto.getDayOfWeek(), result.get(0).getDayOfWeek());
        assertEquals(availabilityDto.getStartTime(), result.get(0).getStartTime());
        verify(userCalendarRepository).findById(userCalendarId);
        verify(availabilityRepository).findByUserCalendarAndDayOfWeek(userCalendar, DayOfWeek.MONDAY);
    }

    @Test
    void updateAvailability_ShouldUpdateAvailabilitySuccessfully() {
        // Arrange
        when(availabilityRepository.findById(availabilityId)).thenReturn(Optional.of(availability));
        when(availabilityRepository.save(any(Availability.class))).thenReturn(availability);
        when(modelMapper.map(availability, AvailabilityDto.class)).thenReturn(availabilityDto);

        AvailabilityDto updatedDto = new AvailabilityDto();
        updatedDto.setDayOfWeek(DayOfWeek.TUESDAY);
        updatedDto.setStartTime(LocalTime.of(10, 0));
        updatedDto.setEndTime(LocalTime.of(18, 0));
        updatedDto.setRecurring(false);
        updatedDto.setActive(false);

        // Act
        AvailabilityDto result = availabilityService.updateAvailability(availabilityId, updatedDto);

        // Assert
        assertNotNull(result);
        // Verify that the entity was updated with the new values
        assertEquals(DayOfWeek.TUESDAY, availability.getDayOfWeek());
        assertEquals(LocalTime.of(10, 0), availability.getStartTime());
        assertEquals(LocalTime.of(18, 0), availability.getEndTime());
        assertFalse(availability.isRecurring());
        assertFalse(availability.isActive());
        verify(availabilityRepository).findById(availabilityId);
        verify(availabilityRepository).save(availability);
    }

    @Test
    void updateAvailability_WithNonexistentId_ShouldThrowException() {
        // Arrange
        when(availabilityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> availabilityService.updateAvailability(UUID.randomUUID(), availabilityDto));
        verify(availabilityRepository).findById(any(UUID.class));
        verify(availabilityRepository, never()).save(any(Availability.class));
    }

    @Test
    void deleteAvailability_WithValidId_ShouldDeleteAvailability() {
        // Arrange
        when(availabilityRepository.existsById(availabilityId)).thenReturn(true);
        doNothing().when(availabilityRepository).deleteById(availabilityId);

        // Act
        availabilityService.deleteAvailability(availabilityId);

        // Assert
        verify(availabilityRepository).existsById(availabilityId);
        verify(availabilityRepository).deleteById(availabilityId);
    }

    @Test
    void deleteAvailability_WithNonexistentId_ShouldThrowException() {
        // Arrange
        when(availabilityRepository.existsById(any(UUID.class))).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> availabilityService.deleteAvailability(UUID.randomUUID()));
        verify(availabilityRepository).existsById(any(UUID.class));
        verify(availabilityRepository, never()).deleteById(any(UUID.class));
    }
}