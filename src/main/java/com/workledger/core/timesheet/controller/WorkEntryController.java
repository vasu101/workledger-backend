package com.workledger.core.timesheet.controller;


import com.workledger.core.common.dto.ApiResponse;
import com.workledger.core.common.dto.PageResponse;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import com.workledger.core.timesheet.dto.CreateWorkEntryRequest;
import com.workledger.core.timesheet.dto.UpdateWorkEntryRequest;
import com.workledger.core.timesheet.dto.WorkEntryResponse;
import com.workledger.core.timesheet.dto.WorkEntrySummary;
import com.workledger.core.timesheet.service.WorkEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/work-entries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Work Entries", description = "Work entry management APIs")
public class WorkEntryController {

    private final WorkEntryService workEntryService;

    @PostMapping
    @Operation(summary = "Create a new work entity", description = "Creates a new work entry with the provided details")
    public ResponseEntity<ApiResponse<WorkEntryResponse>> createWorkEntry(@Valid @RequestBody CreateWorkEntryRequest request) {
        log.info("Creating new work entry for date: {}", request.workDate());
        WorkEntryResponse response = workEntryService.createWorkEntry(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Work entry created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a work entry", description = "Updates an existing work entry")
    public ResponseEntity<ApiResponse<WorkEntryResponse>> updateWorkEntry(@PathVariable Long id, @Valid @RequestBody UpdateWorkEntryRequest request) {
        log.info("Updating work entry with id: {}", id);
        WorkEntryResponse response = workEntryService.updateWorkEntry(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Work entry updated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all work entries", description = "Retrieves all work entries with pagination")
    public ResponseEntity<ApiResponse<PageResponse<WorkEntrySummary>>> getAllWorkEntries(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "workDate") String sortBy,
            @Parameter(description = "Sort direction (ASC/DESC)")
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        log.info("Fetching all work entries - page: {}, size: {}, sort: {} {}", page, size, sortBy, direction);

        Page<WorkEntrySummary> workEntriesPage = workEntryService.getAllWorkEntries(pageable);
        PageResponse<WorkEntrySummary> pageResponse = PageResponse.from(workEntriesPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse,"Response with pagination"));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get work entries by date range", description = "Retrieves work entries within a specified date range")
    public ResponseEntity<ApiResponse<PageResponse<WorkEntrySummary>>> getWorkEntriesByDateRange(
            @Parameter(description = "Start date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "workDate"));

        log.info("Fetching work entries for date range: {} to {}", startDate, endDate);

        Page<WorkEntrySummary> workEntriesPage = workEntryService.getWorkEntriesByDateRange(startDate, endDate, pageable);
        PageResponse<WorkEntrySummary> pageResponse = PageResponse.from(workEntriesPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse,"Response with pagination"));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get work entries by specified date", description = "Retrieves all work entries for a specific date")
    public ResponseEntity<ApiResponse<List<WorkEntrySummary>>> getWorkEntriesByDate(
            @Parameter(description = "Work date (yyyy-MM-dd)")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Fetching work entries for date: {}", date);
        List<WorkEntrySummary> workEntries = workEntryService.getWorkEntriesByDate(date);

        return ResponseEntity.ok(ApiResponse.success(workEntries,"Response in a list"));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get work entries by status", description = "Retrieves work entries filtered by status")
    public ResponseEntity<ApiResponse<PageResponse<WorkEntrySummary>>> getWorkEntriesByStatus(
            @Parameter(description = "Work entry status")
            @PathVariable WorkEntryStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
            ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"workDate"));

        log.info("Fetching work entries with status: {}", status);

        Page<WorkEntrySummary> workEntriesPage = workEntryService.getWorkEntriesByStatus(status, pageable);
        PageResponse<WorkEntrySummary> pageResponse = PageResponse.from(workEntriesPage);

        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Response with pagination"));
    }

    @PatchMapping("/{id}/submit")
    @Operation(summary = "Submit work entry", description = "Submits a draft work entry for approval")
    public ResponseEntity<ApiResponse<WorkEntryResponse>> submitWorkEntry(@PathVariable Long id) {
        log.info("Submitting work entry with id: {}", id);
        WorkEntryResponse response = workEntryService.submitWorkEntry(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Work entry submit successfully"));
    }

    @PatchMapping("/{id}/lock")
    @Operation(summary = "Lock work entry", description = "Locks a submitted work entry")
    public ResponseEntity<ApiResponse<WorkEntryResponse>> lockWorkEntry(@PathVariable Long id) {
        log.info("Locking work entry with id: {}", id);
        WorkEntryResponse response = workEntryService.lockWorkEntry(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Work entry locked successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete work entry", description = "Deletes a work entry")
    public ResponseEntity<ApiResponse<Void>> deleteWorkEntry(@PathVariable Long id) {
        log.info("Deleting work entry with id: {}", id);
        workEntryService.deleteWorkEntry(id);
        return ResponseEntity.ok(ApiResponse.noContent("Work entry deleted successfully"));
    }

    @GetMapping("/hours/total")
    @Operation(summary = "Calculates total hours", description = "Calculate total hours spent within a date range")
    public ResponseEntity<ApiResponse<Double>> calculateTotalHours(
            @Parameter(description = "Start date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("Calculating total hours for date range: {} to {}", startDate, endDate);
        Double totalHours = workEntryService.calculateTotalHours(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(totalHours, String.format("Total hours: %.2f", totalHours)));
    }
}
