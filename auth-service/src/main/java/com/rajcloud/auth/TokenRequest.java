package com.rajcloud.auth;

public record TokenRequest(String grantType, String username, String password, String refreshToken, String clientId,
                           String clientSecret) {
}
