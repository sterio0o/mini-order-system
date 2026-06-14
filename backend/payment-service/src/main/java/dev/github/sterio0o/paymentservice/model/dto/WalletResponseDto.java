package dev.github.sterio0o.paymentservice.model.dto;

import dev.github.sterio0o.paymentservice.model.entity.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record WalletResponseDto(
        UUID id,
        UUID userId,
        BigDecimal balance
) {
    public static WalletResponseDto fromEntity(Wallet entity) {
        return new WalletResponseDto(
                entity.getId(),
                entity.getUserId(),
                entity.getBalance()
        );
    }
}
