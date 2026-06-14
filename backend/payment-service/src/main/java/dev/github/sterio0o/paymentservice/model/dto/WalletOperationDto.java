package dev.github.sterio0o.paymentservice.model.dto;

import dev.github.sterio0o.paymentservice.model.entity.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WalletOperationDto(
        @NotNull
        @Positive
        BigDecimal sum,

        @NotNull
        OperationType type
) {
}
