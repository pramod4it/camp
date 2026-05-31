package com.cloud.observability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelatedKafkaTest {
    @AfterEach
    void clear() {
        CorrelationId.clear();
    }

    @Test
    void messageAddsTopicKeyCorrelationAndEventTypeHeaders() {
        CorrelationId.set("corr-1");
        Object event = new TestEvent("value");

        var message = CorrelatedKafka.message("topic-a", "key-a", event);

        assertThat(message.getPayload()).isSameAs(event);
        assertThat(message.getHeaders().get(KafkaHeaders.TOPIC)).isEqualTo("topic-a");
        assertThat(message.getHeaders().get(KafkaHeaders.KEY)).isEqualTo("key-a");
        assertThat(message.getHeaders().get(CorrelationConstants.CORRELATION_ID_HEADER)).isEqualTo("corr-1");
        assertThat(message.getHeaders().get(CorrelationConstants.EVENT_TYPE)).isEqualTo("TestEvent");
    }

    record TestEvent(String value) {
    }
}
