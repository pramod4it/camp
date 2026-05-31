package com.cloud.order;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerOrderTest {
    @Test
    void newOrderStartsPendingAndStateTransitionsWork() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 2, BigDecimal.TEN);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreatedAt()).isNotNull();

        order.markInventoryReserved();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.INVENTORY_RESERVED);

        order.markPaid();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        order.markPaymentFailed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);

        order.markInventoryRejected();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.INVENTORY_REJECTED);

        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<CustomerOrder> constructor = CustomerOrder.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getId()).isNull();
    }
}
