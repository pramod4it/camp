package com.cloud.payment;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {
    @Test
    void markPublishedCanBeCalled() {
        OutboxEvent event = new OutboxEvent("topic", "key", "type", "{}");

        event.markPublished();
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<OutboxEvent> constructor = OutboxEvent.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance()).isNotNull();
    }
}
