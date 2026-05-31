package com.rajcloud.order;

import com.rajcloud.events.InventoryRejectedEvent;
import com.rajcloud.events.InventoryReservedEvent;
import com.rajcloud.events.PaymentProcessedEvent;
import com.rajcloud.observability.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentEventListenerTest {
    private final OrderRepository repository = mock(OrderRepository.class);
    private final OutboxPublisher outboxPublisher = mock(OutboxPublisher.class);
    private final PaymentEventListener listener = new PaymentEventListener(repository, outboxPublisher);

    @Test
    void inventoryReservedMarksOrderReserved() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 1, BigDecimal.TEN);
        ReflectionTestUtils.setField(order, "id", 10L);
        when(repository.findById(10L)).thenReturn(Optional.of(order));

        listener.onInventoryReserved(new InventoryReservedEvent(10L, 1L, 1001L, 1, BigDecimal.TEN, Instant.now()));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.INVENTORY_RESERVED);
    }

    @Test
    void inventoryRejectedCancelsOrder() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 1, BigDecimal.TEN);
        when(repository.findById(10L)).thenReturn(Optional.of(order));

        listener.onInventoryRejected(new InventoryRejectedEvent(10L, 1L, 1001L, 1, "No stock", Instant.now()));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void approvedPaymentMarksPaid() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 1, BigDecimal.TEN);
        when(repository.findById(10L)).thenReturn(Optional.of(order));

        listener.onPaymentProcessed(new PaymentProcessedEvent(5L, 10L, 1L, BigDecimal.TEN, "APPROVED", Instant.now()));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void declinedPaymentMarksFailedAndRequestsInventoryRelease() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 1, BigDecimal.TEN);
        ReflectionTestUtils.setField(order, "id", 10L);
        when(repository.findById(10L)).thenReturn(Optional.of(order));

        listener.onPaymentProcessed(new PaymentProcessedEvent(5L, 10L, 1L, BigDecimal.TEN, "DECLINED", Instant.now()));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        verify(outboxPublisher).save(org.mockito.ArgumentMatchers.eq(KafkaTopics.INVENTORY_RELEASE_REQUESTED),
                org.mockito.ArgumentMatchers.eq("10"), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void missingOrderThrows() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listener.onPaymentProcessed(
                new PaymentProcessedEvent(5L, 10L, 1L, BigDecimal.TEN, "APPROVED", Instant.now())))
                .isInstanceOf(RuntimeException.class);
    }
}
