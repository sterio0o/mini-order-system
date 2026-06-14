package dev.github.sterio0o.paymentservice.exception;

public class WalletAlreadyCreatedException extends RuntimeException {
    public WalletAlreadyCreatedException(String message) {
        super(message);
    }
}
