package com.rajcloud.order;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {
    @Test
    void constructorAndMarkPublishedWork() {
        OutboxEvent event = new OutboxEvent("topic", "key", "type", "{}");

        assertThat(event.getTopic()).isEqualTo("topic");
        assertThat(event.getEventKey()).isEqualTo("key");

        event.markPublished();
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<OutboxEvent> constructor = OutboxEvent.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getTopic()).isNull();
    }
}
