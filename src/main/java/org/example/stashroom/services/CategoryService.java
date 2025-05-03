package org.example.stashroom.services;
import lombok.extern.slf4j.Slf4j;
import org.example.stashroom.dto.CategoryCreateDTO;
import org.example.stashroom.dto.CategoryDTO;
import org.example.stashroom.entities.Category;
import org.example.stashroom.exceptions.NotFoundException;
import org.example.stashroom.mappers.CategoryMapper;
import org.example.stashroom.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    public List<CategoryDTO> findAll() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO findById(Long id) {
        log.debug("Fetching category by id: {}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", id);
                    return new NotFoundException("Category not found");
                });
    }

    @Transactional
    public CategoryDTO create(CategoryCreateDTO dto) {
        log.info("Creating new category: {}", dto.name());
        Category category = categoryMapper.fromCreateDto(dto);
        Category saved = categoryRepository.save(category);
        log.info("Category created with id: {}", saved.getId());
        return categoryMapper.toDto(saved);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryCreateDTO dto) {
        log.info("Updating category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category not found for update: {}", id);
                    return new NotFoundException("Category not found");
                });
        category.setName(dto.name());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting category with id: {}", id);
        categoryRepository.deleteById(id);
        log.debug("Category deleted: {}", id);
    }
}
