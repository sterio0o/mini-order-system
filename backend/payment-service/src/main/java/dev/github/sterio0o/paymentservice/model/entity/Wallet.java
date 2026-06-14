package dev.github.sterio0o.paymentservice.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;
    
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @PrePersist
    public void onCreate() {
        if (balance == null)
            balance = BigDecimal.ZERO;
    }
}
