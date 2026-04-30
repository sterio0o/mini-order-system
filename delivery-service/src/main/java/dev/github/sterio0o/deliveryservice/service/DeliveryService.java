package dev.github.sterio0o.deliveryservice.service;

import dev.github.sterio0o.common.events.DeliveryCreatedEvent;
import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.deliveryservice.kafka.KafkaProducer;
import dev.github.sterio0o.deliveryservice.model.entity.Delivery;
import dev.github.sterio0o.deliveryservice.model.entity.DeliveryStatus;
import dev.github.sterio0o.deliveryservice.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final KafkaProducer kafkaProducer;

    @Transactional
    public void processDelivery(PaymentProcessingEvent event) {
        if (!event.paymentStatus().equals("COMPLETED")) {
            log.info("Payment not completed for order {} skipping delivery", event.orderId());
            return;
        }

        if (deliveryRepository.existsById(event.paymentId())) {
            log.info("Delivery already created for order {}", event.orderId());
            return;
        }

        Delivery delivery = Delivery.builder()
                .orderId(event.orderId())
                .paymentId(event.paymentId())
                .customerEmail(event.email())
                .status(DeliveryStatus.PENDING)
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(3))
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);

        DeliveryCreatedEvent deliveryCreatedEvent = new DeliveryCreatedEvent(
                savedDelivery.getId(),
                savedDelivery.getOrderId(),
                savedDelivery.getPaymentId(),
                savedDelivery.getCustomerEmail(),
                savedDelivery.getTrackingNumber(),
                savedDelivery.getEstimatedDeliveryDate(),
                DeliveryStatus.PENDING.toString()
        );

        kafkaProducer.sendEvent(deliveryCreatedEvent);
        log.info("Delivery created: {} for order {}", savedDelivery.getTrackingNumber(), savedDelivery.getOrderId());
    }

    @Transactional
    public void updateDeliveryStatus(UUID deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery with ID=" + deliveryId + " not found"));

        delivery.setStatus(newStatus);
        deliveryRepository.save(delivery);

        log.info("Delivery {} status updated to {}", deliveryId, newStatus);

        // Мб отправить событие обновление заказа
    }
}
