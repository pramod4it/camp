package com.cloud.observability;

public final class CorrelationConstants {
    public static final String CORRELATION_ID = "correlation_id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String SERVICE_NAME = "service_name";
    public static final String EVENT_TYPE = "event_type";
    public static final String TOPIC = "topic";
    public static final String ORDER_ID = "order_id";
    public static final String USER_ID = "user_id";

    private CorrelationConstants() {
    }
}
