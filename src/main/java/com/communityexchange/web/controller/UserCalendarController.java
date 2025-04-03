package com.communityexchange.web.controller;

import com.communityexchange.model.dto.AvailabilitySlotDto;
import com.communityexchange.model.dto.UserCalendarDto;
import com.communityexchange.service.UserCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/calendars")
@RequiredArgsConstructor
public class UserCalendarController {
    
    private final UserCalendarService userCalendarService;
    
    @PostMapping
    public ResponseEntity<UserCalendarDto> createUserCalendar(@RequestParam UUID userId) {
        UserCalendarDto createdCalendar = userCalendarService.createUserCalendar(userId);
        return new ResponseEntity<>(createdCalendar, HttpStatus.CREATED);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserCalendarDto> getUserCalendar(@PathVariable UUID userId) {
        UserCalendarDto userCalendar = userCalendarService.getUserCalendar(userId);
        return ResponseEntity.ok(userCalendar);
    }
    
    @GetMapping("/{userId}/available-slots")
    public ResponseEntity<List<AvailabilitySlotDto>> getAvailableSlots(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<AvailabilitySlotDto> availableSlots = userCalendarService.getAvailableSlots(userId, start, end);
        return ResponseEntity.ok(availableSlots);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserCalendar(@PathVariable UUID userId) {
        userCalendarService.deleteUserCalendar(userId);
        return ResponseEntity.noContent().build();
    }
}