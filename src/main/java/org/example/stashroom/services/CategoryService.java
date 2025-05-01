package org.example.stashroom.services;
import org.example.stashroom.dto.CategoryCreateDTO;
import org.example.stashroom.dto.CategoryDTO;
import org.example.stashroom.entities.Category;
import org.example.stashroom.mappers.CategoryMapper;
import org.example.stashroom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CategoryMapper categoryMapper;

    public List<CategoryDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @Transactional
    public CategoryDTO create(CategoryCreateDTO dto) {
        Category category = categoryMapper.fromCreateDto(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryCreateDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(dto.name());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
