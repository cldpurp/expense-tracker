package com.makosha.expense_tracker.repositories;

import com.makosha.expense_tracker.domain.entities.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
    Page<Expense> findByCategoryId(UUID categoryId, Pageable pageable);
    Optional<Expense> findByCategoryIdAndId(UUID categoryId, UUID expenseId);
    void deleteByCategoryIdAndId(UUID taskListId, UUID id);
    List<Expense> findByDateBetween(LocalDate dateAfter, LocalDate dateBefore);
}
