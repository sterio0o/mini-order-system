package dev.github.sterio0o.deliveryservice.kafka;

import dev.github.sterio0o.common.events.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String DELIVERY_CREATED_EVENT = "delivery-created-event";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(DeliveryCreatedEvent event) {
        kafkaTemplate.send(DELIVERY_CREATED_EVENT, event);
    }

}
