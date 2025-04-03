package com.communityexchange.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user_calendars")
@Getter
@Setter
@NoArgsConstructor
public class UserCalendar extends BaseEntity {
    
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "userCalendar")
    private Set<Availability> availabilities = new HashSet<>();
    
    public void addAvailability(Availability availability) {
        availabilities.add(availability);
        availability.setUserCalendar(this);
    }
    
    public void removeAvailability(Availability availability) {
        availabilities.remove(availability);
        availability.setUserCalendar(null);
    }
}