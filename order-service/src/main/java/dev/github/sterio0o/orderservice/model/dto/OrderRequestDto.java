package dev.github.sterio0o.orderservice.model.dto;

import java.util.List;

public record OrderRequestDto(
        String customerEmail,
        List<OrderItemRequestDto> items
) {

}
