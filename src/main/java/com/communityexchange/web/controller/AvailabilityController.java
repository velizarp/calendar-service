package com.communityexchange.web.controller;

import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/availabilities")
@RequiredArgsConstructor
public class AvailabilityController {
    
    private final AvailabilityService availabilityService;
    
    @PostMapping
    public ResponseEntity<AvailabilityDto> createAvailability(@Valid @RequestBody AvailabilityDto availabilityDto) {
        AvailabilityDto createdAvailability = availabilityService.createAvailability(availabilityDto);
        return new ResponseEntity<>(createdAvailability, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AvailabilityDto> getAvailabilityById(@PathVariable UUID id) {
        AvailabilityDto availability = availabilityService.getAvailabilityById(id);
        return ResponseEntity.ok(availability);
    }
    
    @GetMapping("/user-calendar/{userCalendarId}")
    public ResponseEntity<List<AvailabilityDto>> getAvailabilitiesByUserCalendar(@PathVariable UUID userCalendarId) {
        List<AvailabilityDto> availabilities = availabilityService.getAvailabilitiesByUserCalendar(userCalendarId);
        return ResponseEntity.ok(availabilities);
    }
    
    @GetMapping("/user-calendar/{userCalendarId}/day/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityDto>> getAvailabilitiesByUserCalendarAndDayOfWeek(
            @PathVariable UUID userCalendarId,
            @PathVariable DayOfWeek dayOfWeek) {
        List<AvailabilityDto> availabilities = availabilityService.getAvailabilitiesByUserCalendarAndDayOfWeek(userCalendarId, dayOfWeek);
        return ResponseEntity.ok(availabilities);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AvailabilityDto> updateAvailability(
            @PathVariable UUID id,
            @Valid @RequestBody AvailabilityDto availabilityDto) {
        AvailabilityDto updatedAvailability = availabilityService.updateAvailability(id, availabilityDto);
        return ResponseEntity.ok(updatedAvailability);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable UUID id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}