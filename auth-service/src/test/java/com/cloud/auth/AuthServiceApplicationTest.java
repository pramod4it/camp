package com.cloud.auth;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class AuthServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            AuthServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(AuthServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new AuthServiceApplication()).isNotNull();
    }
}
