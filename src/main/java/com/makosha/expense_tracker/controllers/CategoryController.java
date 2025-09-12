package com.makosha.expense_tracker.controllers;

import com.makosha.expense_tracker.domain.dto.CategoryDto;
import com.makosha.expense_tracker.domain.entities.Category;
import com.makosha.expense_tracker.mappers.CategoryMapper;
import com.makosha.expense_tracker.services.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

    CategoryService categoryService;
    CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public List<CategoryDto> listAllCategories() {
        return categoryService.listAllCategories()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryDto categoryDto) {
        Category CreatedCategory = categoryService.createCategory(
                categoryMapper.toEntity(categoryDto)
        );
        return categoryMapper.toDto(CreatedCategory);
    }

    @GetMapping(path = "/{category_id}")
    public Optional<CategoryDto> getCategory(@PathVariable("category_id") UUID categoryId) {
        return categoryService.getCategory(categoryId)
                .map(categoryMapper::toDto);
    }

    @PutMapping(path = "/{category_id}")
    public CategoryDto updateCategory(
            @PathVariable("category_id") UUID categoryId,
            @RequestBody CategoryDto categoryDto) {
        Category updatedCategory = categoryService.updateCategory(
                categoryId,
                categoryMapper.toEntity(categoryDto)
        );

        return categoryMapper.toDto(updatedCategory);
    }

    @DeleteMapping(path = "/{category_id}")
    public void deleteCategory(
            @PathVariable("category_id") UUID categoryId) {
        categoryService.deleteCategory(categoryId);
    }

}
