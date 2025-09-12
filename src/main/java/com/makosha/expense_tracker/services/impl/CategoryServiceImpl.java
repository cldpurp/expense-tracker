package com.makosha.expense_tracker.services.impl;

import com.makosha.expense_tracker.domain.entities.Category;
import com.makosha.expense_tracker.domain.entities.Expense;
import com.makosha.expense_tracker.repositories.CategoryRepository;
import com.makosha.expense_tracker.services.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> listAllCategories() {
        //return categoryRepository.findAll();                                      //то, что было
        List<Category> categories = categoryRepository.findAll();

        double totalExpenses = categories.stream()
                .mapToDouble(cat -> Optional.ofNullable(cat.getExpenses())
                        .orElse(List.of())
                        .stream()
                        .mapToDouble(Expense::getAmount)
                        .sum())
                .sum();

        categories.forEach(cat -> {
            double categorySum = Optional.ofNullable(cat.getExpenses())
                    .orElse(List.of())
                    .stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();
            double rawPercentage = totalExpenses == 0 ? 0 : (categorySum / totalExpenses) * 100;
            cat.setPercentage(Math.round(rawPercentage * 10) / 10.0);
        });


        return categories;
    }

    @Override
    public Category createCategory(Category category) {
        if(null != category.getId()) {
            throw new IllegalArgumentException("Category already has an ID!");
        }
        if(null == category.getName() || category.getName().isBlank()) {
            throw new IllegalArgumentException("Category name must be present!");
        }

        return categoryRepository.save(new Category(
                null,
                category.getName(),
                0.0,
                null
        ));

    }

    @Override
    public Optional<Category> getCategory(UUID id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    @Override
    public Category updateCategory(UUID categoryId, Category category) {
        if (null == category.getId()) {
            throw new IllegalArgumentException("Category ID must be present!");
        }

        if(!Objects.equals(category.getId(), categoryId)) {
            throw new IllegalArgumentException("It's not permitted to change a category ID!");
        }

        Category existingCategory = categoryRepository.findById(categoryId).orElseThrow(() ->
                new IllegalArgumentException("Category not found!"));

        existingCategory.setName(category.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }


}
