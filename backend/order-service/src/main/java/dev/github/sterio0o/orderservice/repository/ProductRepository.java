package dev.github.sterio0o.orderservice.repository;

import dev.github.sterio0o.orderservice.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByProductName(String productName);

    Optional<Product> findByProductNameIgnoreCase(String productName);

    List<Product> findAll();
}
