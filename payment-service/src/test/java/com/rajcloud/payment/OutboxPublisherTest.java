package com.rajcloud.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxPublisherTest {
    @Test
    void savePersistsOutboxEventAndPublishesKafkaMessage() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        KafkaTemplate<String, Object> kafkaTemplate = mock(KafkaTemplate.class);
        OutboxPublisher publisher = new OutboxPublisher(repository, kafkaTemplate, new ObjectMapper());

        publisher.save("topic", "key", new TestEvent("value"));

        verify(repository).save(org.mockito.ArgumentMatchers.any(OutboxEvent.class));
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.<Message<?>>any());
    }

    @Test
    void markPendingAsPublishedMarksAllPendingRows() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        OutboxEvent event = new OutboxEvent("topic", "key", "type", "{}");
        when(repository.findTop20ByPublishedFalseOrderByCreatedAtAsc()).thenReturn(List.of(event));
        OutboxPublisher publisher = new OutboxPublisher(repository, mock(KafkaTemplate.class), new ObjectMapper());

        publisher.markPendingAsPublished();

        verify(repository).findTop20ByPublishedFalseOrderByCreatedAtAsc();
    }

    @Test
    void saveThrowsIllegalStateWhenEventCannotBeSerialized() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsString(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new JsonProcessingException("bad json") {
                });
        OutboxPublisher publisher = new OutboxPublisher(mock(OutboxEventRepository.class),
                mock(KafkaTemplate.class), objectMapper);

        assertThatThrownBy(() -> publisher.save("topic", "key", new TestEvent("value")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Could not serialize outbox event");
    }

    record TestEvent(String value) {
    }
}
