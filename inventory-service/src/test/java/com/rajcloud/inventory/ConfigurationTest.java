package com.rajcloud.inventory;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConfigurationTest {
    @Test
    void openApiConfigCreatesInventoryApiMetadata() {
        assertThat(new OpenApiConfig().openAPI().getInfo().getTitle()).isEqualTo("Inventory Service API");
    }

    @Test
    void kafkaRetryConfigCreatesListenerFactory() {
        var factory = new KafkaRetryConfig().kafkaListenerContainerFactory(mock(org.springframework.kafka.core.ConsumerFactory.class),
                mock(org.springframework.kafka.core.KafkaTemplate.class));

        assertThat(factory).isNotNull();
    }

    @Test
    void kafkaRetryConfigRoutesFailuresToDeadLetterTopic() {
        var partition = KafkaRetryConfig.dltPartition(new ConsumerRecord<>("orders", 2, 0L, "key", "value"),
                new RuntimeException("failed"));

        assertThat(partition.topic()).isEqualTo("orders.DLT");
        assertThat(partition.partition()).isEqualTo(2);
    }
}
