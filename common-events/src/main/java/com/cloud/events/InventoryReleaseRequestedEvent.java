package com.cloud.events;

import java.time.Instant;

public record InventoryReleaseRequestedEvent(
        Long orderId,
        Long productId,
        Integer quantity,
        String reason,
        Instant requestedAt
) {
}
