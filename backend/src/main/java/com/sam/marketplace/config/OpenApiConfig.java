package com.sam.marketplace.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartMarketplaceOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Agricultural Marketplace (SAM) REST API")
                        .description("Academic Prototype REST API for Smart Agricultural Marketplace. Connects verified farmers with verified buyers with escrow, delivery, warehouse booking, price benchmarking, dispute management, and audit tracking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("SAM Platform Team")
                                .email("support@sam-marketplace.gov.in"))
                        .license(new License().name("Educational / Academic License")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
