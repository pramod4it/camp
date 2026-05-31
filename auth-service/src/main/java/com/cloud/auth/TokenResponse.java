package com.cloud.auth;

public record TokenResponse(String tokenType, String accessToken, String refreshToken, long expiresIn,
                            String scope) {
}
