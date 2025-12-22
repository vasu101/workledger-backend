package com.workledger.core.timesheet.service;

import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.UpdateWorkEntryRequest;
import com.workledger.core.timesheet.dto.WorkEntryResponse;
import com.workledger.core.timesheet.dto.WorkEntrySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;


/**
 * Service interface for managing work entries in the timesheet system.
 * Provides operations for CRUD, filtering, and status management of work entries.
 */
public interface WorkEntryService {

    WorkEntryResponse createWorkEntry(CreateWorkEntryRequest request);
    WorkEntryResponse updateWorkEntry(Long id, UpdateWorkEntryRequest request);
    WorkEntryResponse getWorkEntryById(Long id);
    Page<WorkEntrySummary> getAllWorkEntries(Pageable pageable);
    Page<WorkEntrySummary> getWorkEntriesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    Page<WorkEntrySummary> getWorkEntriesByStatus(WorkEntryStatus workEntryStatus, Pageable pageable);
    List<WorkEntrySummary> getWorkEntriesByDate(LocalDate workDate);
    WorkEntryResponse submitWorkEntry(Long id);
    WorkEntryResponse lockWorkEntry(Long id);
    void deleteWorkEntry(Long id);
    Double calculateTotalHours(LocalDate statDate, LocalDate endDate);
    boolean canModifyWorkEntry(Long id);
}
