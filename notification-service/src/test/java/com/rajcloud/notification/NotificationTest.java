package com.rajcloud.notification;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {
    @Test
    void constructorSetsFields() {
        Notification notification = new Notification(10L, 1L, "message");

        assertThat(notification.getOrderId()).isEqualTo(10L);
        assertThat(notification.getUserId()).isEqualTo(1L);
        assertThat(notification.getMessage()).isEqualTo("message");
        assertThat(notification.getCreatedAt()).isNotNull();
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<Notification> constructor = Notification.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getId()).isNull();
    }
}
