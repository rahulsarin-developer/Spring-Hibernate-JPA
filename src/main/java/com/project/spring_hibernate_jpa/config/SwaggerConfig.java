package com.project.spring_hibernate_jpa.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI employeeOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management API")
                        .description("""
                                REST APIs for Employee Management System.
                                Supports CRUD operations, pagination,
                                sorting, filtering and salary updates.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team"))
                        .license(new License()
                                .name("Internal Use")));
    }
}
