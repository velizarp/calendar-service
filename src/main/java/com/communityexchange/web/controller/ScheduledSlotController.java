package com.communityexchange.web.controller;

import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.service.ScheduledSlotService;
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
@RequestMapping("/scheduled-slots")
@RequiredArgsConstructor
public class ScheduledSlotController {
    
    private final ScheduledSlotService scheduledSlotService;
    
    @PostMapping
    public ResponseEntity<ScheduledSlotDto> createScheduledSlot(@Valid @RequestBody ScheduledSlotDto scheduledSlotDto) {
        ScheduledSlotDto createdSlot = scheduledSlotService.createScheduledSlot(scheduledSlotDto);
        return new ResponseEntity<>(createdSlot, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledSlotDto> getScheduledSlotById(@PathVariable UUID id) {
        ScheduledSlotDto scheduledSlot = scheduledSlotService.getScheduledSlotById(id);
        return ResponseEntity.ok(scheduledSlot);
    }
    
    @GetMapping("/exchange/{exchangeId}")
    public ResponseEntity<ScheduledSlotDto> getScheduledSlotByExchangeId(@PathVariable UUID exchangeId) {
        ScheduledSlotDto scheduledSlot = scheduledSlotService.getScheduledSlotByExchangeId(exchangeId);
        return ResponseEntity.ok(scheduledSlot);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScheduledSlotDto>> getScheduledSlotsByUserId(@PathVariable UUID userId) {
        List<ScheduledSlotDto> scheduledSlots = scheduledSlotService.getScheduledSlotsByUserId(userId);
        return ResponseEntity.ok(scheduledSlots);
    }
    
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<ScheduledSlotDto>> getScheduledSlotsByUserIdAndDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ScheduledSlotDto> scheduledSlots = scheduledSlotService.getScheduledSlotsByUserIdAndDateRange(userId, start, end);
        return ResponseEntity.ok(scheduledSlots);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledSlotDto> updateScheduledSlot(
            @PathVariable UUID id,
            @Valid @RequestBody ScheduledSlotDto scheduledSlotDto) {
        ScheduledSlotDto updatedSlot = scheduledSlotService.updateScheduledSlot(id, scheduledSlotDto);
        return ResponseEntity.ok(updatedSlot);
    }
    
    @PutMapping("/{id}/confirm")
    public ResponseEntity<ScheduledSlotDto> confirmScheduledSlot(@PathVariable UUID id) {
        ScheduledSlotDto confirmedSlot = scheduledSlotService.confirmScheduledSlot(id);
        return ResponseEntity.ok(confirmedSlot);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScheduledSlot(@PathVariable UUID id) {
        scheduledSlotService.deleteScheduledSlot(id);
        return ResponseEntity.noContent().build();
    }
}