package com.rajcloud.notification;

import com.rajcloud.events.PaymentProcessedEvent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentEventListenerTest {
    @Test
    void paymentProcessedCreatesNotification() {
        NotificationRepository repository = mock(NotificationRepository.class);

        new PaymentEventListener(repository).onPaymentProcessed(
                new PaymentProcessedEvent(9L, 10L, 1L, BigDecimal.TEN, "APPROVED", Instant.now()));

        verify(repository).save(org.mockito.ArgumentMatchers.argThat(notification ->
                notification.getOrderId().equals(10L)
                        && notification.getUserId().equals(1L)
                        && notification.getMessage().contains("APPROVED")));
    }
}
