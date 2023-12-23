package com.example.taskrestapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()

                .info(
                        new Info()
                                .title("TASK REST API WITH JWT AUTHENTICATION")
                                .description("Task Manager Spring Boot Application.")
                                .version("1.0")

                );
    }


}
