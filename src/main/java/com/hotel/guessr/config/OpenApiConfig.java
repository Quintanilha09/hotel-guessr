package com.hotel.guessr.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Guessr API")
                        .version("1.0.0")
                        .description("API para consulta de CEPs e busca de hotéis próximos utilizando Google Places")
                        .contact(new Contact()
                                .name("Hotel Guessr Team")
                                .email("contato@hotelguessr.com")));
    }
}
