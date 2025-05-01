package org.example.stashroom.mappers;
import org.example.stashroom.dto.CategoryCreateDTO;
import org.example.stashroom.dto.CategoryDTO;
import org.example.stashroom.entities.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);
    Category fromCreateDto(CategoryCreateDTO dto);
}
