package com.cloud.notification;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ConfigurationTest {
    @Test
    void openApiConfigCreatesNotificationApiMetadata() {
        assertThat(new OpenApiConfig().openAPI().getInfo().getTitle()).isEqualTo("Notification Service API");
    }

    @Test
    void kafkaRetryConfigCreatesListenerFactory() {
        var factory = new KafkaRetryConfig().kafkaListenerContainerFactory(mock(org.springframework.kafka.core.ConsumerFactory.class),
                mock(org.springframework.kafka.core.KafkaTemplate.class));

        assertThat(factory).isNotNull();
    }

    @Test
    void kafkaRetryConfigRoutesFailuresToDeadLetterTopic() {
        var partition = KafkaRetryConfig.dltPartition(new ConsumerRecord<>("payments", 3, 0L, "key", "value"),
                new RuntimeException("failed"));

        assertThat(partition.topic()).isEqualTo("payments.DLT");
        assertThat(partition.partition()).isEqualTo(3);
    }
}
