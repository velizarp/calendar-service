package com.communityexchange.web.controller;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.service.AvailabilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityController.class)
public class AvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AvailabilityService availabilityService;

    private AvailabilityDto availabilityDto;
    private final UUID availabilityId = UUID.randomUUID();
    private final UUID userCalendarId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
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
    void createAvailability_ShouldReturnCreatedAvailability() throws Exception {
        when(availabilityService.createAvailability(any(AvailabilityDto.class))).thenReturn(availabilityDto);

        mockMvc.perform(post("/availabilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availabilityDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(availabilityId.toString()))
                .andExpect(jsonPath("$.userCalendarId").value(userCalendarId.toString()))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("09:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:00:00"))
                .andExpect(jsonPath("$.recurring").value(true))
                .andExpect(jsonPath("$.active").value(true));

        verify(availabilityService).createAvailability(any(AvailabilityDto.class));
    }

    @Test
    void getAvailabilityById_WithValidId_ShouldReturnAvailability() throws Exception {
        when(availabilityService.getAvailabilityById(availabilityId)).thenReturn(availabilityDto);

        mockMvc.perform(get("/availabilities/{id}", availabilityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(availabilityId.toString()))
                .andExpect(jsonPath("$.userCalendarId").value(userCalendarId.toString()))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"));

        verify(availabilityService).getAvailabilityById(availabilityId);
    }

    @Test
    void getAvailabilityById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(availabilityService.getAvailabilityById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Availability not found"));

        mockMvc.perform(get("/availabilities/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(availabilityService).getAvailabilityById(any(UUID.class));
    }

    @Test
    void getAvailabilitiesByUserCalendar_ShouldReturnAvailabilities() throws Exception {
        List<AvailabilityDto> availabilities = Arrays.asList(availabilityDto);
        when(availabilityService.getAvailabilitiesByUserCalendar(userCalendarId)).thenReturn(availabilities);

        mockMvc.perform(get("/availabilities/user-calendar/{userCalendarId}", userCalendarId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(availabilityId.toString()))
                .andExpect(jsonPath("$[0].userCalendarId").value(userCalendarId.toString()));

        verify(availabilityService).getAvailabilitiesByUserCalendar(userCalendarId);
    }

    @Test
    void getAvailabilitiesByUserCalendarAndDayOfWeek_ShouldReturnAvailabilities() throws Exception {
        List<AvailabilityDto> availabilities = Arrays.asList(availabilityDto);
        when(availabilityService.getAvailabilitiesByUserCalendarAndDayOfWeek(
                userCalendarId, DayOfWeek.MONDAY)).thenReturn(availabilities);

        mockMvc.perform(get("/availabilities/user-calendar/{userCalendarId}/day/{dayOfWeek}", 
                userCalendarId, DayOfWeek.MONDAY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(availabilityId.toString()))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));

        verify(availabilityService).getAvailabilitiesByUserCalendarAndDayOfWeek(userCalendarId, DayOfWeek.MONDAY);
    }

    @Test
    void updateAvailability_WithValidId_ShouldReturnUpdatedAvailability() throws Exception {
        AvailabilityDto updatedDto = new AvailabilityDto();
        updatedDto.setId(availabilityId);
        updatedDto.setUserCalendarId(userCalendarId);
        updatedDto.setDayOfWeek(DayOfWeek.TUESDAY);
        updatedDto.setStartTime(LocalTime.of(10, 0));
        updatedDto.setEndTime(LocalTime.of(18, 0));
        updatedDto.setRecurring(false);
        updatedDto.setActive(false);

        when(availabilityService.updateAvailability(eq(availabilityId), any(AvailabilityDto.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/availabilities/{id}", availabilityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(availabilityId.toString()))
                .andExpect(jsonPath("$.dayOfWeek").value("TUESDAY"))
                .andExpect(jsonPath("$.startTime").value("10:00:00"))
                .andExpect(jsonPath("$.endTime").value("18:00:00"))
                .andExpect(jsonPath("$.recurring").value(false))
                .andExpect(jsonPath("$.active").value(false));

        verify(availabilityService).updateAvailability(eq(availabilityId), any(AvailabilityDto.class));
    }

    @Test
    void updateAvailability_WithInvalidId_ShouldReturnNotFound() throws Exception {
        when(availabilityService.updateAvailability(any(UUID.class), any(AvailabilityDto.class)))
                .thenThrow(new ResourceNotFoundException("Availability not found"));

        mockMvc.perform(put("/availabilities/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(availabilityDto)))
                .andExpect(status().isNotFound());

        verify(availabilityService).updateAvailability(any(UUID.class), any(AvailabilityDto.class));
    }

    @Test
    void deleteAvailability_WithValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(availabilityService).deleteAvailability(availabilityId);

        mockMvc.perform(delete("/availabilities/{id}", availabilityId))
                .andExpect(status().isNoContent());

        verify(availabilityService).deleteAvailability(availabilityId);
    }

    @Test
    void deleteAvailability_WithInvalidId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Availability not found"))
                .when(availabilityService).deleteAvailability(any(UUID.class));

        mockMvc.perform(delete("/availabilities/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());

        verify(availabilityService).deleteAvailability(any(UUID.class));
    }
}