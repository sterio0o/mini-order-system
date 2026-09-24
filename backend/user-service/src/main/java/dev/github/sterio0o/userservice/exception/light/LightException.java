package dev.github.sterio0o.userservice.exception.light;

public abstract class LightException extends RuntimeException {
    public LightException(String message) {
        super(message, null, false, false);
    }
}
