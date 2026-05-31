package com.cloud.order;

import com.cloud.security.JwtTokenService;
import com.cloud.security.ServiceTokenProvider;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ServiceAuthenticationConfig {
    @Bean
    JwtTokenService jwtTokenService(@Value("${camp.security.jwt-secret:camp-development-secret-key-32-chars}") String secret) {
        return new JwtTokenService(secret);
    }

    @Bean
    ServiceTokenProvider serviceTokenProvider(JwtTokenService jwtTokenService) {
        return new ServiceTokenProvider(jwtTokenService, "order-service");
    }

    @Bean
    RequestInterceptor serviceAuthenticationInterceptor(ServiceTokenProvider tokenProvider) {
        return template -> template.header("Authorization", tokenProvider.authorizationHeader());
    }
}
