package dev.github.sterio0o.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID userId,
        UUID orderId,
        String customerEmail,
        BigDecimal amount
) {
}
