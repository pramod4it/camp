package com.cloud.notification;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class NotificationServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            NotificationServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(NotificationServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new NotificationServiceApplication()).isNotNull();
    }
}
