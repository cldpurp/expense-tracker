package com.makosha.expense_tracker.mappers.impl;

import com.makosha.expense_tracker.domain.dto.CategoryDto;
import com.makosha.expense_tracker.domain.entities.Category;
import com.makosha.expense_tracker.domain.entities.Expense;
import com.makosha.expense_tracker.mappers.CategoryMapper;
import com.makosha.expense_tracker.mappers.ExpenseMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryMapperImpl implements CategoryMapper {

    private final ExpenseMapper expenseMapper;

    public CategoryMapperImpl(ExpenseMapper expenseMapper) {
        this.expenseMapper = expenseMapper;
    }

    @Override
    public CategoryDto toDto(Category category) {
        if (category == null) return null;

        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getPercentage(),
                category.getExpenses() == null ? 0.0 :
                        category.getExpenses().stream()
                                .mapToDouble(Expense::getAmount)
                                .sum(),
                Optional.ofNullable(category.getExpenses())
                        .map(expenses -> expenses.stream()
                                .map(expenseMapper::toDto)
                                .toList()
                        ).orElse(null)
        );
    }

    @Override
    public Category toEntity(CategoryDto dto) {
        if (dto == null) return null;

        return new Category(
                dto.id(),
                dto.name(),
                dto.percentage(),
                Optional.ofNullable(dto.expenses())
                        .map(expenses -> expenses.stream()
                                .map(expenseMapper::toEntity)
                                .toList()
                        ).orElse(null)
        );
    }

}