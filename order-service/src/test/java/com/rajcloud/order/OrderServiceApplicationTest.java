package com.rajcloud.order;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class OrderServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            OrderServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(OrderServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new OrderServiceApplication()).isNotNull();
    }
}
