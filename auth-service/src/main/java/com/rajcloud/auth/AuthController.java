package com.rajcloud.auth;

import com.rajcloud.api.ApiResource;
import com.rajcloud.security.AuthConstants;
import com.rajcloud.security.JwtClaims;
import com.rajcloud.security.JwtTokenService;
import com.rajcloud.security.JwtValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping(ApiResource.AUTH)
class AuthController {
    private static final long ACCESS_TOKEN_SECONDS = 900L;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenStore refreshTokenStore;

    AuthController(JwtTokenService jwtTokenService, RefreshTokenStore refreshTokenStore) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenStore = refreshTokenStore;
    }

    @PostMapping("/token")
    TokenResponse token(@RequestBody TokenRequest request) {
        return switch (request.grantType()) {
            case "password" -> passwordToken(request);
            case "refresh_token" -> refreshToken(request);
            case "client_credentials" -> clientCredentialsToken(request);
            default -> throw new JwtValidationException("Unsupported grant type");
        };
    }

    @PostMapping("/validate")
    TokenValidationResponse validate(@RequestHeader(AuthConstants.AUTHORIZATION) String authorization) {
        JwtClaims claims = jwtTokenService.validate(jwtTokenService.extractBearerToken(authorization));
        return new TokenValidationResponse(true, claims.subject(), claims.roles(), claims.tokenType(), claims.expiresAt());
    }

    private TokenResponse passwordToken(TokenRequest request) {
        if (!validUser(request.username(), request.password())) {
            throw new JwtValidationException("Invalid user credentials");
        }
        List<String> roles = rolesFor(request.username());
        return response(request.username(), roles, AuthConstants.ACCESS, refreshTokenStore.create(request.username()));
    }

    private TokenResponse refreshToken(TokenRequest request) {
        String subject = refreshTokenStore.consume(request.refreshToken())
                .orElseThrow(() -> new JwtValidationException("Invalid refresh token"));
        return response(subject, rolesFor(subject), AuthConstants.ACCESS, refreshTokenStore.create(subject));
    }

    private TokenResponse clientCredentialsToken(TokenRequest request) {
        if (!"camp-service".equals(request.clientId()) || !"camp-service-secret".equals(request.clientSecret())) {
            throw new JwtValidationException("Invalid service credentials");
        }
        return response(request.clientId(), List.of(AuthConstants.ROLE_SERVICE), AuthConstants.SERVICE, null);
    }

    private TokenResponse response(String subject, List<String> roles, String tokenType, String refreshToken) {
        String accessToken = jwtTokenService.issue(subject, roles, tokenType, Duration.ofSeconds(ACCESS_TOKEN_SECONDS));
        return new TokenResponse("Bearer", accessToken, refreshToken, ACCESS_TOKEN_SECONDS, String.join(" ", roles));
    }

    private boolean validUser(String username, String password) {
        return ("admin".equals(username) && "admin123".equals(password))
                || ("user".equals(username) && "user123".equals(password));
    }

    private List<String> rolesFor(String username) {
        if ("admin".equals(username)) {
            return List.of(AuthConstants.ROLE_ADMIN, AuthConstants.ROLE_USER);
        }
        return List.of(AuthConstants.ROLE_USER);
    }

    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    String unauthorized(JwtValidationException ex) {
        return ex.getMessage();
    }
}
