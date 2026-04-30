package dev.github.sterio0o.deliveryservice.repository;

import dev.github.sterio0o.deliveryservice.model.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
}
