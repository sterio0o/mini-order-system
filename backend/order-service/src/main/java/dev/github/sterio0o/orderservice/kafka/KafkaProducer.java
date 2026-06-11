package dev.github.sterio0o.orderservice.kafka;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String ORDER_CREATED_EVENT = "order-created-event";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(ORDER_CREATED_EVENT, event);
    }
}
