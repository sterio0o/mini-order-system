package dev.github.sterio0o.orderservice.model.dto;

public record OrderRequestDto(
        String customerEmail,
        String productName,
        Integer quantity
) {

}
