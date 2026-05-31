package com.cloud.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cloud.observability.CorrelatedKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutboxEventRepository repository, KafkaTemplate<String, Object> kafkaTemplate,
                           ObjectMapper objectMapper) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void save(String topic, String key, Object event) {
        repository.save(new OutboxEvent(topic, key, event.getClass().getName(), toJson(event)));
        kafkaTemplate.send(CorrelatedKafka.message(topic, key, event));
    }

    @Scheduled(fixedDelay = 10000)
    @Transactional
    public void markPendingAsPublished() {
        repository.findTop20ByPublishedFalseOrderByCreatedAtAsc().forEach(OutboxEvent::markPublished);
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize outbox event", ex);
        }
    }
}
