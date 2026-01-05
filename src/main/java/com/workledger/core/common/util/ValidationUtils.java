package com.workledger.core.common.util;

import com.workledger.core.common.exception.BusinessValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 * Provides reusable validation for business logic.
 */
public class ValidationUtils {
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Common regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern TICKET_ID_PATTERN = Pattern.compile(
            "^[A-Z]+-\\d+$"
    );

    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9]+$"
    );

    public static void requireNonEmpty(String value, String fieldName) {
        if(value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    public static void requireNonNull(Object value, Object fieldName) {
        if(value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public static void requireNonEmpty(Collection<?> collection, String fieldName) {
        if(collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }

    public static void validateNotFutureDate(LocalDate date, String fieldName) {
        requireNonNull(date, fieldName);

        if(date.isAfter(LocalDate.now())) {
            throw new BusinessValidationException(fieldName + " cannot be in the future");
        }
    }

    public static void validateNotPastDate(LocalDate date, String fieldName) {
        requireNonNull(date, fieldName);

        if(date.isBefore(LocalDate.now())) {
            throw new BusinessValidationException(fieldName + " cannot be in the past");
        }
    }

    public static void validateRange(Number value, String fieldName, Number min, Number max) {
        requireNonNull(value, fieldName);

        double doubleValue = value.doubleValue();
        double minValue = min.doubleValue();
        double maxValue = max.doubleValue();

        if(doubleValue < minValue || doubleValue > maxValue) {
            throw new BusinessValidationException(
                    String.format("%s must be between %s and %s (current: %s)",
                            fieldName, min, max, value)
            );
        }
    }

    public static void validateHoursSpent(Double hours) {
        requireNonNull(hours, "Hours spent");
        validateRange(hours, "Hours spent", 0.0, 24.0);

        if(hours <= 0) {
            throw new BusinessValidationException("Hours spent must be greater than 0");
        }
    }

    public static void validateWorkDate(LocalDate workDate) {
        requireNonNull(workDate, "Work Date");
        validateNotFutureDate(workDate, "Work Date");

        LocalDate cutoffDate = LocalDate.now().minusDays(60);
        if(workDate.isBefore(cutoffDate)) {
            throw new BusinessValidationException("Work date cannot be older than 60 days");
        }
    }

    public static void validateEmail(String email) {
        requireNonNull(email, "Email");

        if(!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessValidationException("Invalid email address");
        }
    }

    public static void validateTicketId(String ticketId) {
        if(ticketId == null || ticketId.trim().isEmpty()) {
            return;
        }

        if(!TICKET_ID_PATTERN.matcher(ticketId).matches()) {
            throw new BusinessValidationException(
                    "Invalid ticket ID format. Expected format: PREFIX_NUMBER (e.g., PROJ-123)"
            );
        }
    }

    public static void validateMaxLength(String value, String fieldName, int maxLength) {
        if(value != null && value.length() > maxLength) {
            throw new BusinessValidationException(
                    String.format("%s must not exceed %d characters (current: &d)", fieldName, maxLength, value.length())
            );
        }
    }

    public static void validatePaginationParams(int page, int size) {
        if(page < 0) {
            throw new BusinessValidationException("Page number cannot be negative");
        }
        if(size <= 0) {
            throw new BusinessValidationException("Page size must be positive");
        }
        if(size > 100) {
            throw new BusinessValidationException("Page size cannot exceed 100");
        }
    }

    public static void validateDescription(String description) {
        if(description != null) {
            validateMaxLength(description, "Description", 2000);
            if(description.trim().isEmpty()) {
                throw new BusinessValidationException("Description cannot be empty");
            }
        }
    }

    /**
     * Validates that at least on field in update request is non-null
     */
    public static void requireAtLeastOneNonNull(String message, Object... values) {
        for(Object value : values) {
            if(value != null){
                return;
            }
        }
        throw new BusinessValidationException(message);
    }

}
