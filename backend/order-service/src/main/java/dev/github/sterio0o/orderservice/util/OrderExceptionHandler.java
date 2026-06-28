package dev.github.sterio0o.orderservice.util;

import dev.github.sterio0o.common.util.ErrorResponse;
import dev.github.sterio0o.orderservice.exception.OrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.ProductNotFoundException;
import dev.github.sterio0o.orderservice.exception.QRCodeGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    // QRCodeGenerationException
    @ExceptionHandler(QRCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQRCodeGeneration(QRCodeGenerationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 - Ошибка сервера
    }

    // OrderNotFoundException
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 - NOT FOUND
    }

    // ProductNotFoundException
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 - NOT FOUND
    }
}
