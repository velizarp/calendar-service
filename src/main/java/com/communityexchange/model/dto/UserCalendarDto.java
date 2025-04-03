package com.communityexchange.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCalendarDto {
    
    private UUID id;
    private UUID userId;
    private List<AvailabilityDto> availabilities;
}