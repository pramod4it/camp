package com.cloud.search;

import com.cloud.events.OrderCreatedEvent;
import com.cloud.events.PaymentProcessedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SearchEventListenerTest {
    private final OrderSearchRepository repository = mock(OrderSearchRepository.class);
    private final SearchEventListener listener = new SearchEventListener(repository);

    @Test
    void orderCreatedIndexesPendingDocument() {
        listener.onOrderCreated(new OrderCreatedEvent(10L, 1L, 1001L, 1, BigDecimal.TEN, Instant.now()));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(document ->
                document.getOrderId().equals(10L) && "PENDING".equals(document.getPaymentStatus())));
    }

    @Test
    void paymentProcessedUpdatesIndexedDocument() {
        listener.onPaymentProcessed(new PaymentProcessedEvent(5L, 10L, 1L, BigDecimal.TEN, "APPROVED", Instant.now()));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(document ->
                document.getOrderId().equals(10L) && "APPROVED".equals(document.getPaymentStatus())));
    }
}
