package com.rajcloud.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataLoader implements CommandLineRunner {
    private final InventoryRepository repository;

    public InventoryDataLoader(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        saveIfAbsent(new InventoryItem(1001L, "Java Cloud Camp Seat", 25));
        saveIfAbsent(new InventoryItem(1002L, "Spring Cloud Lab Access", 10));
    }

    private void saveIfAbsent(InventoryItem item) {
        if (!repository.existsById(item.getProductId())) {
            repository.save(item);
        }
    }
}
