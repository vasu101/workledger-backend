package com.workledger.core.timesheet.dto;

import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntryStatus;

import java.time.LocalDate;

public record WorkEntrySummary(
        Long id,
        LocalDate workDate,
        ProgramType programType,
        String programReference,
        Double hoursSpent,
        WorkEntryStatus workEntryStatus
) {}
