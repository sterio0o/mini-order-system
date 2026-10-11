package dev.github.sterio0o.orderservice.controller;

import dev.github.sterio0o.orderservice.model.dto.product.ProductRequestDto;
import dev.github.sterio0o.orderservice.model.dto.product.ProductResponseDto;
import dev.github.sterio0o.orderservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> getProductByName(@PathVariable String name) {
        ProductResponseDto product = productService.findProductInHotCache(name);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ProductResponseDto>> getAllOrders(
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findAllProducts(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto response = productService.createProduct(requestDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{name}")
                .buildAndExpand(response.productName())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody ProductRequestDto requestDto) {
        ProductResponseDto response = productService.updateProductHotCache(requestDto);
        return ResponseEntity.ok(response);
    }
}
