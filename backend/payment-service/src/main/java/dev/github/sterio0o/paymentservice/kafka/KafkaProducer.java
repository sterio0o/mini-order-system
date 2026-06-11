package dev.github.sterio0o.paymentservice.kafka;

import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private static final String PAYMENT_PROCESSING_EVENT = "payment-processing-event";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendPaymentProcessingEvent(PaymentProcessingEvent event) {
        kafkaTemplate.send(PAYMENT_PROCESSING_EVENT, event);
    }
}
