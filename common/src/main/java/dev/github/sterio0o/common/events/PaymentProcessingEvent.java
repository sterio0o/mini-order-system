package dev.github.sterio0o.common.events;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentProcessingEvent(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        String paymentStatus
) {
}
