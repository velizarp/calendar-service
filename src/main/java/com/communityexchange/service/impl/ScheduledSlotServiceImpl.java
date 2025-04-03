package com.communityexchange.service.impl;

import com.communityexchange.exception.ResourceNotFoundException;
import com.communityexchange.model.dto.ScheduledSlotDto;
import com.communityexchange.model.entity.ScheduledSlot;
import com.communityexchange.repository.ScheduledSlotRepository;
import com.communityexchange.service.ScheduledSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledSlotServiceImpl implements ScheduledSlotService {

    private final ScheduledSlotRepository scheduledSlotRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ScheduledSlotDto createScheduledSlot(ScheduledSlotDto scheduledSlotDto) {
        // Check if a slot already exists for the same exchange
        scheduledSlotRepository.findByExchangeId(scheduledSlotDto.getExchangeId())
                .ifPresent(slot -> {
                    throw new IllegalStateException("A scheduled slot already exists for this exchange");
                });

        ScheduledSlot scheduledSlot = modelMapper.map(scheduledSlotDto, ScheduledSlot.class);
        scheduledSlot.setCreatedAt(LocalDateTime.now());
        scheduledSlot.setUpdatedAt(LocalDateTime.now());

        ScheduledSlot savedSlot = scheduledSlotRepository.save(scheduledSlot);
        return modelMapper.map(savedSlot, ScheduledSlotDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduledSlotDto getScheduledSlotById(UUID id) {
        ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled slot not found with id: " + id));

        return modelMapper.map(scheduledSlot, ScheduledSlotDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduledSlotDto getScheduledSlotByExchangeId(UUID exchangeId) {
        ScheduledSlot scheduledSlot = scheduledSlotRepository.findByExchangeId(exchangeId)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled slot not found for exchange id: " + exchangeId));

        return modelMapper.map(scheduledSlot, ScheduledSlotDto.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduledSlotDto> getScheduledSlotsByUserId(UUID userId) {
        return scheduledSlotRepository.findByUserId(userId).stream()
                .map(slot -> modelMapper.map(slot, ScheduledSlotDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduledSlotDto> getScheduledSlotsByUserIdAndDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        return scheduledSlotRepository.findByUserIdAndStartTimeBetween(userId, start, end).stream()
                .map(slot -> modelMapper.map(slot, ScheduledSlotDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduledSlotDto updateScheduledSlot(UUID id, ScheduledSlotDto scheduledSlotDto) {
        ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled slot not found with id: " + id));

        scheduledSlot.setStartTime(scheduledSlotDto.getStartTime());
        scheduledSlot.setEndTime(scheduledSlotDto.getEndTime());
        scheduledSlot.setTitle(scheduledSlotDto.getTitle());
        scheduledSlot.setDescription(scheduledSlotDto.getDescription());
        scheduledSlot.setUpdatedAt(LocalDateTime.now());

        ScheduledSlot updatedSlot = scheduledSlotRepository.save(scheduledSlot);
        return modelMapper.map(updatedSlot, ScheduledSlotDto.class);
    }

    @Override
    @Transactional
    public ScheduledSlotDto confirmScheduledSlot(UUID id) {
        ScheduledSlot scheduledSlot = scheduledSlotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Scheduled slot not found with id: " + id));

        scheduledSlot.setConfirmed(true);
        scheduledSlot.setUpdatedAt(LocalDateTime.now());

        ScheduledSlot confirmedSlot = scheduledSlotRepository.save(scheduledSlot);
        return modelMapper.map(confirmedSlot, ScheduledSlotDto.class);
    }

    @Override
    @Transactional
    public void deleteScheduledSlot(UUID id) {
        if (!scheduledSlotRepository.existsById(id)) {
            throw new ResourceNotFoundException("Scheduled slot not found with id: " + id);
        }
        
        scheduledSlotRepository.deleteById(id);
    }
}