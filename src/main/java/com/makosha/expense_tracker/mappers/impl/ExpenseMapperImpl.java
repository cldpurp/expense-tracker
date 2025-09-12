package com.makosha.expense_tracker.mappers.impl;

import com.makosha.expense_tracker.domain.dto.ExpenseDto;
import com.makosha.expense_tracker.domain.entities.Expense;
import com.makosha.expense_tracker.mappers.ExpenseMapper;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public ExpenseDto toDto(Expense expense) {
        return new ExpenseDto(
                expense.getId(),
                expense.getAmount(),
                expense.getDate(),
                expense.getDescription()
        );
    }

    @Override
    public Expense toEntity(ExpenseDto dto) {
        return new Expense(
                dto.id(),
                null,
                dto.amount(),
                dto.date(),
                dto.description()
        );
    }
}
