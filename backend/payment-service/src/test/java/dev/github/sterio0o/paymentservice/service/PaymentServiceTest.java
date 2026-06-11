package dev.github.sterio0o.paymentservice.service;

import dev.github.sterio0o.common.events.OrderCreatedEvent;
import dev.github.sterio0o.common.events.PaymentProcessingEvent;
import dev.github.sterio0o.paymentservice.kafka.KafkaProducer;
import dev.github.sterio0o.paymentservice.model.entity.Payment;
import dev.github.sterio0o.paymentservice.model.entity.PaymentStatus;
import dev.github.sterio0o.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void paymentProcessing_processPayment() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                "test@example.com",
                UUID.randomUUID(),
                2,
                BigDecimal.valueOf(1400)
        );

        Payment payment = createPendingPayment(event);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment)
                .thenReturn(createCompletedPayment(payment));
        when(paymentGateway.process()).thenReturn(true);

        paymentService.paymentProcessing(event);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(2)).save(paymentArgumentCaptor.capture());

        List<Payment> savedPayment = paymentArgumentCaptor.getAllValues();

        assertEquals(PaymentStatus.PENDING, savedPayment.get(0).getPaymentStatus());
        assertEquals(PaymentStatus.COMPLETED, savedPayment.get(1).getPaymentStatus());
        assertNotNull(savedPayment.get(1).getPaidAt());

        verify(kafkaProducer).sendPaymentProcessingEvent(any(PaymentProcessingEvent.class));
    }

    @Test
    void paymentProcessing_failedPayment() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                "test@example.com",
                UUID.randomUUID(),
                2,
                BigDecimal.valueOf(1400)
        );

        Payment payment = createPendingPayment(event);

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment)
                .thenReturn(createFailedPayment(payment));
        when(paymentGateway.process()).thenReturn(false);

        paymentService.paymentProcessing(event);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(2)).save(paymentArgumentCaptor.capture());

        List<Payment> savedPayments = paymentArgumentCaptor.getAllValues();

        assertEquals(PaymentStatus.PENDING, savedPayments.get(0).getPaymentStatus());
        assertEquals(PaymentStatus.FAILED, savedPayments.get(1).getPaymentStatus());
        assertNotNull(savedPayments.get(1).getUpdatedAt());

        verify(kafkaProducer).sendPaymentProcessingEvent(any(PaymentProcessingEvent.class));
    }

    private Payment createPendingPayment(OrderCreatedEvent event) {
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setOrderId(event.orderId());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setAmount(event.amount());
        payment.setCreateAt(LocalDateTime.now());

        return payment;
    }

    private final Payment createCompletedPayment(Payment pending) {
        Payment completed = new Payment();
        completed.setId(pending.getId());
        completed.setOrderId(pending.getOrderId());
        completed.setAmount(pending.getAmount());
        completed.setPaymentStatus(PaymentStatus.COMPLETED);
        completed.setCreateAt(pending.getCreateAt());
        completed.setPaidAt(LocalDateTime.now());
        completed.setUpdatedAt(LocalDateTime.now());

        return completed;
    }

    private Payment createFailedPayment(Payment pending) {
        Payment failed = new Payment();
        failed.setId(pending.getId());
        failed.setOrderId(pending.getOrderId());
        failed.setAmount(pending.getAmount());
        failed.setPaymentStatus(PaymentStatus.FAILED);
        failed.setCreateAt(pending.getCreateAt());
        failed.setUpdatedAt(LocalDateTime.now());
        return failed;
    }
}