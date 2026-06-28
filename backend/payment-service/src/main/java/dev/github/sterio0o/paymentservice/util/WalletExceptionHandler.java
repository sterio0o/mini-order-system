package dev.github.sterio0o.paymentservice.util;

import dev.github.sterio0o.common.util.ErrorResponse;
import dev.github.sterio0o.paymentservice.exception.NotEnoughMoneyException;
import dev.github.sterio0o.paymentservice.exception.PaymentNotFoundException;
import dev.github.sterio0o.paymentservice.exception.WalletAlreadyCreatedException;
import dev.github.sterio0o.paymentservice.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WalletExceptionHandler {

    // WalletNotFoundException
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 NOT FOUND
    }

    // PaymentNotFoundException
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFound(PaymentNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 NOT FOUND
    }

    // WalletAlreadyCreatedException
    @ExceptionHandler(WalletAlreadyCreatedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyCreated(WalletAlreadyCreatedException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 CONFLICT
    }

    // NotEnoughMoneyException
    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughMoney(NotEnoughMoneyException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.PAYMENT_REQUIRED.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse); // 402 - ошибка оплаты
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // 400 - BAD REQUEST
    }

    // InternalServerError
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse); // 500 Ошибка сервера
    }
}
