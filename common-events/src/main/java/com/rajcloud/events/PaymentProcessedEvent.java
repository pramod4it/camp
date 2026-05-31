package com.rajcloud.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentProcessedEvent(
        Long paymentId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String status,
        Instant processedAt
) {
}
