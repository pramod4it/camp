package com.cloud.auth;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class RefreshTokenStore {
    private final Map<String, RefreshTokenRecord> tokens = new ConcurrentHashMap<>();

    String create(String subject) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, new RefreshTokenRecord(subject, Instant.now().plusSeconds(86400)));
        return token;
    }

    Optional<String> consume(String token) {
        RefreshTokenRecord record = tokens.remove(token);
        if (record == null || !record.expiresAt().isAfter(Instant.now())) {
            return Optional.empty();
        }
        return Optional.of(record.subject());
    }

    record RefreshTokenRecord(String subject, Instant expiresAt) {
    }
}
