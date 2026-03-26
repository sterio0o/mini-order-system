package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.orderservice.event.OrderCreatedEvent;
import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.ProductNotFoundException;
import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.model.entities.Product;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import dev.github.sterio0o.orderservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderResponseDto getOrderById(UUID id) {
        log.info("getOrderById: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID=" + id + " not found"));

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {
        log.info("createOrder for email: {}", requestDto.customerEmail());
        Product product = productRepository.findByName(requestDto.product())
                .orElseThrow(() -> new ProductNotFoundException(
                        "Product with name=" + requestDto.product() + " not found"
                )
        );

        BigDecimal totalAmount = calculateTotalAmount(requestDto.quantity(), product.getPrice());

        Order newOrder = new Order().builder()
                .customerEmail(requestDto.customerEmail())
                .product(product)
                .quantity(requestDto.quantity())
                .status(OrderStatus.ORDER_CREATED)
                .amount(totalAmount)
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        OrderCreatedEvent event = new OrderCreatedEvent(
                savedOrder.getId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getProduct().getId(),
                savedOrder.getQuantity(),
                savedOrder.getAmount()
        );

        log.info("Kafka: order-created-event {}", event);
        kafkaTemplate.send("order-created-event", event.orderId());

        return OrderResponseDto.fromEntity(savedOrder);
    }

    private BigDecimal calculateTotalAmount(Integer quantity, Integer price) {
        BigDecimal quantityDecimal = new BigDecimal(quantity);
        BigDecimal priceDecimal = new BigDecimal(price);

        BigDecimal totalAmount = quantityDecimal.multiply(priceDecimal);
        log.info("CalculateTotalAmount: {}", totalAmount);

        return totalAmount;
    }
}
