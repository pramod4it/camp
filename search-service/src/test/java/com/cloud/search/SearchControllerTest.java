package com.cloud.search;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchControllerTest {
    @Test
    void findOrdersReturnsDocuments() {
        OrderSearchRepository repository = mock(OrderSearchRepository.class);
        when(repository.findAll()).thenReturn(List.of(new OrderDocument(10L, 1L, BigDecimal.TEN, "PENDING", Instant.now())));

        assertThat(new SearchController(repository).findOrders()).hasSize(1);
    }
}
