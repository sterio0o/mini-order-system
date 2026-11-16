package dev.github.sterio0o.orderservice.model.dto.product;

import java.util.UUID;

public record ProductRequestDto(
        UUID id,
        String productName,
        Integer price
) {
}
