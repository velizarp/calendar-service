package com.communityexchange.service;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.model.entity.ScheduledSlot;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.service.impl.ScheduledSlotServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledSlotServiceTest {

    @Mock
    private ScheduledSlotRepository scheduledSlotRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ScheduledSlotServiceImpl scheduledSlotService;

    private ScheduledSlot scheduledSlot;
    private ScheduledSlotDto scheduledSlotDto;
    private final UUID scheduledSlotId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID exchangeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);

        scheduledSlot = new ScheduledSlot();
        scheduledSlot.setId(scheduledSlotId);
        scheduledSlot.setUserId(userId);
        scheduledSlot.setExchangeId(exchangeId);
        scheduledSlot.setStartTime(startTime);
        scheduledSlot.setEndTime(endTime);
        scheduledSlot.setTitle("Test Meeting");
        scheduledSlot.setDescription("Test Description");
        scheduledSlot.setConfirmed(false);
        scheduledSlot.setCreatedAt(now);
        scheduledSlot.setUpdatedAt(now);

        scheduledSlotDto = new ScheduledSlotDto();
        scheduledSlotDto.setId(scheduledSlotId);
        scheduledSlotDto.setUserId(userId);
        scheduledSlotDto.setExchangeId(exchangeId);
        scheduledSlotDto.setStartTime(startTime);
        scheduledSlotDto.setEndTime(endTime);
        scheduledSlotDto.setTitle("Test Meeting");
        scheduledSlotDto.setDescription("Test Description");
        scheduledSlotDto.setConfirmed(false);
    }

    @Test
    void createScheduledSlot_ShouldCreateSuccessfully() {
        // Arrange
        when(scheduledSlotRepository.findByExchangeId(exchangeId)).thenReturn(Optional.empty());
        when(modelMapper.map(scheduledSlotDto, ScheduledSlot.class)).thenReturn(scheduledSlot);
        when(scheduledSlotRepository.save(any(ScheduledSlot.class))).thenReturn(scheduledSlot);
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        ScheduledSlotDto result = scheduledSlotService.createScheduledSlot(scheduledSlotDto);

        // Assert
        assertNotNull(result);
        assertEquals(scheduledSlotDto.getId(), result.getId());
        assertEquals(scheduledSlotDto.getUserId(), result.getUserId());
        assertEquals(scheduledSlotDto.getExchangeId(), result.getExchangeId());
        assertEquals(scheduledSlotDto.getStartTime(), result.getStartTime());
        assertEquals(scheduledSlotDto.getEndTime(), result.getEndTime());
        assertEquals(scheduledSlotDto.getTitle(), result.getTitle());
        assertEquals(scheduledSlotDto.getDescription(), result.getDescription());
        assertFalse(result.isConfirmed());
        verify(scheduledSlotRepository).findByExchangeId(exchangeId);
        verify(scheduledSlotRepository).save(any(ScheduledSlot.class));
    }

    @Test
    void createScheduledSlot_WithExistingExchangeId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.findByExchangeId(exchangeId)).thenReturn(Optional.of(scheduledSlot));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> scheduledSlotService.createScheduledSlot(scheduledSlotDto));
        verify(scheduledSlotRepository).findByExchangeId(exchangeId);
        verify(scheduledSlotRepository, never()).save(any(ScheduledSlot.class));
    }

    @Test
    void getScheduledSlotById_WithValidId_ShouldReturnScheduledSlot() {
        // Arrange
        when(scheduledSlotRepository.findById(scheduledSlotId)).thenReturn(Optional.of(scheduledSlot));
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        ScheduledSlotDto result = scheduledSlotService.getScheduledSlotById(scheduledSlotId);

        // Assert
        assertNotNull(result);
        assertEquals(scheduledSlotDto.getId(), result.getId());
        assertEquals(scheduledSlotDto.getUserId(), result.getUserId());
        assertEquals(scheduledSlotDto.getExchangeId(), result.getExchangeId());
        verify(scheduledSlotRepository).findById(scheduledSlotId);
    }

    @Test
    void getScheduledSlotById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> scheduledSlotService.getScheduledSlotById(UUID.randomUUID()));
        verify(scheduledSlotRepository).findById(any(UUID.class));
    }

    @Test
    void getScheduledSlotByExchangeId_WithValidId_ShouldReturnScheduledSlot() {
        // Arrange
        when(scheduledSlotRepository.findByExchangeId(exchangeId)).thenReturn(Optional.of(scheduledSlot));
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        ScheduledSlotDto result = scheduledSlotService.getScheduledSlotByExchangeId(exchangeId);

        // Assert
        assertNotNull(result);
        assertEquals(scheduledSlotDto.getId(), result.getId());
        assertEquals(scheduledSlotDto.getExchangeId(), result.getExchangeId());
        verify(scheduledSlotRepository).findByExchangeId(exchangeId);
    }

    @Test
    void getScheduledSlotByExchangeId_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.findByExchangeId(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> scheduledSlotService.getScheduledSlotByExchangeId(UUID.randomUUID()));
        verify(scheduledSlotRepository).findByExchangeId(any(UUID.class));
    }

    @Test
    void getScheduledSlotsByUserId_ShouldReturnScheduledSlots() {
        // Arrange
        List<ScheduledSlot> scheduledSlots = Arrays.asList(scheduledSlot);
        when(scheduledSlotRepository.findByUserId(userId)).thenReturn(scheduledSlots);
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        List<ScheduledSlotDto> result = scheduledSlotService.getScheduledSlotsByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(scheduledSlotDto.getId(), result.get(0).getId());
        assertEquals(scheduledSlotDto.getUserId(), result.get(0).getUserId());
        verify(scheduledSlotRepository).findByUserId(userId);
    }

    @Test
    void getScheduledSlotsByUserIdAndDateRange_ShouldReturnScheduledSlots() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        List<ScheduledSlot> scheduledSlots = Arrays.asList(scheduledSlot);
        
        when(scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end)).thenReturn(scheduledSlots);
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        List<ScheduledSlotDto> result = scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(userId, start, end);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(scheduledSlotDto.getId(), result.get(0).getId());
        assertEquals(scheduledSlotDto.getUserId(), result.get(0).getUserId());
        verify(scheduledSlotRepository).findByUserIdAndStartTimeBetween(userId, start, end);
    }

    @Test
    void updateScheduledSlot_ShouldUpdateSuccessfully() {
        // Arrange
        when(scheduledSlotRepository.findById(scheduledSlotId)).thenReturn(Optional.of(scheduledSlot));
        when(scheduledSlotRepository.save(any(ScheduledSlot.class))).thenReturn(scheduledSlot);
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        LocalDateTime newStartTime = LocalDateTime.now().plusHours(3);
        LocalDateTime newEndTime = LocalDateTime.now().plusHours(4);
        
        ScheduledSlotDto updatedDto = new ScheduledSlotDto();
        updatedDto.setStartTime(newStartTime);
        updatedDto.setEndTime(newEndTime);
        updatedDto.setTitle("Updated Title");
        updatedDto.setDescription("Updated Description");

        // Act
        ScheduledSlotDto result = scheduledSlotService.updateScheduledSlot(scheduledSlotId, updatedDto);

        // Assert
        assertNotNull(result);
        // Verify the entity was updated with new values
        assertEquals(newStartTime, scheduledSlot.getStartTime());
        assertEquals(newEndTime, scheduledSlot.getEndTime());
        assertEquals("Updated Title", scheduledSlot.getTitle());
        assertEquals("Updated Description", scheduledSlot.getDescription());
        verify(scheduledSlotRepository).findById(scheduledSlotId);
        verify(scheduledSlotRepository).save(scheduledSlot);
    }

    @Test
    void updateScheduledSlot_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            scheduledSlotService.updateScheduledSlot(UUID.randomUUID(), scheduledSlotDto));
        verify(scheduledSlotRepository).findById(any(UUID.class));
        verify(scheduledSlotRepository, never()).save(any(ScheduledSlot.class));
    }

    @Test
    void confirmScheduledSlot_ShouldConfirmSuccessfully() {
        // Arrange
        when(scheduledSlotRepository.findById(scheduledSlotId)).thenReturn(Optional.of(scheduledSlot));
        when(scheduledSlotRepository.save(any(ScheduledSlot.class))).thenReturn(scheduledSlot);
        
        // Update DTO to reflect confirmation
        scheduledSlotDto.setConfirmed(true);
        when(modelMapper.map(scheduledSlot, ScheduledSlotDto.class)).thenReturn(scheduledSlotDto);

        // Act
        ScheduledSlotDto result = scheduledSlotService.confirmScheduledSlot(scheduledSlotId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isConfirmed());
        assertTrue(scheduledSlot.isConfirmed());
        verify(scheduledSlotRepository).findById(scheduledSlotId);
        verify(scheduledSlotRepository).save(scheduledSlot);
    }

    @Test
    void confirmScheduledSlot_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            scheduledSlotService.confirmScheduledSlot(UUID.randomUUID()));
        verify(scheduledSlotRepository).findById(any(UUID.class));
        verify(scheduledSlotRepository, never()).save(any(ScheduledSlot.class));
    }

    @Test
    void deleteScheduledSlot_WithValidId_ShouldDeleteSuccessfully() {
        // Arrange
        when(scheduledSlotRepository.existsById(scheduledSlotId)).thenReturn(true);
        doNothing().when(scheduledSlotRepository).deleteById(scheduledSlotId);

        // Act
        scheduledSlotService.deleteScheduledSlot(scheduledSlotId);

        // Assert
        verify(scheduledSlotRepository).existsById(scheduledSlotId);
        verify(scheduledSlotRepository).deleteById(scheduledSlotId);
    }

    @Test
    void deleteScheduledSlot_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(scheduledSlotRepository.existsById(any(UUID.class))).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            scheduledSlotService.deleteScheduledSlot(UUID.randomUUID()));
        verify(scheduledSlotRepository).existsById(any(UUID.class));
        verify(scheduledSlotRepository, never()).deleteById(any(UUID.class));
    }
}