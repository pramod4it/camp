package com.rajcloud.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.Instant;

@Document(indexName = "orders")
public class OrderDocument {
    @Id
    private String id;
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentStatus;
    private Instant eventTime;

    protected OrderDocument() {
    }

    public OrderDocument(Long orderId, Long userId, BigDecimal amount, String paymentStatus, Instant eventTime) {
        this.id = orderId.toString();
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.eventTime = eventTime;
    }

    public String getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Instant getEventTime() {
        return eventTime;
    }
}
