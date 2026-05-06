package com.northpay.backend.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NorthPay Backend API")
                        .description("API para el sistema de onboarding de contratistas de NorthPay")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("NorthPay Team")
                                .email("support@northpay.com")
                                .url("https://northpay.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:7860")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url(" https://n4nd0-northpay-backend.hf.space")
                                .description("Servidor de producción")
                ));
    }
}
