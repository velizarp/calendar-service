package com.communityexchange.repository;

import com.communityexchange.model.entity.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCalendarRepository extends JpaRepository<UserCalendar, UUID> {
    
    Optional<UserCalendar> findByUserId(UUID userId);
}