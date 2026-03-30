package dev.github.sterio0o.paymentservice.exception;

public class PaymentNotFoundException extends BasePaymentException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
