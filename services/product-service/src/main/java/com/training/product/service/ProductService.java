package com.training.product.service;

import com.training.product.dto.ProductRequest;
import com.training.product.dto.ProductResponse;
import com.training.product.exception.ResourceNotFoundException;
import com.training.product.model.Product;
import com.training.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProductService {

    private static final String DEFAULT_IMAGE_URL = "https://placehold.co/600x400?text=Product";

    private final ProductRepository productRepository;
    private final RestClient restClient;

    public ProductService(
            ProductRepository productRepository,
            @Value("${services.category.url}") String categoryServiceUrl
    ) {
        this.productRepository = productRepository;
        this.restClient = RestClient.builder()
                .baseUrl(categoryServiceUrl)
                .build();
    }

    public List<ProductResponse> getProducts(String categoryId, String search) {
        boolean hasCategory = categoryId != null && !categoryId.isBlank();
        boolean hasSearch = search != null && !search.isBlank();

        List<Product> products;
        if (hasCategory && hasSearch) {
            products = productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId.trim(), search.trim());
        } else if (hasCategory) {
            products = productRepository.findByCategoryId(categoryId.trim());
        } else if (hasSearch) {
            products = productRepository.findByNameContainingIgnoreCase(search.trim());
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return mapToResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        String categoryId = request.categoryId().trim();
        validateCategoryExists(categoryId);

        Product product = new Product(
                request.name().trim(),
                request.description() != null ? request.description().trim() : null,
                resolveImage(request.image()),
                request.price(),
                categoryId
        );

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        String categoryId = request.categoryId().trim();
        validateCategoryExists(categoryId);

        product.setName(request.name().trim());
        product.setDescription(request.description() != null ? request.description().trim() : null);
        product.setImage(resolveImage(request.image()));
        product.setPrice(request.price());
        product.setCategoryId(categoryId);

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private void validateCategoryExists(String categoryId) {
        try {
            restClient.get()
                    .uri("/api/categories/{id}", categoryId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private String resolveImage(String image) {
        if (image == null || image.isBlank()) {
            return DEFAULT_IMAGE_URL;
        }
        return image.trim();
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                String.valueOf(product.getId()),
                product.getName(),
                product.getDescription(),
                product.getImage(),
                product.getPrice(),
                product.getCategoryId()
        );
    }
}