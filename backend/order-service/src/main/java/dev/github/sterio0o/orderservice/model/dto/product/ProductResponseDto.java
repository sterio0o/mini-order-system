package dev.github.sterio0o.orderservice.model.dto.product;

import dev.github.sterio0o.orderservice.model.entities.Product;

import java.io.Serializable;
import java.util.UUID;

public record ProductResponseDto(
        UUID id,
        String productName,
        Integer price
) implements Serializable {
    public static ProductResponseDto fromEntity(Product entity) {
        return new ProductResponseDto(
                entity.getId(),
                entity.getProductName(),
                entity.getPrice()
        );
    }
}
