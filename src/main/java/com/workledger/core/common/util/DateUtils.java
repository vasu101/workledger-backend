package com.workledger.core.common.util;

import org.springframework.cglib.core.Local;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * Utility class for dte and time operations.
 * Provides common date calculations and formatting for the WorkLedger application.
 */
public class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter DISPLAY_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    public static final DateTimeFormatter COMPACT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDate today() {
        return LocalDate.now();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static boolean isPast(LocalDate date) {
        return date.isBefore(today());
    }

    public static boolean isFuture(LocalDate date) {
        return date.isAfter(today());
    }

    public static boolean isWithinRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public static LocalDate getStartOfWeek() {
        return today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public static LocalDate getEndOfWeek() {
        return today().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
    }

    public static LocalDate getStartOfMonth() {
        return today().with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDate getEndOfMonth() {
        return today().with(TemporalAdjusters.lastDayOfMonth());
    }

    public static LocalDate getStartOfYear() {
        return today().with(TemporalAdjusters.firstDayOfYear());
    }

    public static LocalDate getEndOfYear() {
        return today().with(TemporalAdjusters.lastDayOfYear());
    }

    public static int getQuarter(LocalDate date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }
}
