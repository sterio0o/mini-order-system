package dev.github.sterio0o.paymentservice.service;

import dev.github.sterio0o.paymentservice.exception.NotEnoughMoneyException;
import dev.github.sterio0o.paymentservice.exception.WalletAlreadyCreatedException;
import dev.github.sterio0o.paymentservice.exception.WalletNotFoundException;
import dev.github.sterio0o.paymentservice.model.dto.WalletRequestDto;
import dev.github.sterio0o.paymentservice.model.dto.WalletResponseDto;
import dev.github.sterio0o.paymentservice.model.entity.Wallet;
import dev.github.sterio0o.paymentservice.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletResponseDto getWalletByUserId(UUID userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet for user=" + userId + " already created"));

        return WalletResponseDto.fromEntity(wallet);
    }

    @Transactional
    public WalletResponseDto createWallet(UUID userId) {
        if (walletRepository.existsByUserId(userId))
            throw new WalletAlreadyCreatedException("Wallet for user=" + userId + " already created");

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        Wallet savedWallet = walletRepository.save(wallet);
        return WalletResponseDto.fromEntity(savedWallet);
    }

    @Transactional
    public WalletResponseDto topUpBalance(UUID userId, WalletRequestDto requestDto) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet by userId=" + userId + " not found"));

        BigDecimal prevBalance = wallet.getBalance();
        BigDecimal newBalance = prevBalance.add(BigDecimal.valueOf(requestDto.sum()));
        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        return WalletResponseDto.fromEntity(savedWallet);
    }

    @Transactional
    public WalletResponseDto withdrawMoney(UUID userId, WalletRequestDto requestDto) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet by userId=" + userId + " not found"));

        BigDecimal prevBalance = wallet.getBalance();
        BigDecimal amount = BigDecimal.valueOf(requestDto.sum());

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Sum must be positive");

        if (prevBalance.compareTo(amount) < 0)
            throw new NotEnoughMoneyException("Not enough money on balance");

        wallet.setBalance(prevBalance.subtract(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        return WalletResponseDto.fromEntity(savedWallet);
    }
}
