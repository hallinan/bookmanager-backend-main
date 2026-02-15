package com.acme.bookmanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidationUtil {
    private static final int DEFAULT_PAGE_SIZE = 10;

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void validatePositiveId(long id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    public static int validatePage(Integer page) {
        return page != null && page >= 0 ? page : 0;
    }

    public static int validateSize(Integer size) {
        return size != null && size > 0 ? size : DEFAULT_PAGE_SIZE;
    }

    public static LocalDate parseDate(String date, String fieldName) {
        if (date == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format for " + fieldName + ". Use YYYY-MM-DD");
        }
    }

    public static void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end dates cannot be null");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
