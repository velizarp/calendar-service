package com.communityexchange.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "availabilities")
@Getter
@Setter
@NoArgsConstructor
public class Availability extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "user_calendar_id", nullable = false)
    private UserCalendar userCalendar;
    
    @Column(name = "day_of_week", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring = true;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}