package dev.github.sterio0o.paymentservice.service;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.paymentservice.kafka.KafkaProducer;
import dev.github.sterio0o.paymentservice.model.entity.Payment;
import dev.github.sterio0o.paymentservice.model.entity.PaymentStatus;
import dev.github.sterio0o.paymentservice.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final KafkaProducer kafkaProducer;

    /*
        Главный метод ответственный за весь платеж
        Из order-service приходит событие на оплату, этот метод вызывается и управляет всем созданием платежа
     */

    public void paymentProcessing(OrderCreatedEvent event) {
        if (paymentRepository.existsByOrderId(event.orderId())) {
            log.info("Order {} already processed", event.orderId());
            return;
        }

        String email = event.customerEmail();
        Payment payment = createPayment(event);

        try {
            Thread.sleep(2000);

            // Имитация платежа
            boolean success = paymentGateway.process();

            if (success) {
                processPayment(payment, email);
            } else {
                failedPayment(payment, email);
            }

        } catch (InterruptedException e) {
            log.info("Payment ID={} for order ID={} is FAILED", payment.getId(), payment.getOrderId());
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            Thread.currentThread().interrupt();
        }
    }

    private Payment createPayment(OrderCreatedEvent event) {
        Payment payment = new Payment();
        payment.setOrderId(event.orderId());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8));
        payment.setAmount(event.amount());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment ID={} for order ID={} is CREATED", savedPayment.getId(), savedPayment.getOrderId());

        return savedPayment;
    }

    private void processPayment(Payment payment, String email) {
        log.info("Process payment ID={}", payment.getId());

        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        sendPaymentProcessingEvent(savedPayment, email);
    }

    private void failedPayment(Payment payment, String email) {
        log.info("Failed payment ID={}", payment.getId());

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        sendPaymentProcessingEvent(savedPayment, email);
    }

    private void refundPayment(Payment payment, String email) {
        log.info("Refund payment ID={}", payment.getId());

        payment.setPaymentStatus(PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        sendPaymentProcessingEvent(savedPayment, email);
    }

    private void sendPaymentProcessingEvent(Payment payment, String email) {
        PaymentProcessingEvent event = new PaymentProcessingEvent(
                payment.getId(),
                payment.getOrderId(),
                email,
                payment.getAmount(),
                payment.getPaymentStatus().toString()
        );

        kafkaProducer.sendPaymentProcessingEvent(event);
    }
}
