package dev.github.sterio0o.paymentservice.exception.light;

public abstract class LightException extends RuntimeException {
    public LightException(String message) {
        super(message, null, false, false);
    }
}
