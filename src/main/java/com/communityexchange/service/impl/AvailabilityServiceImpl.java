package com.communityexchange.service.impl;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.AvailabilityDto;
import com.communityexchange.model.entity.Availability;
import com.communityexchange.model.entity.UserCalendar;
import com.communityexchange.repository.AvailabilityRepository;
import com.communityexchange.repository.UserCalendarRepository;
import com.communityexchange.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final UserCalendarRepository userCalendarRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public AvailabilityDto createAvailability(AvailabilityDto availabilityDto) {
        UserCalendar userCalendar = userCalendarRepository.findById(availabilityDto.getUserCalendarId())
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found with id: " + availabilityDto.getUserCalendarId()));

        Availability availability = modelMapper.map(availabilityDto, Availability.class);
        availability.setUserCalendar(userCalendar);

        Availability savedAvailability = availabilityRepository.save(availability);
        return modelMapper.map(savedAvailability, AvailabilityDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityDto getAvailabilityById(UUID id) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + id));

        return modelMapper.map(availability, AvailabilityDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDto> getAvailabilitiesByUserCalendar(UUID userCalendarId) {
        UserCalendar userCalendar = userCalendarRepository.findById(userCalendarId)
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found with id: " + userCalendarId));

        return availabilityRepository.findByUserCalendar(userCalendar).stream()
                .map(availability -> modelMapper.map(availability, AvailabilityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityDto> getAvailabilitiesByUserCalendarAndDayOfWeek(UUID userCalendarId, DayOfWeek dayOfWeek) {
        UserCalendar userCalendar = userCalendarRepository.findById(userCalendarId)
                .orElseThrow(() -> new ResourceNotFoundException("User calendar not found with id: " + userCalendarId));

        return availabilityRepository.findByUserCalendarAndDayOfWeek(userCalendar, dayOfWeek).stream()
                .map(availability -> modelMapper.map(availability, AvailabilityDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AvailabilityDto updateAvailability(UUID id, AvailabilityDto availabilityDto) {
        Availability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + id));

        availability.setDayOfWeek(availabilityDto.getDayOfWeek());
        availability.setStartTime(availabilityDto.getStartTime());
        availability.setEndTime(availabilityDto.getEndTime());
        availability.setRecurring(availabilityDto.isRecurring());
        availability.setActive(availabilityDto.isActive());

        Availability updatedAvailability = availabilityRepository.save(availability);
        return modelMapper.map(updatedAvailability, AvailabilityDto.class);
    }

    @Override
    @Transactional
    public void deleteAvailability(UUID id) {
        if (!availabilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Availability not found with id: " + id);
        }
        
        availabilityRepository.deleteById(id);
    }
}