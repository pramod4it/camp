package com.cloud.order;

import java.math.BigDecimal;

public record CreateOrderRequest(Long userId, Long productId, Integer quantity, BigDecimal amount) {
}
