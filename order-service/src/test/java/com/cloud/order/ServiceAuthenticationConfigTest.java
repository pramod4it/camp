package com.cloud.order;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ServiceAuthenticationConfigTest {
    private final ServiceAuthenticationConfig configuration = new ServiceAuthenticationConfig();

    @Test
    void createsServiceAuthBeansAndAddsAuthorizationHeader() {
        var jwt = configuration.jwtTokenService("camp-development-secret-key-32-chars");
        var provider = configuration.serviceTokenProvider(jwt);
        var interceptor = configuration.serviceAuthenticationInterceptor(provider);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers().get("Authorization").iterator().next()).startsWith("Bearer ");
    }
}
