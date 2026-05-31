package com.cloud.inventory;

import com.cloud.events.InventoryRejectedEvent;
import com.cloud.events.InventoryReleaseRequestedEvent;
import com.cloud.events.InventoryReservedEvent;
import com.cloud.events.OrderCreatedEvent;
import com.cloud.observability.CorrelatedKafka;
import com.cloud.observability.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class OrderEventListener {
    private final InventoryRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventListener(InventoryRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.ORDER_CREATED, groupId = "inventory-service")
    public void onOrderCreated(OrderCreatedEvent event) {
        InventoryItem item = repository.findById(event.productId()).orElse(null);
        if (item != null && item.reserve(event.quantity())) {
            kafkaTemplate.send(CorrelatedKafka.message(KafkaTopics.INVENTORY_RESERVED, event.orderId().toString(),
                    new InventoryReservedEvent(event.orderId(), event.userId(), event.productId(),
                            event.quantity(), event.amount(), Instant.now())));
            return;
        }

        String reason = item == null ? "Product not found" : "Insufficient stock";
        kafkaTemplate.send(CorrelatedKafka.message(KafkaTopics.INVENTORY_REJECTED, event.orderId().toString(),
                new InventoryRejectedEvent(event.orderId(), event.userId(), event.productId(),
                        event.quantity(), reason, Instant.now())));
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.INVENTORY_RELEASE_REQUESTED, groupId = "inventory-service")
    public void onInventoryReleaseRequested(InventoryReleaseRequestedEvent event) {
        repository.findById(event.productId()).ifPresent(item -> item.release(event.quantity()));
    }
}
