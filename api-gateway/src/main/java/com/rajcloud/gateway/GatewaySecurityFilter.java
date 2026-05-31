package com.rajcloud.gateway;

import com.rajcloud.security.AuthConstants;
import com.rajcloud.security.JwtClaims;
import com.rajcloud.security.JwtTokenService;
import com.rajcloud.security.JwtValidationException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
class GatewaySecurityFilter implements GlobalFilter, Ordered {
    private final JwtTokenService jwtTokenService;

    GatewaySecurityFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (publicPath(path)) {
            return chain.filter(exchange);
        }
        try {
            JwtClaims claims = jwtTokenService.validate(jwtTokenService.extractBearerToken(
                    exchange.getRequest().getHeaders().getFirst(AuthConstants.AUTHORIZATION)));
            if (!authorized(path, claims)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            ServerWebExchange securedExchange = exchange.mutate().request(builder -> builder
                    .header("X-Authenticated-Subject", claims.subject())
                    .header("X-Authenticated-Roles", String.join(",", claims.roles()))).build();
            return chain.filter(securedExchange);
        } catch (JwtValidationException ex) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }

    boolean publicPath(String path) {
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/actuator/")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars/")
                || path.startsWith("/v3/api-docs");
    }

    boolean authorized(String path, JwtClaims claims) {
        if (claims.hasRole(AuthConstants.ROLE_SERVICE)) {
            return true;
        }
        List<String> adminPrefixes = List.of("/api/v1/inventory", "/api/v1/payments", "/api/v1/notifications");
        if (adminPrefixes.stream().anyMatch(path::startsWith)) {
            return claims.hasRole(AuthConstants.ROLE_ADMIN);
        }
        return claims.hasRole(AuthConstants.ROLE_USER) || claims.hasRole(AuthConstants.ROLE_ADMIN);
    }
}
