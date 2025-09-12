package com.makosha.expense_tracker.domain.dto;

public record ErrorResponse(
        int status,
        String message,
        String details
) {
}
