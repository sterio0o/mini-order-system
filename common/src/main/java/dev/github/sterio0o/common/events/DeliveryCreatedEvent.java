package dev.github.sterio0o.common.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryCreatedEvent(
        UUID deliveryId,
        UUID orderId,
        UUID paymentId,
        String customerEmail,
        String trackingNumber,
        LocalDateTime estimatedDeliveryDate,
        String status
) {
}
