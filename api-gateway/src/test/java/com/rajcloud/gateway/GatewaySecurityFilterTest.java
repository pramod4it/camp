package com.rajcloud.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rajcloud.security.AuthConstants;
import com.rajcloud.security.JwtClaims;
import com.rajcloud.security.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class GatewaySecurityFilterTest {
    private final JwtTokenService jwt = new JwtTokenService("camp-development-secret-key-32-chars",
            Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC), new ObjectMapper());
    private final GatewaySecurityFilter filter = new GatewaySecurityFilter(jwt);

    @Test
    void publicPathsBypassAuthentication() {
        AtomicBoolean called = new AtomicBoolean(false);
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/v1/auth/token"));

        filter.filter(exchange, secured -> {
            called.set(true);
            return Mono.empty();
        }).block();

        assertThat(called).isTrue();
        assertThat(filter.publicPath("/actuator/health")).isTrue();
        assertThat(filter.publicPath("/swagger-ui.html")).isTrue();
        assertThat(filter.publicPath("/webjars/test.js")).isTrue();
        assertThat(filter.publicPath("/v3/api-docs")).isTrue();
        assertThat(filter.publicPath("/api/v1/orders")).isFalse();
    }

    @Test
    void validUserTokenCanAccessUserRoutesAndAddsIdentityHeaders() {
        String token = jwt.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS, Duration.ofMinutes(5));
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/orders")
                .header(AuthConstants.AUTHORIZATION, AuthConstants.BEARER + token));
        AtomicBoolean called = new AtomicBoolean(false);

        filter.filter(exchange, secured -> {
            called.set(true);
            assertThat(secured.getRequest().getHeaders().getFirst("X-Authenticated-Subject")).isEqualTo("user");
            assertThat(secured.getRequest().getHeaders().getFirst("X-Authenticated-Roles")).isEqualTo(AuthConstants.ROLE_USER);
            return Mono.empty();
        }).block();

        assertThat(called).isTrue();
    }

    @Test
    void adminAndServiceTokensCanAccessAdminRoutes() {
        var admin = new JwtClaims("admin", List.of(AuthConstants.ROLE_ADMIN), AuthConstants.ACCESS,
                Instant.now(), Instant.now().plusSeconds(60));
        var service = new JwtClaims("service", List.of(AuthConstants.ROLE_SERVICE), AuthConstants.SERVICE,
                Instant.now(), Instant.now().plusSeconds(60));

        assertThat(filter.authorized("/api/v1/payments", admin)).isTrue();
        assertThat(filter.authorized("/api/v1/inventory", service)).isTrue();
        assertThat(filter.authorized("/api/v1/orders", admin)).isTrue();
        assertThat(filter.authorized("/api/v1/orders", new JwtClaims("guest", List.of(), AuthConstants.ACCESS,
                Instant.now(), Instant.now().plusSeconds(60)))).isFalse();
    }

    @Test
    void rejectsMissingInvalidAndInsufficientTokens() {
        var missing = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/orders"));
        filter.filter(missing, secured -> Mono.empty()).block();
        assertThat(missing.getResponse().getStatusCode().value()).isEqualTo(401);

        var invalid = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/orders")
                .header(AuthConstants.AUTHORIZATION, AuthConstants.BEARER + "bad"));
        filter.filter(invalid, secured -> Mono.empty()).block();
        assertThat(invalid.getResponse().getStatusCode().value()).isEqualTo(401);

        String userToken = jwt.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS, Duration.ofMinutes(5));
        var forbidden = MockServerWebExchange.from(MockServerHttpRequest.get("/api/v1/payments")
                .header(AuthConstants.AUTHORIZATION, AuthConstants.BEARER + userToken));
        filter.filter(forbidden, secured -> Mono.empty()).block();
        assertThat(forbidden.getResponse().getStatusCode().value()).isEqualTo(403);
    }

    @Test
    void orderIsHighPrecedence() {
        assertThat(filter.getOrder()).isLessThan(0);
    }
}
