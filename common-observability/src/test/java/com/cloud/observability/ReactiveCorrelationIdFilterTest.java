package com.cloud.observability;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveCorrelationIdFilterTest {
    @Test
    void filterUsesIncomingCorrelationIdAndWritesResponseHeaderAndContext() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test")
                .header(CorrelationConstants.CORRELATION_ID_HEADER, "corr-123"));
        var filter = new ReactiveCorrelationIdFilter();

        Mono<Void> result = filter.filter(exchange, next -> Mono.deferContextual(context -> {
            assertThat((String) context.get(CorrelationConstants.CORRELATION_ID)).isEqualTo("corr-123");
            return Mono.empty();
        }));

        result.block();
        assertThat(exchange.getResponse().getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER))
                .isEqualTo("corr-123");
    }

    @Test
    void filterCreatesCorrelationIdWhenHeaderIsBlank() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test")
                .header(CorrelationConstants.CORRELATION_ID_HEADER, " "));
        var filter = new ReactiveCorrelationIdFilter();

        filter.filter(exchange, next -> Mono.empty()).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER))
                .isNotBlank();
    }

    @Test
    void filterCreatesCorrelationIdWhenHeaderIsMissing() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test"));
        var filter = new ReactiveCorrelationIdFilter();

        filter.filter(exchange, next -> Mono.empty()).block();

        assertThat(exchange.getResponse().getHeaders().getFirst(CorrelationConstants.CORRELATION_ID_HEADER))
                .isNotBlank();
    }
}
