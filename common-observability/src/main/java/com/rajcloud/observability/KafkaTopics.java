package com.rajcloud.observability;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_REJECTED = "inventory-rejected";
    public static final String PAYMENT_PROCESSED = "payment-processed";
    public static final String INVENTORY_RELEASE_REQUESTED = "inventory-release-requested";

    private KafkaTopics() {
    }

    public static String deadLetter(String topic) {
        return topic + ".DLT";
    }
}
