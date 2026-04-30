package dev.github.sterio0o.deliveryservice.kafka;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.deliveryservice.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final DeliveryService deliveryService;

    // Слушает событие оплаты и вызывает метод ответственный за создание заказа
    @KafkaListener(topics = "payment-processing-event", groupId = "delivery-service")
    public void orderCreatedListener(PaymentProcessingEvent event) {
        log.info("Kafka сonsumer accepted the event: {}", event);
        deliveryService.processDelivery(event);
    }

}