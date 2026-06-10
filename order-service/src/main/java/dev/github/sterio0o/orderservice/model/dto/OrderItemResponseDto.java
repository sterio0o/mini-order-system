package dev.github.sterio0o.orderservice.model.dto;

import dev.github.sterio0o.orderservice.model.entities.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        String productName,
        Integer quantity,
        BigDecimal price
) {
    public static OrderItemResponseDto fromEntity(OrderItem entity) {
        return new OrderItemResponseDto(
                entity.getProduct().getProductName(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }
}
