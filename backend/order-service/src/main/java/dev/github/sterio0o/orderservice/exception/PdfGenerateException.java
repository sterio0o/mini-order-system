package dev.github.sterio0o.orderservice.exception;

public class PdfGenerateException extends RuntimeException {
    public PdfGenerateException(String message, Exception e) {
        super(message);
    }
}
