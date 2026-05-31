package com.rajcloud.order;

import com.rajcloud.api.ApiResource;
import com.rajcloud.events.OrderCreatedEvent;
import com.rajcloud.observability.KafkaTopics;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiResource.ORDERS)
public class OrderController {
    private final OrderRepository repository;
    private final UserClient userClient;
    private final OutboxPublisher outboxPublisher;

    public OrderController(OrderRepository repository, UserClient userClient, OutboxPublisher outboxPublisher) {
        this.repository = repository;
        this.userClient = userClient;
        this.outboxPublisher = outboxPublisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerOrder create(@RequestBody CreateOrderRequest request) {
        userClient.findById(request.userId());
        CustomerOrder order = repository.save(new CustomerOrder(request.userId(), request.productId(),
                request.quantity(), request.amount()));
        outboxPublisher.save(KafkaTopics.ORDER_CREATED, order.getId().toString(),
                new OrderCreatedEvent(order.getId(), order.getUserId(), order.getProductId(),
                        order.getQuantity(), order.getAmount(), order.getCreatedAt()));
        return order;
    }

    @GetMapping
    public List<CustomerOrder> findAll() {
        return repository.findAll();
    }

    @GetMapping(ApiResource.ORDER_BY_ID)
    public CustomerOrder findById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
