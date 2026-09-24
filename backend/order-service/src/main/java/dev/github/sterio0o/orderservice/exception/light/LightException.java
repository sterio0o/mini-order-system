package dev.github.sterio0o.orderservice.exception.light;

// Отключает тяжелый стек-трейс
public abstract class LightException extends RuntimeException {
    public LightException(String message) {
        super(message, null, false, false);
    }
}
