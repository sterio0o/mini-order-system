package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.ProductNotFoundException;
import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.model.entities.Product;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import dev.github.sterio0o.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrderByIdFailed() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        OrderNotFoundException expectedException = assertThrows(OrderNotFoundException.class, () ->
                orderService.getOrderById(id)
        );

        String expectedMessage = "Order with ID=" + id + " not found";
        assertEquals(expectedMessage, expectedException.getMessage());
    }

    @Test
    void getOrderByIdSuccess() {
        UUID id = UUID.randomUUID();
        String customerEmail = "test@example.com";
        Integer quantity = 2;
        BigDecimal amount = BigDecimal.valueOf(1200);

        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setProductName("Net Beans");
        product.setPrice(1200);

        Order order = new Order();
        order.setId(id);
        order.setCustomerEmail(customerEmail);
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setAmount(amount);
        order.setStatus(OrderStatus.ORDER_CREATED);
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findById(id)).thenReturn(Optional.of(order));

        OrderResponseDto result = orderService.getOrderById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(customerEmail, result.customerEmail());
        assertEquals(quantity, result.quantity());
        assertEquals(amount, result.amount());
        assertEquals(OrderStatus.ORDER_CREATED, result.status());
        assertEquals(product.getProductName(), result.product());
    }

    @Test
    void createOrderFailed() {
        String productName = "Non exist product name";
        when(productRepository.findByProductName(productName)).thenReturn(Optional.empty());

        OrderRequestDto requestDto = new OrderRequestDto(
                "test@example.com",
                productName,
                1
        );

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () ->
                orderService.createOrder(requestDto)
        );

        String expectedMessage = "Product with name=" + requestDto.productName() + " not found";

        assertEquals(expectedMessage, exception.getMessage());
    }

}