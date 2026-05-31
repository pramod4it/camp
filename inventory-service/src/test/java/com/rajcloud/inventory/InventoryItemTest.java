package com.rajcloud.inventory;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryItemTest {
    @Test
    void reserveReducesAvailableQuantityWhenStockExists() {
        InventoryItem item = new InventoryItem(1L, "Course", 5);

        assertThat(item.reserve(3)).isTrue();
        assertThat(item.getAvailableQuantity()).isEqualTo(2);
    }

    @Test
    void reserveReturnsFalseWhenInsufficientStock() {
        InventoryItem item = new InventoryItem(1L, "Course", 2);

        assertThat(item.reserve(3)).isFalse();
        assertThat(item.getAvailableQuantity()).isEqualTo(2);
    }

    @Test
    void releaseAddsAvailableQuantity() {
        InventoryItem item = new InventoryItem(1L, "Course", 2);

        item.release(4);

        assertThat(item.getAvailableQuantity()).isEqualTo(6);
    }

    @Test
    void protectedConstructorSupportsJpa() throws Exception {
        Constructor<InventoryItem> constructor = InventoryItem.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThat(constructor.newInstance().getProductId()).isNull();
    }
}
