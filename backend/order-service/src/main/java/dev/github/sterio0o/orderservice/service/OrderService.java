package dev.github.sterio0o.orderservice.service;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.ProductNotFoundException;
import dev.github.sterio0o.orderservice.kafka.KafkaProducer;
import dev.github.sterio0o.orderservice.model.dto.OrderItemRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderRequestDto;
import dev.github.sterio0o.orderservice.model.dto.OrderResponseDto;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderItem;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.model.entities.Product;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import dev.github.sterio0o.orderservice.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final KafkaProducer kafkaProducer;

    public OrderResponseDto getOrderById(UUID id) {
        log.info("getOrderById: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID=" + id + " not found"));

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto, UUID userId) {
        log.info("createOrder for email: {}", requestDto.customerEmail());

        Order newOrder = Order.builder()
                .userId(userId)
                .customerEmail(requestDto.customerEmail())
                .orderItems(new ArrayList<>())
                .status(OrderStatus.PENDING_PAYMENT)
                .amount(BigDecimal.ZERO)
                .build();

        // Обработка каждой позиции заказа
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequestDto item : requestDto.items()) {
            Product product = productRepository.findByProductName(item.productName())
                    .orElseThrow(() -> new ProductNotFoundException(
                                    "Product with name=" + item.productName() + " not found")
                    );

            BigDecimal priceToOrderItem = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.quantity()));

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .price(priceToOrderItem)
                    .quantity(item.quantity())
                    .build();

            newOrder.addOrderItem(orderItem);
            totalAmount = totalAmount.add(priceToOrderItem);
        }

        newOrder.setAmount(totalAmount);

        Order savedOrder = orderRepository.save(newOrder);

        OrderCreatedEvent event = new OrderCreatedEvent(
                userId,
                savedOrder.getId(),
                savedOrder.getCustomerEmail(),
                savedOrder.getAmount()
        );

        log.info("Kafka: order-created-event {}", event);
        kafkaProducer.sendEvent(event);

        return OrderResponseDto.fromEntity(savedOrder);
    }
}
