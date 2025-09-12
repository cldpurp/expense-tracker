package com.makosha.expense_tracker.services.impl;

import com.makosha.expense_tracker.domain.entities.Category;
import com.makosha.expense_tracker.domain.entities.Expense;
import com.makosha.expense_tracker.repositories.CategoryRepository;
import com.makosha.expense_tracker.repositories.ExpenseRepository;
import com.makosha.expense_tracker.services.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository, CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Expense> listExpensesByCategory(UUID categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        return expenseRepository.findByCategoryId(categoryId, pageable);
    }


    @Transactional
    @Override
    public Expense createExpense(UUID categoryId, Expense expense) {
        if(null != expense.getId()) {
            throw new IllegalArgumentException("Expense already has an ID!");
        }
        if(null == expense.getAmount() || expense.getAmount() <= 0) {
            throw new IllegalArgumentException("Expense must have a proper value!");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found!"));

        Expense expenseToSave = new Expense(
                null,
                category,
                expense.getAmount(),
                expense.getDate(),
                expense.getDescription()
        );

        return expenseRepository.save(expenseToSave);

    }

    @Transactional
    @Override
    public Expense updateExpense(UUID categoryId, UUID expenseId, Expense expense) {

        if (expense.getId() == null || !expense.getId().equals(expenseId)) {
            throw new IllegalArgumentException("Expense ID mismatch!");
        }

        Expense exisitingExpense = expenseRepository.findByCategoryIdAndId(categoryId, expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found!"));

        exisitingExpense.setAmount(expense.getAmount());
        exisitingExpense.setDate(expense.getDate());
        exisitingExpense.setDescription(expense.getDescription());

        return expenseRepository.save(exisitingExpense);
    }

    @Transactional
    @Override
    public void deleteExpense(UUID categoryId, UUID expenseId) {
        expenseRepository.deleteByCategoryIdAndId(categoryId, expenseId);
    }

    @Override
    public List<Expense> getExpensesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public Double getTotalAmountBetweenDates(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByDateBetween(startDate, endDate)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }
}
