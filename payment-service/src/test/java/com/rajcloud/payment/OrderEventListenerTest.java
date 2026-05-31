package com.rajcloud.payment;

import com.rajcloud.events.InventoryReservedEvent;
import com.rajcloud.observability.KafkaTopics;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEventListenerTest {
    private final PaymentRepository repository = mock(PaymentRepository.class);
    private final OutboxPublisher outboxPublisher = mock(OutboxPublisher.class);
    private final OrderEventListener listener = new OrderEventListener(repository, outboxPublisher);

    @Test
    void approvedPaymentIsSavedAndPublished() {
        Payment saved = new Payment(10L, 1L, new BigDecimal("5000.00"), PaymentStatus.APPROVED);
        when(repository.save(org.mockito.ArgumentMatchers.any(Payment.class))).thenReturn(saved);

        listener.onInventoryReserved(new InventoryReservedEvent(10L, 1L, 1001L, 1, new BigDecimal("5000.00"), Instant.now()));

        verify(outboxPublisher).save(org.mockito.ArgumentMatchers.eq(KafkaTopics.PAYMENT_PROCESSED),
                org.mockito.ArgumentMatchers.eq("10"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void amountAboveLimitIsDeclined() {
        Payment saved = new Payment(10L, 1L, new BigDecimal("5000.01"), PaymentStatus.DECLINED);
        when(repository.save(org.mockito.ArgumentMatchers.any(Payment.class))).thenReturn(saved);

        listener.onInventoryReserved(new InventoryReservedEvent(10L, 1L, 1001L, 1, new BigDecimal("5000.01"), Instant.now()));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(payment -> payment.getStatus() == PaymentStatus.DECLINED));
    }
}
