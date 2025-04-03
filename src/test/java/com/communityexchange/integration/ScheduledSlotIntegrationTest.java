package com.communityexchange.integration;

import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.service.ScheduledSlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ScheduledSlotIntegrationTest {

    @Autowired
    private ScheduledSlotService scheduledSlotService;

    @Autowired
    private ScheduledSlotRepository scheduledSlotRepository;

    @Test
    void createAndManageScheduledSlot_ShouldWorkCorrectly() {
        // Create test data
        UUID userId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusDays(1).withHour(10).withMinute(0);
        LocalDateTime endTime = now.plusDays(1).withHour(11).withMinute(0);

        // Create a scheduled slot
        ScheduledSlotDto slotDto = new ScheduledSlotDto();
        slotDto.setUserId(userId);
        slotDto.setExchangeId(exchangeId);
        slotDto.setStartTime(startTime);
        slotDto.setEndTime(endTime);
        slotDto.setTitle("Integration Test Meeting");
        slotDto.setDescription("Testing scheduled slot integration");

        ScheduledSlotDto createdSlot = scheduledSlotService.createScheduledSlot(slotDto);

        // Verify the slot was created correctly
        assertNotNull(createdSlot);
        assertNotNull(createdSlot.getId());
        assertEquals(userId, createdSlot.getUserId());
        assertEquals(exchangeId, createdSlot.getExchangeId());
        assertEquals("Integration Test Meeting", createdSlot.getTitle());
        assertEquals("Testing scheduled slot integration", createdSlot.getDescription());
        assertFalse(createdSlot.isConfirmed());

        // Test retrieving by ID
        ScheduledSlotDto retrievedById = scheduledSlotService.getScheduledSlotById(createdSlot.getId());
        assertNotNull(retrievedById);
        assertEquals(createdSlot.getId(), retrievedById.getId());

        // Test retrieving by exchange ID
        ScheduledSlotDto retrievedByExchangeId = scheduledSlotService.getScheduledSlotByExchangeId(exchangeId);
        assertNotNull(retrievedByExchangeId);
        assertEquals(exchangeId, retrievedByExchangeId.getExchangeId());

        // Test retrieving by user ID
        List<ScheduledSlotDto> slotsByUser = scheduledSlotService.getScheduledSlotsByUserId(userId);
        assertNotNull(slotsByUser);
        assertFalse(slotsByUser.isEmpty());
        assertEquals(1, slotsByUser.size());
        assertEquals(createdSlot.getId(), slotsByUser.get(0).getId());

        // Test retrieving by date range
        List<ScheduledSlotDto> slotsByDateRange = scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(
                userId, now, now.plusDays(2));
        assertNotNull(slotsByDateRange);
        assertFalse(slotsByDateRange.isEmpty());
        assertEquals(1, slotsByDateRange.size());

        // Test updating
        ScheduledSlotDto updateDto = new ScheduledSlotDto();
        updateDto.setTitle("Updated Meeting Title");
        updateDto.setDescription("Updated meeting description");
        updateDto.setStartTime(startTime.plusHours(1));
        updateDto.setEndTime(endTime.plusHours(1));

        ScheduledSlotDto updatedSlot = scheduledSlotService.updateScheduledSlot(createdSlot.getId(), updateDto);
        assertNotNull(updatedSlot);
        assertEquals("Updated Meeting Title", updatedSlot.getTitle());
        assertEquals("Updated meeting description", updatedSlot.getDescription());
        assertEquals(startTime.plusHours(1), updatedSlot.getStartTime());

        // Test confirming
        ScheduledSlotDto confirmedSlot = scheduledSlotService.confirmScheduledSlot(createdSlot.getId());
        assertNotNull(confirmedSlot);
        assertTrue(confirmedSlot.isConfirmed());

        // Test deletion
        scheduledSlotService.deleteScheduledSlot(createdSlot.getId());
        assertTrue(scheduledSlotRepository.findById(createdSlot.getId()).isEmpty());
    }

    @Test
    void testMultipleSlots_ForSameUser() {
        // Create test data
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Create multiple slots for the same user
        for (int i = 0; i < 3; i++) {
            ScheduledSlotDto slotDto = new ScheduledSlotDto();
            slotDto.setUserId(userId);
            slotDto.setExchangeId(UUID.randomUUID());  // Each slot needs a unique exchange ID
            slotDto.setStartTime(now.plusDays(i).withHour(10));
            slotDto.setEndTime(now.plusDays(i).withHour(11));
            slotDto.setTitle("Meeting " + (i + 1));
            slotDto.setDescription("Description " + (i + 1));

            scheduledSlotService.createScheduledSlot(slotDto);
        }

        // Verify all slots were created
        List<ScheduledSlotDto> userSlots = scheduledSlotService.getScheduledSlotsByUserId(userId);
        assertNotNull(userSlots);
        assertEquals(3, userSlots.size());

        // Verify date range filtering works
        List<ScheduledSlotDto> dayOneSlots = scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(
                userId, now, now.plusDays(1));
        assertEquals(1, dayOneSlots.size());

        List<ScheduledSlotDto> dayTwoSlots = scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(
                userId, now.plusDays(1), now.plusDays(2));
        assertEquals(1, dayTwoSlots.size());

        // Clean up all created slots
        for (ScheduledSlotDto slot : userSlots) {
            scheduledSlotService.deleteScheduledSlot(slot.getId());
        }

        // Verify all slots were deleted
        List<ScheduledSlotDto> slotsAfterDeletion = scheduledSlotService.getScheduledSlotsByUserId(userId);
        assertTrue(slotsAfterDeletion.isEmpty());
    }
}