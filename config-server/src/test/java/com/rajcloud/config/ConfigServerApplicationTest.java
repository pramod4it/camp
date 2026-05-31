package com.rajcloud.config;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class ConfigServerApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ConfigServerApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ConfigServerApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new ConfigServerApplication()).isNotNull();
    }
}
