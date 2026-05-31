package com.cloud.gateway;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class ApiGatewayApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ApiGatewayApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(ApiGatewayApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new ApiGatewayApplication()).isNotNull();
    }
}
