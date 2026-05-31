package com.cloud.events;

import java.math.BigDecimal;
import java.time.Instant;

public record InventoryReservedEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        BigDecimal amount,
        Instant reservedAt
) {
}
