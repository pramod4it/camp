package com.cloud.search;

import com.cloud.events.OrderCreatedEvent;
import com.cloud.events.PaymentProcessedEvent;
import com.cloud.observability.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchEventListener {
    private final OrderSearchRepository repository;

    public SearchEventListener(OrderSearchRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "search-service-orders")
    public void onOrderCreated(OrderCreatedEvent event) {
        repository.save(new OrderDocument(event.orderId(), event.userId(), event.amount(), "PENDING", event.createdAt()));
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = "search-service-payments")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        repository.save(new OrderDocument(event.orderId(), event.userId(), event.amount(), event.status(), event.processedAt()));
    }
}
