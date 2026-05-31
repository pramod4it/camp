package com.rajcloud.discovery;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class DiscoveryServerApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            DiscoveryServerApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(DiscoveryServerApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new DiscoveryServerApplication()).isNotNull();
    }
}
