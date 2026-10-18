package com.training.product.repository;

import com.training.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByCategoryIdAndNameContainingIgnoreCase(String categoryId, String name);
}