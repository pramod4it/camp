package com.cloud.auth;

import com.cloud.security.JwtTokenService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AuthConfiguration {
    @Bean
    JwtTokenService jwtTokenService(@Value("${camp.security.jwt-secret}") String secret) {
        return new JwtTokenService(secret);
    }

    @Bean
    RefreshTokenStore refreshTokenStore() {
        return new RefreshTokenStore();
    }

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Auth Service API").version("v1"));
    }
}
