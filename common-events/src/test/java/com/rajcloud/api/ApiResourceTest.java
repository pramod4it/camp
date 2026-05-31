package com.rajcloud.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResourceTest {
    @Test
    void exposesVersionedApiResources() {
        assertThat(ApiResource.API).isEqualTo("/api");
        assertThat(ApiResource.V1).isEqualTo("/api/v1");
        assertThat(ApiResource.V2).isEqualTo("/api/v2");
        assertThat(ApiResource.AUTH).isEqualTo("/api/v1/auth");
        assertThat(ApiResource.AUTH_TOKEN).isEqualTo("/api/v1/auth/token");
        assertThat(ApiResource.AUTH_VALIDATE).isEqualTo("/api/v1/auth/validate");
        assertThat(ApiResource.USERS).isEqualTo("/api/v1/users");
        assertThat(ApiResource.INVENTORY).isEqualTo("/api/v1/inventory");
        assertThat(ApiResource.ORDERS).isEqualTo("/api/v1/orders");
        assertThat(ApiResource.PAYMENTS).isEqualTo("/api/v1/payments");
        assertThat(ApiResource.NOTIFICATIONS).isEqualTo("/api/v1/notifications");
        assertThat(ApiResource.SEARCH + ApiResource.SEARCH_ORDERS).isEqualTo("/api/v1/search/orders");
    }
}
