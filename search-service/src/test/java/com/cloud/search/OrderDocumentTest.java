package com.cloud.search;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.Constructor;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDocumentTest {
    @Test
    void constructorSetsFields() {
        Instant now = Instant.now();
        OrderDocument document = new OrderDocument(10L, 1L, BigDecimal.TEN, "APPROVED", now);

        assertThat(document.getId()).isEqualTo("10");
        assertThat(document.getOrderId()).isEqualTo(10L);
        assertThat(document.getUserId()).isEqualTo(1L);
        assertThat(document.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(document.getPaymentStatus()).isEqualTo("APPROVED");
        assertThat(document.getEventTime()).isEqualTo(now);
    }

    @Test
    void protectedConstructorSupportsElasticsearch() throws Exception {
        Constructor<OrderDocument> constructor = OrderDocument.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getId()).isNull();
    }
}
