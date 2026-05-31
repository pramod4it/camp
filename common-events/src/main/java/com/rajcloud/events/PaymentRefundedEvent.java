package com.rajcloud.events;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRefundedEvent(
        Long paymentId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String reason,
        Instant refundedAt
) {
}
