package com.rajcloud.search;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class SearchServiceApplicationTest {
    @Test
    void mainDelegatesToSpringApplication() {
        String[] args = {"--test=true"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            SearchServiceApplication.main(args);

            springApplication.verify(() -> SpringApplication.run(SearchServiceApplication.class, args));
        }
    }

    @Test
    void canConstructApplication() {
        assertThat(new SearchServiceApplication()).isNotNull();
    }
}
