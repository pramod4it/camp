package com.rajcloud.gateway;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationTest {
    @Test
    void openApiConfigCreatesGatewayApiMetadata() {
        assertThat(new OpenApiConfig().openAPI().getInfo().getTitle()).isEqualTo("Camp API Gateway");
    }

    @Test
    void securityConfigCreatesJwtTokenService() {
        assertThat(new GatewaySecurityConfig().jwtTokenService("camp-development-secret-key-32-chars")).isNotNull();
    }
}
