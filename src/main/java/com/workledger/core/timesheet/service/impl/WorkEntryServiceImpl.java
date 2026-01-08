package com.workledger.core.timesheet.service.impl;

import com.workledger.core.common.exception.BusinessValidationException;
import com.workledger.core.common.exception.ResourceNotFoundException;
import com.workledger.core.timesheet.domain.WorkEntry;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.UpdateWorkEntryRequest;
import com.workledger.core.timesheet.dto.WorkEntryResponse;
import com.workledger.core.timesheet.dto.WorkEntrySummary;
import com.workledger.core.timesheet.mapper.WorkEntryMapper;
import com.workledger.core.timesheet.repository.WorkEntryRepository;
import com.workledger.core.timesheet.service.WorkEntryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.workledger.core.common.util.ValidationUtils.*;

/**
 * Implementation of WorkEntryService interface.
 * Handles business logic for work entry management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkEntryServiceImpl implements WorkEntryService {

    private final WorkEntryRepository workEntryRepository;
    private final WorkEntryMapper workEntryMapper;

    @Override
    public WorkEntryResponse createWorkEntry(CreateWorkEntryRequest request) {
        requireNonNull(request, "CreateWorkEntryRequest must not be null");
        log.debug("Creating work entry: {}", request);
        validateWorkEntryRequest(request.workDate(), request.hoursSpent());

        WorkEntry workEntry = workEntryMapper.toEntity(request);
        // default status
        if(workEntry.getWorkEntryStatus() == null) {
            workEntry.setWorkEntryStatus(WorkEntryStatus.DRAFT);
        }

        WorkEntry savedEntry = workEntryRepository.save(workEntry);

        log.info("Successfully created work entry with id: {}", savedEntry.getId());
        return workEntryMapper.toResponse(savedEntry);
    }

    @Override
    public WorkEntryResponse updateWorkEntry(Long id, UpdateWorkEntryRequest request) {
        requireNonNull(request, "UpdateWorkEntryRequest must not be null");
        log.debug("Updating work entry with id: {}", id);
        validateWorkEntryRequest(request.workDate(), request.hoursSpent());

        WorkEntry workEntry = findWorkEntryById(id);
        workEntry.canModify();

        workEntryMapper.updateEntityFromRequest(request, workEntry);
        WorkEntry updatedEntry = workEntryRepository.save(workEntry);

        log.info("Successfully updated work entry with id: {}", id);
        return workEntryMapper.toResponse(updatedEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkEntryResponse getWorkEntryById(Long id) {
        log.debug("Fetching wor entry with id: {}", id);

        WorkEntry workEntry = findWorkEntryById(id);
        return workEntryMapper.toResponse(workEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkEntrySummary> getAllWorkEntries(Pageable pageable) {
        log.debug("Fetching all work entries with pagination: {}", pageable);
        validatePaginationParams(pageable.getPageNumber(), pageable.getPageSize());

        Page<WorkEntry> workEntries = workEntryRepository.findAll(pageable);
        return workEntries.map(workEntryMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkEntrySummary> getWorkEntriesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("Fetching work entries between {} and {}", startDate, endDate);
        validatePaginationParams(pageable.getPageNumber(), pageable.getPageSize());
        if(startDate.isAfter(endDate)) {
            throw new BusinessValidationException("Start date cannot be after end date");
        }

        Page<WorkEntry> workEntries = workEntryRepository.findByWorkDateBetween(startDate, endDate, pageable);
        return workEntries.map(workEntryMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkEntrySummary> getWorkEntriesByStatus(WorkEntryStatus workEntryStatus, Pageable pageable) {
        log.debug("Fetching work entries with status: {}", workEntryStatus);

        Page<WorkEntry> workEntries = workEntryRepository.findByWorkEntryStatus(workEntryStatus, pageable);
        return workEntries.map(workEntryMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkEntrySummary> getWorkEntriesByDate(LocalDate workDate) {
        log.debug("Fetching work entries for date: {}", workDate);

        List<WorkEntry> workEntries = workEntryRepository.findByWorkDate(workDate);
        return workEntries.stream()
                .map(workEntryMapper::toSummary)
                .collect(Collectors.toList());
    }

    @Override
    public WorkEntryResponse submitWorkEntry(Long id) {
        log.debug("Submitting work entry for id: {}", id);

        WorkEntry workEntry = findWorkEntryById(id);

        workEntry.submit();

        WorkEntry updatedEntry = workEntryRepository.save(workEntry);

        log.info("Successfully submitted work entry with id: {}", id);
        return workEntryMapper.toResponse(updatedEntry);
    }

    @Override
    public WorkEntryResponse lockWorkEntry(Long id) {
        log.debug("Locking work entry with id: {}", id);

        WorkEntry workEntry = findWorkEntryById(id);

        workEntry.lock();
        WorkEntry updatedEntry = workEntryRepository.save(workEntry);
        log.info("Successfully locked work entry with id: {}", id);
        return workEntryMapper.toResponse(updatedEntry);
    }

    @Override
    public void deleteWorkEntry(Long id) {
        log.debug("Deleting work entry with id: {}", id);

        WorkEntry workEntry = findWorkEntryById(id);

        workEntry.canModify();

        workEntryRepository.delete(workEntry);
        log.info("Successfully deleted work entry with id: {}", id);
    }

    @Override
    public Double calculateTotalHours(LocalDate statDate, LocalDate endDate) {
        log.debug("Calculating total hours");

        if(statDate.isAfter(endDate)) {
            throw new BusinessValidationException("Start date cannot be after end date");
        }

        Double totalHours = workEntryRepository.sumHoursByDateRange(statDate, endDate);
        return totalHours != null ? totalHours : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canModifyWorkEntry(Long id) {
        WorkEntry workEntry = findWorkEntryById(id);
        return workEntry.canModify();
    }

    // Private helper methods
    private WorkEntry findWorkEntryById(Long id) {
        requireNonNull(id, "Work entry id must not be null");
        return workEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "WorkEntry", "id", id
                ));
    }

    private void validateWorkEntryRequest(LocalDate workDate, Double hoursSpent) {
        validateWorkDate(workDate);
        validateHoursSpent(hoursSpent);
    }
}
