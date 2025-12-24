package com.workledger.core.timesheet.service.impl;

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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkEntryServiceImpl implements WorkEntryService {

    private final WorkEntryRepository workEntryRepository;
    private final WorkEntryMapper workEntryMapper;

    @Override
    public WorkEntryResponse createWorkEntry(CreateWorkEntryRequest request) {
        return null;
    }

    @Override
    public WorkEntryResponse updateWorkEntry(Long id, UpdateWorkEntryRequest request) {
        return null;
    }

    @Override
    public WorkEntryResponse getWorkEntryById(Long id) {
        return null;
    }

    @Override
    public Page<WorkEntrySummary> getAllWorkEntries(Pageable pageable) {
        return null;
    }

    @Override
    public Page<WorkEntrySummary> getWorkEntriesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return null;
    }

    @Override
    public Page<WorkEntrySummary> getWorkEntriesByStatus(WorkEntryStatus workEntryStatus, Pageable pageable) {
        return null;
    }

    @Override
    public List<WorkEntrySummary> getWorkEntriesByDate(LocalDate workDate) {
        return List.of();
    }

    @Override
    public WorkEntryResponse submitWorkEntry(Long id) {
        return null;
    }

    @Override
    public WorkEntryResponse lockWorkEntry(Long id) {
        return null;
    }

    @Override
    public void deleteWorkEntry(Long id) {

    }

    @Override
    public Double calculateTotalHours(LocalDate statDate, LocalDate endDate) {
        return 0.0;
    }

    @Override
    public boolean canModifyWorkEntry(Long id) {
        return false;
    }
}
