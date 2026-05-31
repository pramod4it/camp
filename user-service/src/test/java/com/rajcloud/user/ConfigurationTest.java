package com.rajcloud.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationTest {
    @Test
    void openApiConfigCreatesUserApiMetadata() {
        assertThat(new OpenApiConfig().openAPI().getInfo().getTitle()).isEqualTo("User Service API");
    }
}
