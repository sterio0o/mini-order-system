package dev.github.sterio0o.paymentservice.exception.light;

public class LightWalletNotFoundException extends RuntimeException {
    public LightWalletNotFoundException(String message) {
        super(message, null, false, false);
    }
}
