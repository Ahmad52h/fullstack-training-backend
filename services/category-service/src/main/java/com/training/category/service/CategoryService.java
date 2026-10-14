package com.training.category.service;

import com.training.category.dto.CategoryRequest;
import com.training.category.dto.CategoryResponse;
import com.training.category.exception.DuplicateResourceException;
import com.training.category.exception.ResourceNotFoundException;
import com.training.category.model.Category;
import com.training.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return mapToResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        String trimmedName = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw new DuplicateResourceException("Category with name '" + trimmedName + "' already exists");
        }
        Category category = new Category(trimmedName);
        Category savedCategory = categoryRepository.save(category);
        return mapToResponse(savedCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        String trimmedName = request.name().trim();
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw new DuplicateResourceException("Category with name '" + trimmedName + "' already exists");
        }
        category.setName(trimmedName);
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private CategoryResponse mapToResponse(Category category) {
        return new CategoryResponse(
                String.valueOf(category.getId()),
                category.getName()
        );
    }
}