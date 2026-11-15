package dev.github.sterio0o.paymentservice.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PaymentMetrics {
    private final MeterRegistry registry;
    private final Counter paymentAttempt;
    private final Counter paymentSuccess;
    private final Timer paymentProcessingTimer;

    public PaymentMetrics(MeterRegistry registry) {
        this.registry = registry;

        this.paymentAttempt = Counter.builder("payment.attempt")
                .description("Количество полученных событий на оплату")
                .register(registry);

        this.paymentSuccess = Counter.builder("payment.success")
                .description("Количество успешных оплат")
                .register(registry);

        this.paymentProcessingTimer = Timer.builder("payment.processing.timer")
                .description("Время обработки платежа")
                .publishPercentiles(0.5, 0.95, 0.99)    // p50, p95, p99
                .register(registry);
    }

    public void recordPaymentAttempt() {
        paymentAttempt.increment();
    }

    public void recordPaymentSuccess(long durationMs) {
        paymentSuccess.increment();
        paymentProcessingTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordPaymentError(Throwable e) {
        registry.counter("payment.error", "reason", e.getClass().getSimpleName()).increment();
    }

}
