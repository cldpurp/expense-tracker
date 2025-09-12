package com.makosha.expense_tracker.controllers;

import com.makosha.expense_tracker.domain.dto.ExpenseDto;
import com.makosha.expense_tracker.domain.entities.Expense;
import com.makosha.expense_tracker.mappers.ExpenseMapper;
import com.makosha.expense_tracker.services.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class ExpenseController {

    ExpenseService expenseService;
    ExpenseMapper expenseMapper;

    public ExpenseController(ExpenseService expenseService, ExpenseMapper expenseMapper) {
        this.expenseService = expenseService;
        this.expenseMapper = expenseMapper;
    }

    @GetMapping(path = "/categories/{category_id}/expenses")
    public Page<ExpenseDto> getExpenses(
            @PathVariable("category_id") UUID categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return expenseService.listExpensesByCategory(categoryId, page, size)
                .map(expenseMapper::toDto);
    }


    @PostMapping(path = "/categories/{category_id}/expenses")
    public ExpenseDto createExpense(
            @PathVariable("category_id") UUID categoryId,
            @RequestBody ExpenseDto expenseDto) {
        Expense createdExpense = expenseService.createExpense(
                categoryId,
                expenseMapper.toEntity(expenseDto)
        );
        return expenseMapper.toDto(createdExpense);
    }

    @PutMapping(path = "/categories/{category_id}/expenses/{expense_id}")
    public ExpenseDto updateExpense(
            @PathVariable("category_id") UUID categoryId,
            @PathVariable("expense_id") UUID expenseId,
            @RequestBody ExpenseDto expenseDto) {
        Expense updatedExpense = expenseService.updateExpense(categoryId, expenseId, expenseMapper.toEntity(expenseDto));
        return expenseMapper.toDto(updatedExpense);
    }

    @DeleteMapping(path = "/categories/{category_id}/expenses/{expense_id}")
    public void deleteExpense(
            @PathVariable("category_id") UUID categoryId,
            @PathVariable("expense_id") UUID expenseId
    ) {
        expenseService.deleteExpense(categoryId, expenseId);
    }

    @GetMapping("/expenses/between")
    public List<ExpenseDto> getExpensesBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return expenseService.getExpensesBetweenDates(startDate, endDate)
                .stream()
                .map(expenseMapper::toDto)
                .toList();
    }

    @GetMapping("/expenses/between/total")
    public Double getTotalAmountBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return expenseService.getTotalAmountBetweenDates(startDate, endDate);
    }
}
