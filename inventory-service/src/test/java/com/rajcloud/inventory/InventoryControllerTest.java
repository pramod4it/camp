package com.rajcloud.inventory;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InventoryControllerTest {
    private final InventoryRepository repository = mock(InventoryRepository.class);
    private final InventoryController controller = new InventoryController(repository);

    @Test
    void findAllReturnsInventory() {
        when(repository.findAll()).thenReturn(List.of(new InventoryItem(1001L, "Seat", 5)));

        assertThat(controller.findAll()).hasSize(1);
    }

    @Test
    void findByProductIdReturnsItem() {
        when(repository.findById(1001L)).thenReturn(Optional.of(new InventoryItem(1001L, "Seat", 5)));

        assertThat(controller.findByProductId(1001L).getProductName()).isEqualTo("Seat");
    }

    @Test
    void findByProductIdThrowsWhenMissing() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.findByProductId(999L)).isInstanceOf(RuntimeException.class);
    }
}
