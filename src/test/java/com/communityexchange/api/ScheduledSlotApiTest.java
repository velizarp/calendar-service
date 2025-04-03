package com.communityexchange.api;

import com.communityexchange.model.dto.ScheduledSlotDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ScheduledSlotApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void scheduledSlotCrudOperations_ShouldWorkCorrectly() throws Exception {
        // Create test data
        UUID userId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusDays(1).withHour(10).withMinute(0);
        LocalDateTime endTime = now.plusDays(1).withHour(11).withMinute(0);

        ScheduledSlotDto slotDto = new ScheduledSlotDto();
        slotDto.setUserId(userId);
        slotDto.setExchangeId(exchangeId);
        slotDto.setStartTime(startTime);
        slotDto.setEndTime(endTime);
        slotDto.setTitle("API Test Meeting");
        slotDto.setDescription("Testing scheduled slot API");

        // Test creation
        MvcResult createResult = mockMvc.perform(post("/scheduled-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(slotDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API Test Meeting"))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.exchangeId").value(exchangeId.toString()))
                .andReturn();

        String createResponseJson = createResult.getResponse().getContentAsString();
        ScheduledSlotDto createdSlot = objectMapper.readValue(createResponseJson, ScheduledSlotDto.class);
        UUID createdId = createdSlot.getId();
        assertNotNull(createdId);

        // Test get by ID
        mockMvc.perform(get("/scheduled-slots/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.title").value("API Test Meeting"));

        // Test get by exchange ID
        mockMvc.perform(get("/scheduled-slots/exchange/{exchangeId}", exchangeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.exchangeId").value(exchangeId.toString()));

        // Test get by user ID
        mockMvc.perform(get("/scheduled-slots/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdId.toString()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        // Test get by date range
        String startParam = startTime.minusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);
        String endParam = endTime.plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME);
        
        mockMvc.perform(get("/scheduled-slots/user/{userId}/date-range", userId)
                .param("start", startParam)
                .param("end", endParam))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(createdId.toString()));

        // Test update
        ScheduledSlotDto updateDto = new ScheduledSlotDto();
        updateDto.setUserId(userId);
        updateDto.setExchangeId(exchangeId);
        updateDto.setTitle("Updated API Meeting");
        updateDto.setDescription("Updated API description");
        updateDto.setStartTime(startTime.plusHours(1));
        updateDto.setEndTime(endTime.plusHours(1));

        mockMvc.perform(put("/scheduled-slots/{id}", createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated API Meeting"))
                .andExpect(jsonPath("$.description").value("Updated API description"));

        // Test confirm
        mockMvc.perform(put("/scheduled-slots/{id}/confirm", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdId.toString()))
                .andExpect(jsonPath("$.confirmed").value(true));

        // Test delete
        mockMvc.perform(delete("/scheduled-slots/{id}", createdId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/scheduled-slots/{id}", createdId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createScheduledSlot_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Missing required fields
        ScheduledSlotDto invalidDto = new ScheduledSlotDto();
        invalidDto.setTitle("Invalid Meeting");
        // Missing userId, exchangeId, startTime, endTime

        mockMvc.perform(post("/scheduled-slots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getScheduledSlotById_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        mockMvc.perform(get("/scheduled-slots/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createMultipleScheduledSlotsForUser_ShouldWorkCorrectly() throws Exception {
        // Create test data
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Create 3 slots for the same user
        for (int i = 0; i < 3; i++) {
            ScheduledSlotDto slotDto = new ScheduledSlotDto();
            slotDto.setUserId(userId);
            slotDto.setExchangeId(UUID.randomUUID());  // Each slot needs a unique exchange ID
            slotDto.setStartTime(now.plusDays(i).withHour(10));
            slotDto.setEndTime(now.plusDays(i).withHour(11));
            slotDto.setTitle("API Meeting " + (i + 1));
            slotDto.setDescription("API Description " + (i + 1));

            mockMvc.perform(post("/scheduled-slots")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(slotDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("API Meeting " + (i + 1)));
        }

        // Verify all slots are returned
        mockMvc.perform(get("/scheduled-slots/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
}