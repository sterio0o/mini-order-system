package dev.github.sterio0o.orderservice.model.dto;

import java.util.UUID;

public record OrderItemRequestDto(
        UUID productId,
        String productName,
        Integer quantity
) {
}
