package dev.github.sterio0o.orderservice.util;

import dev.github.sterio0o.common.util.ErrorResponse;
import dev.github.sterio0o.orderservice.exception.*;
import dev.github.sterio0o.orderservice.exception.light.LightOrderNotFoundException;
import dev.github.sterio0o.orderservice.exception.light.LightPdfGenerateException;
import dev.github.sterio0o.orderservice.exception.light.LightProductNotFoundException;
import dev.github.sterio0o.orderservice.exception.light.LightQRCodeGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    // QRCodeGenerationException
    @ExceptionHandler(LightQRCodeGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQRCodeGeneration(LightQRCodeGenerationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 - Ошибка сервера
    }

    // OrderNotFoundException
    @ExceptionHandler(LightOrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(LightOrderNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 - NOT FOUND
    }

    // ProductNotFoundException
    @ExceptionHandler(LightProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(LightProductNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 - NOT FOUND
    }

    // PdfGenerateException
    @ExceptionHandler(LightPdfGenerateException.class)
    public ResponseEntity<ErrorResponse> handlePdfGenerate(LightPdfGenerateException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 - ошибка сервера
    }
}
