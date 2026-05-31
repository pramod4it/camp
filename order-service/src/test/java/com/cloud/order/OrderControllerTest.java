package com.cloud.order;

import com.cloud.observability.KafkaTopics;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderControllerTest {
    private final OrderRepository repository = mock(OrderRepository.class);
    private final UserClient userClient = mock(UserClient.class);
    private final OutboxPublisher outboxPublisher = mock(OutboxPublisher.class);
    private final OrderController controller = new OrderController(repository, userClient, outboxPublisher);

    @Test
    void createValidatesUserSavesOrderAndPublishesEvent() {
        var request = new CreateOrderRequest(1L, 1001L, 2, BigDecimal.TEN);
        var saved = new CustomerOrder(1L, 1001L, 2, BigDecimal.TEN);
        ReflectionTestUtils.setField(saved, "id", 10L);
        when(userClient.findById(1L)).thenReturn(new UserResponse(1L, "A", "a@example.com"));
        when(repository.save(org.mockito.ArgumentMatchers.any(CustomerOrder.class))).thenReturn(saved);

        CustomerOrder result = controller.create(request);

        assertThat(result.getUserId()).isEqualTo(1L);
        verify(userClient).findById(1L);
        verify(outboxPublisher).save(org.mockito.ArgumentMatchers.eq(KafkaTopics.ORDER_CREATED),
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createDoesNotSaveWhenUserValidationFails() {
        var request = new CreateOrderRequest(99L, 1001L, 2, BigDecimal.TEN);
        when(userClient.findById(99L)).thenThrow(new IllegalStateException("missing"));

        assertThatThrownBy(() -> controller.create(request)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findAllAndFindByIdWork() {
        CustomerOrder order = new CustomerOrder(1L, 1001L, 1, BigDecimal.ONE);
        when(repository.findAll()).thenReturn(List.of(order));
        when(repository.findById(1L)).thenReturn(Optional.of(order));

        assertThat(controller.findAll()).hasSize(1);
        assertThat(controller.findById(1L)).isSameAs(order);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findById(1L)).isInstanceOf(RuntimeException.class);
    }
}
