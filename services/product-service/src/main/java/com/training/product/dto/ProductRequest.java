package com.training.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 100, message = "Product name must not exceed 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String image,

        @NotNull(message = "Product price is required")
        @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
        BigDecimal price,

        @NotBlank(message = "Category ID is required")
        String categoryId
) {
}