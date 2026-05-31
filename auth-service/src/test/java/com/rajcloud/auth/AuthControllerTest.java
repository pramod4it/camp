package com.rajcloud.auth;

import com.rajcloud.security.AuthConstants;
import com.rajcloud.security.JwtTokenService;
import com.rajcloud.security.JwtValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthControllerTest {
    private final JwtTokenService jwtTokenService = new JwtTokenService("camp-development-secret-key-32-chars");
    private final RefreshTokenStore refreshTokenStore = new RefreshTokenStore();
    private final AuthController controller = new AuthController(jwtTokenService, refreshTokenStore);

    @Test
    void passwordGrantIssuesAdminTokenAndRefreshToken() {
        TokenResponse response = controller.token(new TokenRequest("password", "admin", "admin123", null, null, null));

        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.expiresIn()).isEqualTo(900L);
        assertThat(response.scope()).contains(AuthConstants.ROLE_ADMIN, AuthConstants.ROLE_USER);
    }

    @Test
    void passwordGrantIssuesUserToken() {
        TokenResponse response = controller.token(new TokenRequest("password", "user", "user123", null, null, null));

        assertThat(response.scope()).isEqualTo(AuthConstants.ROLE_USER);
    }

    @Test
    void refreshGrantRotatesRefreshToken() {
        TokenResponse first = controller.token(new TokenRequest("password", "user", "user123", null, null, null));

        TokenResponse refreshed = controller.token(new TokenRequest("refresh_token", null, null, first.refreshToken(), null, null));

        assertThat(refreshed.accessToken()).isNotBlank();
        assertThat(refreshed.refreshToken()).isNotEqualTo(first.refreshToken());
    }

    @Test
    void clientCredentialsGrantIssuesServiceToken() {
        TokenResponse response = controller.token(new TokenRequest("client_credentials", null, null, null,
                "camp-service", "camp-service-secret"));

        assertThat(response.refreshToken()).isNull();
        assertThat(response.scope()).isEqualTo(AuthConstants.ROLE_SERVICE);
    }

    @Test
    void validateReturnsClaims() {
        TokenResponse token = controller.token(new TokenRequest("password", "admin", "admin123", null, null, null));

        TokenValidationResponse response = controller.validate(AuthConstants.BEARER + token.accessToken());

        assertThat(response.active()).isTrue();
        assertThat(response.subject()).isEqualTo("admin");
        assertThat(response.roles()).contains(AuthConstants.ROLE_ADMIN);
        assertThat(response.tokenType()).isEqualTo(AuthConstants.ACCESS);
        assertThat(response.expiresAt()).isNotNull();
    }

    @Test
    void invalidGrantsThrowUnauthorizedExceptions() {
        assertThatThrownBy(() -> controller.token(new TokenRequest("password", "user", "bad", null, null, null)))
                .isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> controller.token(new TokenRequest("refresh_token", null, null, "bad", null, null)))
                .isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> controller.token(new TokenRequest("client_credentials", null, null, null, "bad", "bad")))
                .isInstanceOf(JwtValidationException.class);
        assertThatThrownBy(() -> controller.token(new TokenRequest("unknown", null, null, null, null, null)))
                .isInstanceOf(JwtValidationException.class);
    }

    @Test
    void exceptionHandlerReturnsMessage() {
        assertThat(controller.unauthorized(new JwtValidationException("nope"))).isEqualTo("nope");
    }
}
