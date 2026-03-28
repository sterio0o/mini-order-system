package dev.github.sterio0o.orderservice.repository;

import dev.github.sterio0o.orderservice.model.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findById(UUID id);
}
