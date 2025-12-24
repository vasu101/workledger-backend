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

    /**
     * Creates a new work entry.
     *
     * @param request the create work entry request
     * @return created work entry response
     * @throws IllegalArgumentException if the request contains invalid data
     */
    WorkEntryResponse createWorkEntry(CreateWorkEntryRequest request);

    /**
     * Update existing work entry.
     *
     * @param id the work entry identifier
     * @param request the work entry update request
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     *
     */
    WorkEntryResponse updateWorkEntry(Long id, UpdateWorkEntryRequest request);

    /**
     * Retrieves a work entry by its identifier.
     *
     * @param id the work entry identifier
     * @return the work entry response
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     */
    WorkEntryResponse getWorkEntryById(Long id);

    /**
     * Retrieves all work entries with pagination support.
     *
     * @param pageable pagination information
     * @return paginated work entry summaries
     */
    Page<WorkEntrySummary> getAllWorkEntries(Pageable pageable);

    /**
     * Retrieves work entries filtered by date range.
     *
     * @param startDate (inclusive)
     * @param endDate (inclusive)
     * @param pageable pagination information
     * @return paginated work entry summaries within the date range
     */
    Page<WorkEntrySummary> getWorkEntriesByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    /**
     * Retrieves work entries filtered by status.
     *
     * @param workEntryStatus the work entry status
     * @param pageable pagination information
     * @return paginated work entry summaries with the specified status
     */
    Page<WorkEntrySummary> getWorkEntriesByStatus(WorkEntryStatus workEntryStatus, Pageable pageable);

    /**
     * Retrieves work entries for a specific date.
     *
     * @param workDate the work date
     * @return list of work entry summaries for the specified date
     */
    List<WorkEntrySummary> getWorkEntriesByDate(LocalDate workDate);

    /**
     * Submits a draft work entry for approval.
     *
     * @param id the work entry identifier
     * @return the updated work entry response with SUBMITTED status
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     * @throws IllegalStateException if work entry is ot in DRAFT status
     */
    WorkEntryResponse submitWorkEntry(Long id);

    /**
     * Locks a submitted work entry to prevent further modification.
     *
     * @param id the work entry identifier
     * @return the updated work entry response with LOCKED status
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     * @throws IllegalStateException if work entry is not in SUBMITTED status
     */
    WorkEntryResponse lockWorkEntry(Long id);

    /**
     * Deletes a work entry.
     *
     * @param id the work entry identifier
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     * @throws IllegalStateException if work entry is locked
     */
    void deleteWorkEntry(Long id);

    /**
     * Calculates total hours spent within a date range.
     *
     * @param statDate (inclusive)
     * @param endDate (inclusive)
     * @return total hours spent
     */
    Double calculateTotalHours(LocalDate statDate, LocalDate endDate);

    /**
     * Validates ifa work entry can be modified.
     *
     * @param id the work entry identifier
     * @return true if the work entry can be modified, false otherwise
     * @throws com.workledger.core.common.exception.ResourceNotFoundException if work entry not found
     */
    boolean canModifyWorkEntry(Long id);
}
