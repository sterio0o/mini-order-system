package dev.github.sterio0o.deliveryservice.model.entity;

public enum DeliveryStatus {
    PENDING,        // Ожидает
    PROCESSING,     // Готовиться к отправке
    SHIPPED,        // Отправлен
    DELIVERED,      // Доставлен
    CANCELED        // Отмене
}
