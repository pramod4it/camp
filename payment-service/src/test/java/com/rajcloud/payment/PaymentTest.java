package com.rajcloud.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentTest {
    @Test
    void constructorSetsFields() {
        Payment payment = new Payment(10L, 1L, BigDecimal.TEN, PaymentStatus.APPROVED);

        assertThat(payment.getOrderId()).isEqualTo(10L);
        assertThat(payment.getUserId()).isEqualTo(1L);
        assertThat(payment.getAmount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(payment.getProcessedAt()).isNotNull();
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<Payment> constructor = Payment.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getId()).isNull();
    }
}
