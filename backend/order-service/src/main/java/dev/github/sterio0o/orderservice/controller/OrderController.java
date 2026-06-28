package dev.github.sterio0o.orderservice.controller;

import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.service.OrderService;
import dev.github.sterio0o.orderservice.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final QRCodeService qrCodeService;

    // Endpoints

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable UUID id) {
        OrderResponseDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/qrcode/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable UUID id) {
        try {
            byte[] qrCode = qrCodeService.generateQRCode(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline");

            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponseDto>> getAllOrders(
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<OrderResponseDto>> getMyOrders(
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(orderService.getMyOrders(pageable, UUID.fromString(userId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequestDto requestDto,
            @AuthenticationPrincipal String userId
    ) {
        OrderResponseDto order = orderService.createOrder(requestDto, UUID.fromString(userId));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.id())
                .toUri();

        return ResponseEntity.created(location).body(order);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
