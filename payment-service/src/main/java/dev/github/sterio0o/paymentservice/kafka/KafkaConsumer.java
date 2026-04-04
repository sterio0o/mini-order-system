package dev.github.sterio0o.paymentservice.kafka;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final PaymentService paymentService;

    // Слушает событие создания заказа и вызывает метод ответственный за оплату
    @KafkaListener(topics = "order-created-event", groupId = "payment-service")
    public void orderCreatedListener(OrderCreatedEvent event) {
        log.info("Kafka сonsumer accepted the event: {}", event);
        paymentService.paymentProcessing(event);
    }

}
