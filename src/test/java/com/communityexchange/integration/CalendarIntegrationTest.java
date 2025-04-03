package com.communityexchange.integration;

import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.model.dto.UserCalendarDto;
import com.communityexchange.model.entity.UserCalendar;
import com.communityexchange.repository.AvailabilityRepository;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.repository.UserCalendarRepository;
import com.communityexchange.service.AvailabilityService;
import com.communityexchange.service.ScheduledSlotService;
import com.communityexchange.service.UserCalendarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CalendarIntegrationTest {

    @Autowired
    private UserCalendarService userCalendarService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private ScheduledSlotService scheduledSlotService;

    @Autowired
    private UserCalendarRepository userCalendarRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private ScheduledSlotRepository scheduledSlotRepository;

    @Test
    void createUserCalendarWithAvailabilityAndScheduleSlot_ShouldWorkCorrectly() {
        // Create a UUID for a user
        UUID userId = UUID.randomUUID();

        // Create a user calendar
        UserCalendarDto userCalendarDto = userCalendarService.createUserCalendar(userId);
        assertNotNull(userCalendarDto);
        assertEquals(userId, userCalendarDto.getUserId());

        // Create availability for this calendar
        AvailabilityDto availabilityDto = new AvailabilityDto();
        availabilityDto.setUserCalendarId(userCalendarDto.getId());
        availabilityDto.setDayOfWeek(DayOfWeek.MONDAY);
        availabilityDto.setStartTime(LocalTime.of(9, 0));
        availabilityDto.setEndTime(LocalTime.of(17, 0));
        availabilityDto.setRecurring(true);
        availabilityDto.setActive(true);

        AvailabilityDto savedAvailabilityDto = availabilityService.createAvailability(availabilityDto);
        assertNotNull(savedAvailabilityDto);
        assertEquals(DayOfWeek.MONDAY, savedAvailabilityDto.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), savedAvailabilityDto.getStartTime());
        assertEquals(LocalTime.of(17, 0), savedAvailabilityDto.getEndTime());

        // Create a scheduled slot based on availability
        ScheduledSlotDto slotDto = new ScheduledSlotDto();
        slotDto.setUserId(userId);
        slotDto.setExchangeId(UUID.randomUUID()); // Mock exchange ID
        slotDto.setStartTime(LocalDateTime.now().plusDays(7).withHour(10).withMinute(0));
        slotDto.setEndTime(LocalDateTime.now().plusDays(7).withHour(11).withMinute(0));
        slotDto.setTitle("Integration Test Meeting");
        slotDto.setDescription("Testing calendar integration");

        ScheduledSlotDto savedSlotDto = scheduledSlotService.createScheduledSlot(slotDto);
        assertNotNull(savedSlotDto);
        assertEquals("Integration Test Meeting", savedSlotDto.getTitle());
        assertEquals("Testing calendar integration", savedSlotDto.getDescription());
        
        // Verify that we can retrieve the calendar with its availability slots
        UserCalendarDto retrievedCalendar = userCalendarService.getUserCalendar(userId);
        assertNotNull(retrievedCalendar);
        
        List<AvailabilityDto> availabilities = availabilityService.getAvailabilitiesByUserCalendar(retrievedCalendar.getId());
        assertNotNull(availabilities);
        assertFalse(availabilities.isEmpty());
        
        List<ScheduledSlotDto> scheduledSlots = scheduledSlotService.getScheduledSlotsByUserId(userId);
        assertNotNull(scheduledSlots);
        assertFalse(scheduledSlots.isEmpty());
        
        // Verify that we can retrieve by day of week
        List<AvailabilityDto> mondayAvailabilities = availabilityService.getAvailabilitiesByUserCalendarAndDayOfWeek(
                retrievedCalendar.getId(), DayOfWeek.MONDAY);
        assertNotNull(mondayAvailabilities);
        assertFalse(mondayAvailabilities.isEmpty());
        
        // Verify that updating works
        AvailabilityDto toUpdate = mondayAvailabilities.get(0);
        toUpdate.setStartTime(LocalTime.of(10, 0));
        AvailabilityDto updatedAvailability = availabilityService.updateAvailability(toUpdate.getId(), toUpdate);
        assertEquals(LocalTime.of(10, 0), updatedAvailability.getStartTime());
        
        // Get the UserCalendar entity from the repository for verification later
        UserCalendar userCalendar = userCalendarRepository.findByUserId(userId).orElse(null);
        assertNotNull(userCalendar, "User calendar should exist");
        
        // Cleanup - not strictly necessary with @Transactional, but good for clarity
        scheduledSlotService.deleteScheduledSlot(savedSlotDto.getId());
        availabilityService.deleteAvailability(savedAvailabilityDto.getId());
        
        // Verify partial cleanup
        assertTrue(scheduledSlotRepository.findByUserId(userId).isEmpty(), "Scheduled slots should be empty after deletion");
        assertTrue(availabilityRepository.findByUserCalendar(userCalendar).isEmpty(), "Availabilities should be empty after deletion");
        
        // Now delete the user calendar
        userCalendarService.deleteUserCalendar(userId);
        
        // Verify user calendar deletion
        assertFalse(userCalendarRepository.findByUserId(userId).isPresent(), "User calendar should not exist after deletion");
    }
}