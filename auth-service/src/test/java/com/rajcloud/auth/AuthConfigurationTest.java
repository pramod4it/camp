package com.rajcloud.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthConfigurationTest {
    private final AuthConfiguration configuration = new AuthConfiguration();

    @Test
    void createsBeans() {
        assertThat(configuration.jwtTokenService("camp-development-secret-key-32-chars")).isNotNull();
        assertThat(configuration.refreshTokenStore()).isNotNull();
        assertThat(configuration.openAPI().getInfo().getTitle()).isEqualTo("Auth Service API");
    }
}
