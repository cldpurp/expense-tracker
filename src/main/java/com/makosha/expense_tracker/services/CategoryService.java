package com.makosha.expense_tracker.services;

import com.makosha.expense_tracker.domain.entities.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface CategoryService {
    List<Category> listAllCategories();
    Category createCategory(Category category);
    Optional<Category> getCategory(UUID id);
    Category updateCategory(UUID categoryId, Category category);
    void deleteCategory(UUID categoryId);
}
