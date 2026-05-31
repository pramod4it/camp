package com.rajcloud.events;

import java.time.Instant;

public record InventoryRejectedEvent(
        Long orderId,
        Long userId,
        Long productId,
        Integer quantity,
        String reason,
        Instant rejectedAt
) {
}
