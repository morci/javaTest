package com.galicia.assistant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Define el Bean RestTemplate que será inyectado en WeatherApiClient
    @Bean
    public RestTemplate restTemplate() {
        // Se puede configurar timeouts y interceptors aquí si es necesario
        return new RestTemplate();
    }
}
