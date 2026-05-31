package com.rajcloud.gateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
                .title("Camp API Gateway")
                .version("0.0.1")
                .description("Gateway for the Java 17 to Java 25 Spring Cloud microservices camp."));
    }
}
