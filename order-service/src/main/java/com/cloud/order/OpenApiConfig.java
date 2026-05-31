package com.cloud.order;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Order Service API")
                .version("0.0.1")
                .description("Order APIs with OpenFeign validation, outbox publishing, and Saga state updates."));
    }
}
