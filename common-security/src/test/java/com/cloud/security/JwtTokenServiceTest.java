package com.cloud.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.crypto.Mac;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.lang.reflect.Constructor;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class JwtTokenServiceTest {
    private static final String SECRET = "camp-development-secret-key-32-chars";
    private final JwtTokenService service = new JwtTokenService(SECRET,
            Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC), new ObjectMapper());

    @Test
    void issuesAndValidatesJwt() {
        String token = service.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS, Duration.ofMinutes(5));

        JwtClaims claims = service.validate(token);

        assertThat(claims.subject()).isEqualTo("user");
        assertThat(claims.roles()).containsExactly(AuthConstants.ROLE_USER);
        assertThat(claims.tokenType()).isEqualTo(AuthConstants.ACCESS);
        assertThat(claims.issuedAt()).isEqualTo(Instant.parse("2026-05-31T00:00:00Z"));
        assertThat(claims.expiresAt()).isEqualTo(Instant.parse("2026-05-31T00:05:00Z"));
        assertThat(claims.hasRole(AuthConstants.ROLE_USER)).isTrue();
        assertThat(claims.hasRole(AuthConstants.ROLE_ADMIN)).isFalse();
        assertThat(claims.expired(Instant.parse("2026-05-31T00:06:00Z"))).isTrue();
        assertThat(new JwtClaims("nobody", null, AuthConstants.ACCESS, Instant.now(), Instant.now().plusSeconds(1))
                .hasRole(AuthConstants.ROLE_USER)).isFalse();
    }

    @Test
    void extractsBearerToken() {
        JwtTokenService defaultService = new JwtTokenService(SECRET);

        assertThat(defaultService.extractBearerToken("Bearer abc")).isEqualTo("abc");
    }

    @Test
    void rejectsInvalidInputs() {
        assertThatThrownBy(() -> new JwtTokenService("short")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JwtTokenService(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.extractBearerToken(null)).isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> service.extractBearerToken("Basic abc")).isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> service.validate("bad")).isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> service.validate(null)).isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> service.validate("a.b.c")).isInstanceOf(JwtValidationException.class);
    }

    @Test
    void rejectsTamperedAndExpiredTokens() {
        String token = service.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS, Duration.ofMinutes(5));
        assertThatThrownBy(() -> service.validate(token.substring(0, token.length() - 1) + "x"))
                .isInstanceOf(JwtValidationException.class);

        JwtTokenService future = new JwtTokenService(SECRET,
                Clock.fixed(Instant.parse("2026-05-31T00:10:00Z"), ZoneOffset.UTC), new ObjectMapper());
        assertThatThrownBy(() -> future.validate(token)).isInstanceOf(JwtValidationException.class);
    }

    @Test
    void rejectsPayloadsWithMissingOrInvalidClaims() throws Exception {
        Method string = JwtTokenService.class.getDeclaredMethod("string", java.util.Map.class, String.class);
        string.setAccessible(true);
        Method number = JwtTokenService.class.getDeclaredMethod("number", java.util.Map.class, String.class);
        number.setAccessible(true);
        Method readPayload = JwtTokenService.class.getDeclaredMethod("readPayload", String.class);
        readPayload.setAccessible(true);

        assertThatThrownBy(() -> string.invoke(service, java.util.Map.of("sub", ""), "sub"))
                .hasCauseInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> string.invoke(service, java.util.Map.of(), "sub"))
                .hasCauseInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> number.invoke(service, java.util.Map.of("iat", "bad"), "iat"))
                .hasCauseInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> readPayload.invoke(service, "not-base64"))
                .hasCauseInstanceOf(JwtValidationException.class);
    }

    @Test
    void wrapsJsonAndSigningFailures() throws Exception {
        ObjectMapper objectMapper = mock(ObjectMapper.class);
        when(objectMapper.writeValueAsBytes(org.mockito.ArgumentMatchers.any())).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("bad") {
        });
        JwtTokenService brokenJson = new JwtTokenService(SECRET,
                Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC), objectMapper);

        assertThatThrownBy(() -> brokenJson.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS,
                Duration.ofMinutes(5))).isInstanceOf(IllegalStateException.class);

        try (MockedStatic<Mac> mac = mockStatic(Mac.class)) {
            mac.when(() -> Mac.getInstance("HmacSHA256")).thenThrow(new NoSuchAlgorithmException("missing"));
            JwtTokenService brokenSigner = new JwtTokenService(SECRET,
                    Clock.fixed(Instant.parse("2026-05-31T00:00:00Z"), ZoneOffset.UTC), new ObjectMapper());

            assertThatThrownBy(() -> brokenSigner.issue("user", List.of(AuthConstants.ROLE_USER), AuthConstants.ACCESS,
                    Duration.ofMinutes(5))).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void serviceTokenProviderCreatesBearerHeader() {
        ServiceTokenProvider provider = new ServiceTokenProvider(service, "order-service");

        String header = provider.authorizationHeader();

        assertThat(header).startsWith(AuthConstants.BEARER);
        assertThat(service.validate(service.extractBearerToken(header)).hasRole(AuthConstants.ROLE_SERVICE)).isTrue();
    }

    @Test
    void privateConstantsConstructorIsCovered() throws Exception {
        Constructor<AuthConstants> constructor = AuthConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance()).isNotNull();
    }
}
