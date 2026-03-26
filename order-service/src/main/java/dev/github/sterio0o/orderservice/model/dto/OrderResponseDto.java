package dev.github.sterio0o.orderservice.model.dto;

import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponseDto(
        String customerEmail,
        String product,
        Integer quantity,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponseDto fromEntity(Order order) {
        return new OrderResponseDto(
                order.getCustomerEmail(),
                order.getProduct(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
