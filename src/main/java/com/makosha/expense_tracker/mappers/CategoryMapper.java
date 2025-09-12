package com.makosha.expense_tracker.mappers;

import com.makosha.expense_tracker.domain.dto.CategoryDto;
import com.makosha.expense_tracker.domain.entities.Category;

public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto dto);
}
