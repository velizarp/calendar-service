package com.communityexchange.web.controller;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.service.ScheduledSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduledSlotController.class)
public class ScheduledSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduledSlotService scheduledSlotService;

    private ScheduledSlotDto scheduledSlotDto;
    private final UUID scheduledSlotId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID exchangeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusHours(1);
        LocalDateTime endTime = now.plusHours(2);

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
    void createScheduledSlot_ShouldReturnCreatedScheduledSlot() throws Exception {
        when(scheduledSlotService.createScheduledSlot(any(ScheduledSlotDto.class))).thenReturn(scheduledSlotDto);

        mockMvc.perform(post("/scheduled-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scheduledSlotDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.exchangeId").value(exchangeId.toString()))
                .andExpect(jsonPath("$.title").value("Test Meeting"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.confirmed").value(false));

        verify(scheduledSlotService).createScheduledSlot(any(ScheduledSlotDto.class));
    }

    @Test
    void getScheduledSlotById_WithValidId_ShouldReturnScheduledSlot() throws Exception {
        when(scheduledSlotService.getScheduledSlotById(scheduledSlotId)).thenReturn(scheduledSlotDto);

        mockMvc.perform(get("/scheduled-slots/{id}", scheduledSlotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.exchangeId").value(exchangeId.toString()))
                .andExpect(jsonPath("$.title").value("Test Meeting"));

        verify(scheduledSlotService).getScheduledSlotById(scheduledSlotId);
    }

    @Test
    void getScheduledSlotById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(scheduledSlotService.getScheduledSlotById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Scheduled slot not found"));

        mockMvc.perform(get("/scheduled-slots/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(scheduledSlotService).getScheduledSlotById(any(UUID.class));
    }

    @Test
    void getScheduledSlotByExchangeId_WithValidId_ShouldReturnScheduledSlot() throws Exception {
        when(scheduledSlotService.getScheduledSlotByExchangeId(exchangeId)).thenReturn(scheduledSlotDto);

        mockMvc.perform(get("/scheduled-slots/exchange/{exchangeId}", exchangeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$.exchangeId").value(exchangeId.toString()));

        verify(scheduledSlotService).getScheduledSlotByExchangeId(exchangeId);
    }

    @Test
    void getScheduledSlotsByUserId_ShouldReturnListOfScheduledSlots() throws Exception {
        List<ScheduledSlotDto> scheduledSlots = Arrays.asList(scheduledSlotDto);
        when(scheduledSlotService.getScheduledSlotsByUserId(userId)).thenReturn(scheduledSlots);

        mockMvc.perform(get("/scheduled-slots/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(scheduledSlotService).getScheduledSlotsByUserId(userId);
    }

    @Test
    void getScheduledSlotsByUserIdAndDateRange_ShouldReturnListOfScheduledSlots() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        List<ScheduledSlotDto> scheduledSlots = Arrays.asList(scheduledSlotDto);
        when(scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(scheduledSlots);

        mockMvc.perform(get("/scheduled-slots/user/{userId}/date-range", userId)
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(scheduledSlotService).getScheduledSlotsByUserIdAndDateRange(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void updateScheduledSlot_WithValidId_ShouldReturnUpdatedScheduledSlot() throws Exception {
        ScheduledSlotDto updatedDto = new ScheduledSlotDto();
        updatedDto.setId(scheduledSlotId);
        updatedDto.setUserId(userId);
        updatedDto.setExchangeId(exchangeId);
        updatedDto.setTitle("Updated Title");
        updatedDto.setDescription("Updated Description");
        updatedDto.setStartTime(LocalDateTime.now().plusHours(3));
        updatedDto.setEndTime(LocalDateTime.now().plusHours(4));

        when(scheduledSlotService.updateScheduledSlot(eq(scheduledSlotId), any(ScheduledSlotDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/scheduled-slots/{id}", scheduledSlotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(scheduledSlotService).updateScheduledSlot(eq(scheduledSlotId), any(ScheduledSlotDto.class));
    }

    @Test
    void confirmScheduledSlot_WithValidId_ShouldReturnConfirmedScheduledSlot() throws Exception {
        ScheduledSlotDto confirmedDto = new ScheduledSlotDto();
        confirmedDto.setId(scheduledSlotId);
        confirmedDto.setUserId(userId);
        confirmedDto.setExchangeId(exchangeId);
        confirmedDto.setConfirmed(true);
        confirmedDto.setTitle("Test Meeting");
        confirmedDto.setStartTime(LocalDateTime.now().plusHours(1));
        confirmedDto.setEndTime(LocalDateTime.now().plusHours(2));

        when(scheduledSlotService.confirmScheduledSlot(scheduledSlotId)).thenReturn(confirmedDto);

        mockMvc.perform(put("/scheduled-slots/{id}/confirm", scheduledSlotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduledSlotId.toString()))
                .andExpect(jsonPath("$.confirmed").value(true));

        verify(scheduledSlotService).confirmScheduledSlot(scheduledSlotId);
    }

    @Test
    void deleteScheduledSlot_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(scheduledSlotService).deleteScheduledSlot(scheduledSlotId);

        mockMvc.perform(delete("/scheduled-slots/{id}", scheduledSlotId))
                .andExpect(status().isNoContent());

        verify(scheduledSlotService).deleteScheduledSlot(scheduledSlotId);
    }

    @Test
    void deleteScheduledSlot_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Scheduled slot not found"))
                .when(scheduledSlotService).deleteScheduledSlot(any(UUID.class));

        mockMvc.perform(delete("/scheduled-slots/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(scheduledSlotService).deleteScheduledSlot(any(UUID.class));
    }
}