package com.cloud.observability;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTopicsTest {
    @Test
    void deadLetterAppendsDltSuffix() {
        assertThat(KafkaTopics.deadLetter(KafkaTopics.ORDER_CREATED)).isEqualTo("order-created.DLT");
    }
}
