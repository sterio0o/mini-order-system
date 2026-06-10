package dev.github.sterio0o.orderservice.model.dto;

import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        String customerEmail,
        List<OrderItemResponseDto> items,
        BigDecimal amount,
        OrderStatus status,
        LocalDateTime createdAt
) {
    public static OrderResponseDto fromEntity(Order order) {
        List<OrderItemResponseDto> itemDtos = order.getOrderItems().stream()
                .map(OrderItemResponseDto::fromEntity)
                .toList();

        return new OrderResponseDto(
                order.getId(),
                order.getCustomerEmail(),
                itemDtos,
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
