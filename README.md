# Full-Stack Ordering System - Backend Microservices

Java 21 & Spring Boot 3.4.3 microservices architecture for managing categories, products, and customer orders.

## Architecture & Ports
- API Gateway (8080): Single entry point routing /api/categories/**, /api/products/**, and /api/orders/**
- Category Service (8081): Category CRUD & PostgreSQL (category_db)
- Product Service (8082): Product CRUD, search, category filter & PostgreSQL (product_db)
- Order Service (8083): Order creation, server-side total calculation & PostgreSQL (order_db)

## OpenAPI / Swagger UI
- Category Service: http://localhost:8081/swagger-ui/index.html
- Product Service: http://localhost:8082/swagger-ui/index.html
- Order Service: http://localhost:8083/swagger-ui/index.html

## Running with Docker Compose
docker compose up --build
