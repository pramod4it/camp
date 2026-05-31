package com.cloud.search;

import com.cloud.api.ApiResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiResource.SEARCH)
public class SearchController {
    private final OrderSearchRepository repository;

    public SearchController(OrderSearchRepository repository) {
        this.repository = repository;
    }

    @GetMapping(ApiResource.SEARCH_ORDERS)
    public Iterable<OrderDocument> findOrders() {
        return repository.findAll();
    }
}
