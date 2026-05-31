package com.cloud.inventory;

import com.cloud.api.ApiResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiResource.INVENTORY)
public class InventoryController {
    private final InventoryRepository repository;

    public InventoryController(InventoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<InventoryItem> findAll() {
        return repository.findAll();
    }

    @GetMapping(ApiResource.INVENTORY_BY_PRODUCT_ID)
    public InventoryItem findByProductId(@PathVariable Long productId) {
        return repository.findById(productId).orElseThrow();
    }
}
