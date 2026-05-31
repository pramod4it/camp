package com.cloud.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentControllerTest {
    @Test
    void findAllReturnsPayments() {
        PaymentRepository repository = mock(PaymentRepository.class);
        when(repository.findAll()).thenReturn(List.of(new Payment(1L, 1L, BigDecimal.TEN, PaymentStatus.APPROVED)));

        assertThat(new PaymentController(repository).findAll()).hasSize(1);
    }
}
