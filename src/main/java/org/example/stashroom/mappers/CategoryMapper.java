package org.example.stashroom.mappers;
import org.example.stashroom.dto.CategoryCreateDTO;
import org.example.stashroom.dto.CategoryDTO;
import org.example.stashroom.entities.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "categoryId", source = "id")
    CategoryDTO toDto(Category category);
    default Category map(Long categoryId) {
        if (categoryId == null) {return null;}
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
    @Mapping(target = "id", ignore = true)
    Category fromCreateDto(CategoryCreateDTO dto);
}
