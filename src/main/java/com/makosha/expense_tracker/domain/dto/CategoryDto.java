package com.makosha.expense_tracker.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

public record CategoryDto(
        UUID id,
        String name,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Double percentage,
        Double TotalAmount,
        List<ExpenseDto> expenses
) {}
