package dev.github.sterio0o.orderservice.util;

import dev.github.sterio0o.orderservice.model.entities.OrderStatus;
import dev.github.sterio0o.orderservice.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {
    private final Counter createAttempt;
    private final Counter createSuccess;
    private final Counter cacheHit;
    private final Counter cacheMiss;
    private final MeterRegistry registry;

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.createAttempt = Counter.builder("order.create.attempt")
                .description("Количество попыток создания заказа")
                .register(registry);
        this.createSuccess = Counter.builder("order.create.success")
                .description("Количество успешно созданных заказов")
                .register(registry);
        this.cacheHit = Counter.builder("order.cache.hit")
                .description("Количество успешных извлечений данных из кеша")
                .register(registry);
        this.cacheMiss = Counter.builder("order.cache.miss")
                .description("Количество раз когда данные были взяты не из кеша")
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

    public void pendingOrderGauge(OrderRepository repository) {
        Gauge.builder("orders.pending.payment.count", repository,
                repo -> repo.countByStatus(OrderStatus.PENDING_PAYMENT))
                .description("Количество заказов, ожидающих оплату")
                .register(registry);
    }

    public void recordCacheHit() {
        cacheHit.increment();
    }

    public void recordCacheMiss() {
        cacheMiss.increment();
    }
}
