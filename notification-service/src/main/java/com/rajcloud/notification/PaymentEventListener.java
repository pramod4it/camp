package com.rajcloud.notification;

import com.rajcloud.events.PaymentProcessedEvent;
import com.rajcloud.observability.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {
    private final NotificationRepository repository;

    public PaymentEventListener(NotificationRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = "notification-service")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        String message = "Order " + event.orderId() + " payment status: " + event.status();
        repository.save(new Notification(event.orderId(), event.userId(), message));
    }
}
