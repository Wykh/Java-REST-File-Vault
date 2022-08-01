package com.example.filevault.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SpringFileStorage API",
                version = "v0.0.1",
                description = "Spring file storage sample application"))
public class SwaggerConfig {
    // It is able to add groupApi
}
