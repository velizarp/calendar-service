package com.communityexchange.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ScheduledSlotDto {
    
    private UUID id;
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Exchange ID is required")
    private UUID exchangeId;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    @Size(max = 100, message = "Title must be less than 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    private boolean isConfirmed = false;
}