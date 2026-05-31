package com.rajcloud.observability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdTest {
    @AfterEach
    void clear() {
        CorrelationId.clear();
        MDC.clear();
    }

    @Test
    void currentOrNewReturnsExistingMdcValue() {
        CorrelationId.set("corr-123");

        assertThat(CorrelationId.currentOrNew()).isEqualTo("corr-123");
    }

    @Test
    void currentOrNewCreatesValueWhenMissing() {
        assertThat(CorrelationId.currentOrNew()).isNotBlank();
    }

    @Test
    void currentOrNewCreatesValueWhenExistingValueIsBlank() {
        CorrelationId.set(" ");

        assertThat(CorrelationId.currentOrNew()).isNotBlank();
        assertThat(CorrelationId.currentOrNew()).isNotEqualTo(" ");
    }

    @Test
    void clearRemovesKnownMdcKeys() {
        CorrelationId.set("corr");
        MDC.put(CorrelationConstants.EVENT_TYPE, "event");
        MDC.put(CorrelationConstants.TOPIC, "topic");
        MDC.put(CorrelationConstants.ORDER_ID, "1");
        MDC.put(CorrelationConstants.USER_ID, "2");

        CorrelationId.clear();

        assertThat(MDC.get(CorrelationConstants.CORRELATION_ID)).isNull();
        assertThat(MDC.get(CorrelationConstants.EVENT_TYPE)).isNull();
        assertThat(MDC.get(CorrelationConstants.TOPIC)).isNull();
        assertThat(MDC.get(CorrelationConstants.ORDER_ID)).isNull();
        assertThat(MDC.get(CorrelationConstants.USER_ID)).isNull();
    }
}
