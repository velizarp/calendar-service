package com.communityexchange.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlotDto {
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private UUID userId;
}