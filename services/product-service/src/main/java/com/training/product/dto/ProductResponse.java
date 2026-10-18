package com.training.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        String image,
        BigDecimal price,
        String categoryId
) {
}