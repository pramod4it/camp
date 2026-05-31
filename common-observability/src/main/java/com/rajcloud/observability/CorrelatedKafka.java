package com.rajcloud.observability;

import org.slf4j.MDC;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public final class CorrelatedKafka {
    private CorrelatedKafka() {
    }

    public static Message<Object> message(String topic, String key, Object event) {
        String correlationId = CorrelationId.currentOrNew();
        MDC.put(CorrelationConstants.TOPIC, topic);
        MDC.put(CorrelationConstants.EVENT_TYPE, event.getClass().getSimpleName());
        return MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key)
                .setHeader(CorrelationConstants.CORRELATION_ID_HEADER, correlationId)
                .setHeader(CorrelationConstants.EVENT_TYPE, event.getClass().getSimpleName())
                .build();
    }
}
