package com.cloud.security;

import java.time.Instant;
import java.util.List;

public record JwtClaims(String subject, List<String> roles, String tokenType, Instant issuedAt, Instant expiresAt) {
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean expired(Instant now) {
        return !expiresAt.isAfter(now);
    }
}
