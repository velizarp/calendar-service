package com.communityexchange.web.controller;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilitySlotDto;
import com.communityexchange.model.dto.UserCalendarDto;
import com.communityexchange.service.UserCalendarService;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCalendarController.class)
public class UserCalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserCalendarService userCalendarService;

    private UserCalendarDto userCalendarDto;
    private final UUID userId = UUID.randomUUID();
    private final UUID calendarId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        userCalendarDto = new UserCalendarDto();
        userCalendarDto.setId(calendarId);
        userCalendarDto.setUserId(userId);
        userCalendarDto.setAvailabilities(Collections.emptyList());
    }

    @Test
    void createUserCalendar_ShouldReturnCreatedCalendar() throws Exception {
        when(userCalendarService.createUserCalendar(userId)).thenReturn(userCalendarDto);

        mockMvc.perform(post("/calendars")
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(calendarId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(userCalendarService).createUserCalendar(userId);
    }

    @Test
    void getUserCalendar_WithValidId_ShouldReturnCalendar() throws Exception {
        when(userCalendarService.getUserCalendar(userId)).thenReturn(userCalendarDto);

        mockMvc.perform(get("/calendars/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(calendarId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        verify(userCalendarService).getUserCalendar(userId);
    }

    @Test
    void getUserCalendar_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(userCalendarService.getUserCalendar(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("User calendar not found"));

        mockMvc.perform(get("/calendars/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(userCalendarService).getUserCalendar(any(UUID.class));
    }

    @Test
    void getAvailableSlots_ShouldReturnAvailableSlots() throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(7);
        
        AvailabilitySlotDto slot1 = new AvailabilitySlotDto();
        slot1.setStartTime(start.plusDays(1).withHour(10).withMinute(0));
        slot1.setEndTime(start.plusDays(1).withHour(11).withMinute(0));
        slot1.setUserId(userId);
        
        AvailabilitySlotDto slot2 = new AvailabilitySlotDto();
        slot2.setStartTime(start.plusDays(2).withHour(14).withMinute(0));
        slot2.setEndTime(start.plusDays(2).withHour(15).withMinute(0));
        slot2.setUserId(userId);
        
        List<AvailabilitySlotDto> availableSlots = Arrays.asList(slot1, slot2);
        
        when(userCalendarService.getAvailableSlots(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(availableSlots);

        mockMvc.perform(get("/calendars/{userId}/available-slots", userId)
                .param("start", start.toString())
                .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].startTime").exists())
                .andExpect(jsonPath("$[0].endTime").exists())
                .andExpect(jsonPath("$[1].startTime").exists())
                .andExpect(jsonPath("$[1].endTime").exists());

        verify(userCalendarService).getAvailableSlots(eq(userId), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void deleteUserCalendar_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userCalendarService).deleteUserCalendar(userId);

        mockMvc.perform(delete("/calendars/{userId}", userId))
                .andExpect(status().isNoContent());

        verify(userCalendarService).deleteUserCalendar(userId);
    }

    @Test
    void deleteUserCalendar_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User calendar not found"))
                .when(userCalendarService).deleteUserCalendar(any(UUID.class));

        mockMvc.perform(delete("/calendars/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(userCalendarService).deleteUserCalendar(any(UUID.class));
    }
}