package com.communityexchange.repository;

import com.communityexchange.model.entity.Availability;
import com.communityexchange.model.entity.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    
    List<Availability> findByUserCalendar(UserCalendar userCalendar);
    
    List<Availability> findByUserCalendarAndDayOfWeek(UserCalendar userCalendar, DayOfWeek dayOfWeek);
    
    List<Availability> findByUserCalendarAndIsActiveTrue(UserCalendar userCalendar);
}