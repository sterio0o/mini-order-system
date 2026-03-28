package dev.github.sterio0o.orderservice.event;

import dev.github.sterio0o.orderservice.model.entities.Product;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        String customerEmail,
        UUID product,
        Integer quantity,
        BigDecimal amount
) {

}
