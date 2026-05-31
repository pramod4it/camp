package com.rajcloud.auth;

import java.time.Instant;
import java.util.List;

public record TokenValidationResponse(boolean active, String subject, List<String> roles, String tokenType,
                                      Instant expiresAt) {
}
