package com.workledger.core.timesheet.dto;

import com.workledger.core.timesheet.domain.ProgramType;
import com.workledger.core.timesheet.domain.WorkEntryStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CreateWorkEntryRequest(
        @NotNull(message = "Work date is required")
        LocalDate workDate,

        @NotNull(message = "Program type required")
        ProgramType programType,

        @NotBlank(message = "Program reference is required")
        String programReference,

        String ticketId,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Hours spent is required")
        @Positive(message = "Hours spent must be positive")
        @DecimalMax(value = "9.0", message = "Hours spent cannot exceed 9 hours")
        Double hoursSpent,

        WorkEntryStatus workEntryStatus
) {}
