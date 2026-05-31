package com.cloud.user;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class UserServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            UserServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(UserServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new UserServiceApplication()).isNotNull();
    }
}
