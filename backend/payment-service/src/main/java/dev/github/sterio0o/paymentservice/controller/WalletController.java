package dev.github.sterio0o.paymentservice.controller;

import dev.github.sterio0o.paymentservice.exception.NotEnoughMoneyException;
import dev.github.sterio0o.paymentservice.exception.WalletAlreadyCreatedException;
import dev.github.sterio0o.paymentservice.exception.WalletNotFoundException;
import dev.github.sterio0o.paymentservice.model.dto.WalletRequestDto;
import dev.github.sterio0o.paymentservice.model.dto.WalletResponseDto;
import dev.github.sterio0o.paymentservice.service.WalletService;
import dev.github.sterio0o.paymentservice.util.WalletErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Endpoints

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDto> getWallet(@AuthenticationPrincipal String userId) {
        WalletResponseDto wallet = walletService.getWalletByUserId(UUID.fromString(userId));
        return ResponseEntity.ok(wallet);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDto> createWallet(@AuthenticationPrincipal String userId) {
        WalletResponseDto wallet = walletService.createWallet(UUID.fromString(userId));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();
        return ResponseEntity.created(location).body(wallet);
    }

    @PatchMapping("/balance/top-up")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDto> topUpBalance(
            @AuthenticationPrincipal String userId,
            @RequestBody WalletRequestDto requestDto
    ) {
        WalletResponseDto wallet = walletService.topUpBalance(UUID.fromString(userId), requestDto);
        return ResponseEntity.ok(wallet);
    }

    @PatchMapping("/balance/withdraw-money")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDto> withdrawMoney(
            @AuthenticationPrincipal String userId,
            @RequestBody WalletRequestDto requestDto
    ) {
        WalletResponseDto wallet = walletService.withdrawMoney(UUID.fromString(userId), requestDto);
        return ResponseEntity.ok(wallet);
    }

    // ExceptionHandlers

    // WalletNotFoundException
    @ExceptionHandler
    private ResponseEntity<WalletErrorResponse> handleException(WalletNotFoundException e) {
        WalletErrorResponse errorResponse = new WalletErrorResponse(
                "Wallet by this user not found"
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 NOT FOUND
    }

    // WalletAlreadyCreatedException
    @ExceptionHandler
    private ResponseEntity<WalletErrorResponse> handleException(WalletAlreadyCreatedException e) {
        WalletErrorResponse errorResponse = new WalletErrorResponse("Wallet by this user already created");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse); // 409 CONFLICT
    }

    // NotEnoughMoneyException
    @ExceptionHandler
    private ResponseEntity<WalletErrorResponse> handleException(NotEnoughMoneyException e) {
        WalletErrorResponse errorResponse = new WalletErrorResponse("Not enough money on balance");
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse); // 402 - ошибка оплаты
    }

}
