package com.makosha.expense_tracker.domain.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ExpenseDto (
        UUID id,
        Double amount,
        LocalDate date,
        String description
) {}
