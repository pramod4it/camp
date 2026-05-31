package com.rajcloud.observability;

import feign.RequestTemplate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityAutoConfigurationTest {
    @Test
    void createsRootAutoConfiguration() {
        assertThat(new ObservabilityAutoConfiguration()).isNotNull();
    }

    @Test
    void createsServletCorrelationFilter() {
        var configuration = new ObservabilityAutoConfiguration.ServletCorrelationConfiguration();

        assertThat(configuration.correlationIdFilter()).isInstanceOf(CorrelationIdFilter.class);
    }

    @Test
    void createsReactiveCorrelationFilter() {
        var configuration = new ObservabilityAutoConfiguration.ReactiveCorrelationConfiguration();

        assertThat(configuration.reactiveCorrelationIdFilter()).isInstanceOf(ReactiveCorrelationIdFilter.class);
    }

    @Test
    void createsFeignInterceptorThatAddsCorrelationHeader() {
        var configuration = new ObservabilityAutoConfiguration.FeignCorrelationConfiguration();
        CorrelationId.set("corr-feign");
        RequestTemplate template = new RequestTemplate();

        configuration.correlationRequestInterceptor().apply(template);

        assertThat(template.headers().get(CorrelationConstants.CORRELATION_ID_HEADER)).contains("corr-feign");
        CorrelationId.clear();
    }
}
