package com.makosha.expense_tracker.services;

import com.makosha.expense_tracker.domain.entities.Expense;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
    Page<Expense> listExpensesByCategory(UUID categoryId, int page, int size);
    Expense createExpense(UUID categoryId, Expense expense);
    Expense updateExpense(UUID categoryId, UUID expenseId, Expense expense);
    void deleteExpense(UUID categoryId, UUID expenseId);
    List<Expense> getExpensesBetweenDates(LocalDate startDate, LocalDate endDate);
    Double getTotalAmountBetweenDates(LocalDate startDate, LocalDate endDate);
}
