package com.training.category.config;

import com.training.category.model.Category;
import com.training.category.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategoryDataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<Category> defaultCategories = List.of(
                    new Category("Breakfast"),
                    new Category("Lunch"),
                    new Category("Dinner"),
                    new Category("Drinks")
            );
            categoryRepository.saveAll(defaultCategories);
        }
    }
}