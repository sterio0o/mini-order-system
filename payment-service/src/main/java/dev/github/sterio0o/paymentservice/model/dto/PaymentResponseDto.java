package dev.github.sterio0o.paymentservice.model.dto;

import dev.github.sterio0o.paymentservice.model.entity.Payment;
import dev.github.sterio0o.paymentservice.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponseDto(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentStatus paymentStatus
) {
    public static PaymentResponseDto fromEntity(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrderId(),
                payment.getAmount(),
                payment.getPaymentStatus()
        );
    }
}
