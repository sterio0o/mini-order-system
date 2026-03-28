package dev.github.sterio0o.orderservice.model.dto;

import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        String customerEmail,
        String product,
        Integer quantity,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponseDto fromEntity(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getCustomerEmail(),
                order.getProduct().getProductName(),
                order.getQuantity(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
