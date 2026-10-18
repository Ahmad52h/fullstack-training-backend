package com.training.product.config;

import com.training.product.model.Product;
import com.training.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ProductDataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final RestClient restClient;

    public ProductDataSeeder(
            ProductRepository productRepository,
            @Value("${services.category.url}") String categoryServiceUrl
    ) {
        this.productRepository = productRepository;
        this.restClient = RestClient.builder()
                .baseUrl(categoryServiceUrl)
                .build();
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }

        try {
            List<Map<String, String>> categories = restClient.get()
                    .uri("/api/categories")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (categories == null || categories.isEmpty()) {
                return;
            }

            String firstCategoryId = categories.get(0).get("id");
            String secondCategoryId = categories.size() > 1 ? categories.get(1).get("id") : firstCategoryId;
            String thirdCategoryId = categories.size() > 2 ? categories.get(2).get("id") : firstCategoryId;
            String fourthCategoryId = categories.size() > 3 ? categories.get(3).get("id") : firstCategoryId;

            List<Product> defaultProducts = List.of(
                    new Product(
                            "Pancakes",
                            "Fluffy pancakes with maple syrup and butter",
                            "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=600",
                            new BigDecimal("5.00"),
                            firstCategoryId
                    ),
                    new Product(
                            "Omelette",
                            "Three-egg omelette with cheese and herbs",
                            "https://images.unsplash.com/photo-1510693206972-df098062cb71?w=600",
                            new BigDecimal("6.50"),
                            firstCategoryId
                    ),
                    new Product(
                            "Classic Beef Burger",
                            "Juicy grilled beef patty with cheddar and lettuce",
                            "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600",
                            new BigDecimal("9.99"),
                            secondCategoryId
                    ),
                    new Product(
                            "Margherita Pizza",
                            "Wood-fired pizza with fresh mozzarella and basil",
                            "https://images.unsplash.com/photo-1604382355076-af4b0eb60143?w=600",
                            new BigDecimal("12.50"),
                            thirdCategoryId
                    ),
                    new Product(
                            "Iced Caramel Latte",
                            "Freshly brewed espresso with cold milk and caramel",
                            "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600",
                            new BigDecimal("4.25"),
                            fourthCategoryId
                    )
            );

            productRepository.saveAll(defaultProducts);
        } catch (Exception ex) {
            System.out.println("Skipping product seeding: Category service not reachable yet.");
        }
    }
}