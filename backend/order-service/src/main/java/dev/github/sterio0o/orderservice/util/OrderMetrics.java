package dev.github.sterio0o.orderservice.util;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {
    private final Counter createAttempt;
    private final Counter createSuccess;
    private final MeterRegistry registry;

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.createAttempt = Counter.builder("order.create.attempt")
                .description("Количество попыток создания заказа")
                .register(registry);
        this.createSuccess = Counter.builder("order.create.success")
                .description("Количество успешно созданных заказов")
                .register(registry);
    }

    public void recordCreateAttempt() {
        createAttempt.increment();
    }

    public void recordCreateSuccess() {
        createSuccess.increment();
    }

    // Тег reason - тег исключения
    public void recordError(Throwable e) {
        registry.counter("order.error", "reason", e.getClass().getSimpleName()).increment();
    }
}
