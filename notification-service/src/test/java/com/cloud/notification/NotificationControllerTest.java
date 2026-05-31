package com.cloud.notification;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationControllerTest {
    @Test
    void findAllReturnsNotifications() {
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.findAll()).thenReturn(List.of(new Notification(10L, 1L, "ok")));

        assertThat(new NotificationController(repository).findAll()).hasSize(1);
    }
}
