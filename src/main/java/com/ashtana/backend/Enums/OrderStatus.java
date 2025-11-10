package com.ashtana.backend.Enums;

public enum OrderStatus {
    PENDING,
    PENDING_PAYMENT, // ✅ Add this
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED, // ✅ Add this
    REFUNDED
}
