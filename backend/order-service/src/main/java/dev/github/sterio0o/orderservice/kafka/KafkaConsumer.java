package dev.github.sterio0o.orderservice.kafka;

import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.orderservice.model.entities.Order;
import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import dev.github.sterio0o.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(topics = "payment-processing-event", groupId = "order-service")
    @Transactional
    public void paymentProcessingEvent(PaymentProcessingEvent event) {
        log.info("ORDER-SERVICE: Kafka: обработка платежа от заказа: {}", event);

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + event.orderId()));

        if (event.paymentStatus().equals("COMPLETED")) {
            order.setStatus(OrderStatus.ORDER_CREATED);
            log.info("ORDER-SERVICE: order {} успешно создан, PAYMENT_COMPLETED", order.getId());
        } else if (event.paymentStatus().equals("FAILED")) {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
            log.info("ORDER-SERVICE: order {} ошибка оплаты, PAYMENT_FAILED", order.getId());
        } else {
            order.setStatus(OrderStatus.CANCELLED);
            log.info("ORDER-SERVICE: order {} отменен, CANCELLED", order.getId());
        }
    }

}
