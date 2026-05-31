package com.rajcloud.gateway;

import com.rajcloud.security.JwtTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GatewaySecurityConfig {
    @Bean
    JwtTokenService jwtTokenService(@Value("${camp.security.jwt-secret}") String secret) {
        return new JwtTokenService(secret);
    }
}
