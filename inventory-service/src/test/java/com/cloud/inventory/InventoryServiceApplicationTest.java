package com.cloud.inventory;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class InventoryServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            InventoryServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(InventoryServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new InventoryServiceApplication()).isNotNull();
    }
}
