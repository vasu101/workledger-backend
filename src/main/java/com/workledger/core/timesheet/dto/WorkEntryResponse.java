package com.workledger.core.timesheet.dto;

import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WorkEntryResponse(
        Long id,
        LocalDate workDate,
        ProgramType programType,
        String programReference,
        String ticketId,
        String description,
        Double hoursSpent,
        WorkEntryStatus workEntryStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
