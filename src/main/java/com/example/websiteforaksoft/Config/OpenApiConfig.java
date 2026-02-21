package com.example.websiteforaksoft.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Название схемы безопасности
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Информация о API
                .info(new Info()
                        .title("Aksoft Website API")
                        .version("1.0")
                        .description("REST API для сайта Aksoft с JWT авторизацией"))

                // Добавляем требование авторизации для всех endpoints
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // Определяем схему безопасности (JWT Bearer Token)
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Введите JWT токен (без 'Bearer ')")
                        )
                );
    }
}
