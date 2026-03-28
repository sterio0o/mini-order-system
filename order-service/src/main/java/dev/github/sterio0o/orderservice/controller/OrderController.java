package dev.github.sterio0o.orderservice.controller;

import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto) {
        OrderResponseDto order = orderService.createOrder(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.id())
                .toUri();

        return ResponseEntity.created(location).body(order);
    }
}
