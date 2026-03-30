package dev.github.sterio0o.common.events;

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
