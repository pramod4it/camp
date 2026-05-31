package com.cloud.events;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EventRecordsTest {
    @Test
    void recordAccessorsReturnConstructorValues() {
        Instant now = Instant.parse("2026-05-31T10:15:30Z");

        var order = new OrderCreatedEvent(1L, 2L, 1001L, 3, BigDecimal.TEN, now);
        var reserved = new InventoryReservedEvent(1L, 2L, 1001L, 3, BigDecimal.TEN, now);
        var rejected = new InventoryRejectedEvent(1L, 2L, 1001L, 3, "Insufficient stock", now);
        var release = new InventoryReleaseRequestedEvent(1L, 1001L, 3, "Payment failed", now);
        var payment = new PaymentProcessedEvent(9L, 1L, 2L, BigDecimal.TEN, "APPROVED", now);
        var refund = new PaymentRefundedEvent(9L, 1L, 2L, BigDecimal.TEN, "Cancelled", now);

        assertThat(order.productId()).isEqualTo(1001L);
        assertThat(reserved.quantity()).isEqualTo(3);
        assertThat(rejected.reason()).isEqualTo("Insufficient stock");
        assertThat(release.reason()).isEqualTo("Payment failed");
        assertThat(payment.status()).isEqualTo("APPROVED");
        assertThat(refund.reason()).isEqualTo("Cancelled");
    }
}
