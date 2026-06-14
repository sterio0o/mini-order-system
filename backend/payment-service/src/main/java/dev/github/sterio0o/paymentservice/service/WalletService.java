package dev.github.sterio0o.paymentservice.service;

import dev.github.sterio0o.paymentservice.exception.NotEnoughMoneyException;
import dev.github.sterio0o.paymentservice.exception.WalletAlreadyCreatedException;
import dev.github.sterio0o.paymentservice.exception.WalletNotFoundException;
import dev.github.sterio0o.paymentservice.model.dto.WalletOperationDto;
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
                .orElseThrow(() -> new WalletNotFoundException("Wallet для user=" + userId + " не найден"));

        return WalletResponseDto.fromEntity(wallet);
    }

    @Transactional
    public WalletResponseDto createWallet(UUID userId) {
        if (walletRepository.existsByUserId(userId))
            throw new WalletAlreadyCreatedException("Wallet для user=" + userId + " уже существует");

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        Wallet savedWallet = walletRepository.save(wallet);
        return WalletResponseDto.fromEntity(savedWallet);
    }

    @Transactional
    public WalletResponseDto operationTransaction(UUID userId, WalletOperationDto operationDto) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet by userId=" + userId + " не найден"));

        BigDecimal amount = operationDto.sum();
        BigDecimal currentBalance = wallet.getBalance();

        switch (operationDto.type()) {
            case DEPOSIT -> wallet.setBalance(currentBalance.add(amount));
            case WITHDRAWAL -> {
                if (currentBalance.compareTo(amount) < 0)
                    throw new NotEnoughMoneyException("Не достаточно средств на балансе");
                wallet.setBalance(currentBalance.subtract(amount));
            }
            default -> throw new IllegalArgumentException("Неверный тип банковский операции");
        }

        Wallet savedWallet = walletRepository.save(wallet);
        return WalletResponseDto.fromEntity(savedWallet);
    }
}
