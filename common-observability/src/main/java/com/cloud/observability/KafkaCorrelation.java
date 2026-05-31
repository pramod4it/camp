package com.cloud.observability;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.MDC;
import org.springframework.kafka.listener.RecordInterceptor;

import java.nio.charset.StandardCharsets;

public final class KafkaCorrelation {
    private KafkaCorrelation() {
    }

    public static RecordInterceptor<String, Object> recordInterceptor() {
        return (ConsumerRecord<String, Object> record, org.apache.kafka.clients.consumer.Consumer<String, Object> consumer) -> {
            String correlationId = header(record.headers(), CorrelationConstants.CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = CorrelationId.newId();
            }
            CorrelationId.set(correlationId);
            MDC.put(CorrelationConstants.TOPIC, record.topic());
            MDC.put(CorrelationConstants.EVENT_TYPE, header(record.headers(), CorrelationConstants.EVENT_TYPE));
            return record;
        };
    }

    private static String header(Headers headers, String name) {
        Header header = headers.lastHeader(name);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
