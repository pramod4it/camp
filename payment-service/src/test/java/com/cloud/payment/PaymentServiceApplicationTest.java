package com.cloud.payment;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class PaymentServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            PaymentServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(PaymentServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new PaymentServiceApplication()).isNotNull();
    }
}
