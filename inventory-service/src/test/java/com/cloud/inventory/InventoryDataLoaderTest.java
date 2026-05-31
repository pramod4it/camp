package com.cloud.inventory;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InventoryDataLoaderTest {
    @Test
    void runSeedsMissingInventoryOnly() {
        InventoryRepository repository = mock(InventoryRepository.class);
        when(repository.existsById(1001L)).thenReturn(false);
        when(repository.existsById(1002L)).thenReturn(true);

        new InventoryDataLoader(repository).run();

        verify(repository, times(1)).save(org.mockito.ArgumentMatchers.argThat(item -> item.getProductId().equals(1001L)));
        verify(repository, never()).save(org.mockito.ArgumentMatchers.argThat(item -> item.getProductId().equals(1002L)));
    }
}
