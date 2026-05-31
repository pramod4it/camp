package com.rajcloud.order;

public enum OrderStatus {
    PENDING,
    INVENTORY_RESERVED,
    INVENTORY_REJECTED,
    PAID,
    PAYMENT_FAILED,
    CANCELLED
}
