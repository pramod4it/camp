package com.rajcloud.order;

import com.rajcloud.events.InventoryRejectedEvent;
import com.rajcloud.events.InventoryReservedEvent;
import com.rajcloud.events.PaymentProcessedEvent;
import com.rajcloud.observability.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PaymentEventListener {
    private final OrderRepository repository;
    private final OutboxPublisher outboxPublisher;

    public PaymentEventListener(OrderRepository repository, OutboxPublisher outboxPublisher) {
        this.repository = repository;
        this.outboxPublisher = outboxPublisher;
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "order-service")
    public void onInventoryReserved(InventoryReservedEvent event) {
        CustomerOrder order = repository.findById(event.orderId()).orElseThrow();
        order.markInventoryReserved();
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.INVENTORY_REJECTED, groupId = "order-service")
    public void onInventoryRejected(InventoryRejectedEvent event) {
        CustomerOrder order = repository.findById(event.orderId()).orElseThrow();
        order.markInventoryRejected();
        order.cancel();
    }

    @Transactional
    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = "order-service")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        CustomerOrder order = repository.findById(event.orderId()).orElseThrow();
        if ("APPROVED".equals(event.status())) {
            order.markPaid();
        } else {
            order.markPaymentFailed();
            outboxPublisher.save(KafkaTopics.INVENTORY_RELEASE_REQUESTED, order.getId().toString(),
                    new com.rajcloud.events.InventoryReleaseRequestedEvent(order.getId(), order.getProductId(),
                            order.getQuantity(), "Payment failed", java.time.Instant.now()));
        }
    }
}
