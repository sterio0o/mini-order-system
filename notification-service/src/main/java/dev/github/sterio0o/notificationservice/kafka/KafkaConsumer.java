package dev.github.sterio0o.notificationservice.kafka;

import dev.github.sterio0o.common.events.DeliveryCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.notificationservice.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {
    private final MailSenderService mailSenderService;

    @KafkaListener(topics = "payment-processing-event", groupId = "notification-service")
    public void paymentProcessingListener(PaymentProcessingEvent event) {
        log.info("Kafka сonsumer accepted the event: {}", event);
        mailSenderService.handlePaymentEvent(event);
    }

    @KafkaListener(topics = "delivery-created-event", groupId = "notification-service")
    public void deliveryCreatedListener(DeliveryCreatedEvent event) {
        log.info("Kafka сonsumer accepted the event: {}", event);
        mailSenderService.handleDeliveryEvent(event);
    }
}
