package com.rajcloud.observability;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveCorrelationIdFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders()
                .getFirst(CorrelationConstants.CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = CorrelationId.newId();
        }
        String finalCorrelationId = correlationId;
        exchange.getResponse().getHeaders().set(CorrelationConstants.CORRELATION_ID_HEADER, finalCorrelationId);
        return chain.filter(exchange)
                .contextWrite(context -> context.put(CorrelationConstants.CORRELATION_ID, finalCorrelationId));
    }
}
