package com.makosha.expense_tracker.mappers;

import com.makosha.expense_tracker.domain.dto.ExpenseDto;
import com.makosha.expense_tracker.domain.entities.Expense;

public interface ExpenseMapper {
    ExpenseDto toDto(Expense expense);
    Expense toEntity(ExpenseDto dto);
}
