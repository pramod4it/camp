package com.rajcloud.observability;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaCorrelationTest {
    @AfterEach
    void clear() {
        CorrelationId.clear();
        MDC.clear();
    }

    @Test
    void recordInterceptorCopiesHeadersIntoMdc() {
        RecordHeaders headers = new RecordHeaders();
        headers.add(CorrelationConstants.CORRELATION_ID_HEADER, "corr-kafka".getBytes(StandardCharsets.UTF_8));
        headers.add(CorrelationConstants.EVENT_TYPE, "OrderCreatedEvent".getBytes(StandardCharsets.UTF_8));
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic-a", 0, 0, "key", "payload");
        headers.forEach(header -> record.headers().add(header));

        KafkaCorrelation.recordInterceptor().intercept(record, null);

        assertThat(MDC.get(CorrelationConstants.CORRELATION_ID)).isEqualTo("corr-kafka");
        assertThat(MDC.get(CorrelationConstants.TOPIC)).isEqualTo("topic-a");
        assertThat(MDC.get(CorrelationConstants.EVENT_TYPE)).isEqualTo("OrderCreatedEvent");
    }

    @Test
    void recordInterceptorCreatesCorrelationIdWhenMissing() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic-a", 0, 0, "key", "payload");

        KafkaCorrelation.recordInterceptor().intercept(record, null);

        assertThat(MDC.get(CorrelationConstants.CORRELATION_ID)).isNotBlank();
    }

    @Test
    void recordInterceptorCreatesCorrelationIdWhenHeaderIsBlank() {
        ConsumerRecord<String, Object> record = new ConsumerRecord<>("topic-a", 0, 0, "key", "payload");
        record.headers().add(CorrelationConstants.CORRELATION_ID_HEADER, " ".getBytes(StandardCharsets.UTF_8));

        KafkaCorrelation.recordInterceptor().intercept(record, null);

        assertThat(MDC.get(CorrelationConstants.CORRELATION_ID)).isNotBlank();
        assertThat(MDC.get(CorrelationConstants.CORRELATION_ID)).isNotEqualTo(" ");
    }
}
