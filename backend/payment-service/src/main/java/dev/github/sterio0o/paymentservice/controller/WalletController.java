package dev.github.sterio0o.paymentservice.controller;

import dev.github.sterio0o.paymentservice.model.dto.WalletOperationDto;
import dev.github.sterio0o.paymentservice.model.dto.WalletResponseDto;
import dev.github.sterio0o.paymentservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

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

    @PatchMapping("/transaction")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<WalletResponseDto> operation(
            @AuthenticationPrincipal String userId,
            @RequestBody WalletOperationDto operationDto
    ) {
        WalletResponseDto wallet = walletService.operationTransaction(UUID.fromString(userId), operationDto);
        return ResponseEntity.ok(wallet);
    }
}
