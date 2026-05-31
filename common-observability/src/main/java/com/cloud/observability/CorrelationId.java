package com.cloud.observability;

import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

public final class CorrelationId {
    private CorrelationId() {
    }

    public static String currentOrNew() {
        return Optional.ofNullable(MDC.get(CorrelationConstants.CORRELATION_ID))
                .filter(value -> !value.isBlank())
                .orElseGet(CorrelationId::newId);
    }

    public static String newId() {
        return UUID.randomUUID().toString();
    }

    public static void set(String correlationId) {
        MDC.put(CorrelationConstants.CORRELATION_ID, correlationId);
    }

    public static void clear() {
        MDC.remove(CorrelationConstants.CORRELATION_ID);
        MDC.remove(CorrelationConstants.EVENT_TYPE);
        MDC.remove(CorrelationConstants.TOPIC);
        MDC.remove(CorrelationConstants.ORDER_ID);
        MDC.remove(CorrelationConstants.USER_ID);
    }
}
