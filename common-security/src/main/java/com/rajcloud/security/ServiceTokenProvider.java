package com.rajcloud.security;

import java.time.Duration;
import java.util.List;

public class ServiceTokenProvider {
    private final JwtTokenService jwtTokenService;
    private final String serviceName;

    public ServiceTokenProvider(JwtTokenService jwtTokenService, String serviceName) {
        this.jwtTokenService = jwtTokenService;
        this.serviceName = serviceName;
    }

    public String authorizationHeader() {
        String token = jwtTokenService.issue(serviceName, List.of(AuthConstants.ROLE_SERVICE), AuthConstants.SERVICE,
                Duration.ofMinutes(10));
        return AuthConstants.BEARER + token;
    }
}
