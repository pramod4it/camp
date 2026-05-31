package com.rajcloud.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JwtTokenService {
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final String secret;

    public JwtTokenService(String secret) {
        this(secret, Clock.systemUTC(), new ObjectMapper());
    }

    public JwtTokenService(String secret, Clock clock, ObjectMapper objectMapper) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        this.secret = secret;
        this.clock = clock;
        this.objectMapper = objectMapper;
    }

    public String issue(String subject, List<String> roles, String tokenType, Duration ttl) {
        Instant now = Instant.now(clock);
        Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", subject);
        payload.put("roles", roles);
        payload.put("typ", tokenType);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plus(ttl).getEpochSecond());
        String unsigned = base64Json(header) + "." + base64Json(payload);
        return unsigned + "." + sign(unsigned);
    }

    public JwtClaims validate(String token) {
        String[] parts = token == null ? new String[0] : token.split("\\.");
        if (parts.length != 3) {
            throw new JwtValidationException("Invalid JWT format");
        }
        String unsigned = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(unsigned), parts[2])) {
            throw new JwtValidationException("Invalid JWT signature");
        }
        Map<String, Object> payload = readPayload(parts[1]);
        JwtClaims claims = claims(payload);
        if (claims.expired(Instant.now(clock))) {
            throw new JwtValidationException("JWT is expired");
        }
        return claims;
    }

    public String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(AuthConstants.BEARER)) {
            throw new JwtValidationException("Missing bearer token");
        }
        return authorizationHeader.substring(AuthConstants.BEARER.length());
    }

    private JwtClaims claims(Map<String, Object> payload) {
        String subject = string(payload, "sub");
        String tokenType = string(payload, "typ");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) payload.get("roles");
        long issuedAt = number(payload, "iat");
        long expiresAt = number(payload, "exp");
        return new JwtClaims(subject, roles, tokenType, Instant.ofEpochSecond(issuedAt), Instant.ofEpochSecond(expiresAt));
    }

    private String string(Map<String, Object> payload, String name) {
        Object value = payload.get(name);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new JwtValidationException("Missing JWT claim: " + name);
        }
        return text;
    }

    private long number(Map<String, Object> payload, String name) {
        Object value = payload.get(name);
        if (!(value instanceof Number number)) {
            throw new JwtValidationException("Missing JWT claim: " + name);
        }
        return number.longValue();
    }

    private Map<String, Object> readPayload(String payload) {
        try {
            return objectMapper.readValue(DECODER.decode(payload), new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new JwtValidationException("Invalid JWT payload");
        }
    }

    private String base64Json(Map<String, Object> value) {
        try {
            return ENCODER.encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize JWT JSON", ex);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception ex) {
            throw new IllegalStateException("Could not sign JWT", ex);
        }
    }

    private boolean constantTimeEquals(String left, String right) {
        byte[] a = left.getBytes(StandardCharsets.UTF_8);
        byte[] b = right.getBytes(StandardCharsets.UTF_8);
        if (a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
