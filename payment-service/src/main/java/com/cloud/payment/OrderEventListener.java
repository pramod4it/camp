package com.cloud.payment;

import com.cloud.events.InventoryReservedEvent;
import com.cloud.events.PaymentProcessedEvent;
import com.cloud.observability.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderEventListener {
    private static final BigDecimal APPROVAL_LIMIT = new BigDecimal("5000.00");

    private final PaymentRepository repository;
    private final OutboxPublisher outboxPublisher;

    public OrderEventListener(PaymentRepository repository, OutboxPublisher outboxPublisher) {
        this.repository = repository;
        this.outboxPublisher = outboxPublisher;
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "payment-service")
    public void onInventoryReserved(InventoryReservedEvent event) {
        PaymentStatus status = event.amount().compareTo(APPROVAL_LIMIT) <= 0
                ? PaymentStatus.APPROVED
                : PaymentStatus.DECLINED;
        Payment payment = repository.save(new Payment(event.orderId(), event.userId(), event.amount(), status));
        outboxPublisher.save(KafkaTopics.PAYMENT_PROCESSED, event.orderId().toString(),
                new PaymentProcessedEvent(payment.getId(), payment.getOrderId(), payment.getUserId(),
                        payment.getAmount(), payment.getStatus().name(), payment.getProcessedAt()));
    }
}
