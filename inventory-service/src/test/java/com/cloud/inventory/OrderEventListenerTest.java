package com.cloud.inventory;

import com.cloud.events.InventoryReleaseRequestedEvent;
import com.cloud.events.OrderCreatedEvent;
import com.cloud.observability.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderEventListenerTest {
    private final InventoryRepository repository = mock(InventoryRepository.class);
    private final KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
    private final OrderEventListener listener = new OrderEventListener(repository, kafkaTemplate);

    @Test
    void reservesInventoryAndPublishesReservedEvent() {
        InventoryItem item = new InventoryItem(1001L, "Seat", 5);
        when(repository.findById(1001L)).thenReturn(Optional.of(item));

        listener.onOrderCreated(new OrderCreatedEvent(10L, 1L, 1001L, 2, BigDecimal.TEN, Instant.now()));

        assertThat(item.getAvailableQuantity()).isEqualTo(3);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.<Message<?>>argThat(message ->
                KafkaTopics.INVENTORY_RESERVED.equals(message.getHeaders().get("kafka_topic"))));
    }

    @Test
    void publishesRejectedWhenProductMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        listener.onOrderCreated(new OrderCreatedEvent(10L, 1L, 999L, 2, BigDecimal.TEN, Instant.now()));

        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.<Message<?>>argThat(message ->
                KafkaTopics.INVENTORY_REJECTED.equals(message.getHeaders().get("kafka_topic"))));
    }

    @Test
    void publishesRejectedWhenStockInsufficient() {
        when(repository.findById(1001L)).thenReturn(Optional.of(new InventoryItem(1001L, "Seat", 1)));

        listener.onOrderCreated(new OrderCreatedEvent(10L, 1L, 1001L, 2, BigDecimal.TEN, Instant.now()));

        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.<Message<?>>argThat(message ->
                KafkaTopics.INVENTORY_REJECTED.equals(message.getHeaders().get("kafka_topic"))));
    }

    @Test
    void releaseEventAddsStockWhenItemExists() {
        InventoryItem item = new InventoryItem(1001L, "Seat", 1);
        when(repository.findById(1001L)).thenReturn(Optional.of(item));

        listener.onInventoryReleaseRequested(new InventoryReleaseRequestedEvent(10L, 1001L, 3, "Payment failed", Instant.now()));

        assertThat(item.getAvailableQuantity()).isEqualTo(4);
    }
}
