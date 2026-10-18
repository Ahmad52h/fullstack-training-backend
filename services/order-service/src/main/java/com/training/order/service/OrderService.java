package com.training.order.service;

import com.training.order.dto.OrderItemRequest;
import com.training.order.dto.OrderItemResponse;
import com.training.order.dto.OrderRequest;
import com.training.order.dto.OrderResponse;
import com.training.order.exception.ResourceNotFoundException;
import com.training.order.model.Order;
import com.training.order.model.OrderItem;
import com.training.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestClient restClient;

    public OrderService(
            OrderRepository orderRepository,
            @Value("${services.product.url}") String productServiceUrl
    ) {
        this.orderRepository = orderRepository;
        this.restClient = RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order("CREATED", BigDecimal.ZERO, LocalDateTime.now());
        BigDecimal calculatedTotalPrice = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.items()) {
            Map<String, Object> productDetails = fetchProductDetails(itemReq.productId());

            String productName = (String) productDetails.get("name");
            BigDecimal unitPrice = new BigDecimal(productDetails.get("price").toString());
            int quantity = itemReq.quantity();

            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            calculatedTotalPrice = calculatedTotalPrice.add(itemTotal);

            OrderItem orderItem = new OrderItem(
                    itemReq.productId(),
                    productName,
                    quantity,
                    unitPrice
            );
            order.addItem(orderItem);
        }

        order.setTotalPrice(calculatedTotalPrice);
        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    private Map<String, Object> fetchProductDetails(String productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(Map.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        String.valueOf(item.getId()),
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()
                ))
                .toList();

        return new OrderResponse(
                String.valueOf(order.getId()),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}