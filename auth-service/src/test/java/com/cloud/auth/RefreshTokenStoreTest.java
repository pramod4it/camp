package com.cloud.auth;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenStoreTest {
    @Test
    void createsAndConsumesRefreshTokenOnce() {
        RefreshTokenStore store = new RefreshTokenStore();
        String token = store.create("user");

        assertThat(store.consume(token)).contains("user");
        assertThat(store.consume(token)).isEmpty();
        assertThat(store.consume("missing")).isEmpty();
    }
}
