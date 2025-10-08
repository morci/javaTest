package com.galicia.assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class WeatherApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;
    
    public WeatherApiClient(
            RestTemplate restTemplate,
            @Value("${weather.api.base-url}") String baseUrl,
            @Value("${weather.api.key}") String apiKey) {

        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    /**
     * Llama a la API de clima para obtener la información meteorológica.
     * @param city Nombre de la ciudad para la consulta.
     * @return Map<String, Object> con la respuesta JSON parseada.
     */
    public Map<String, Object> getWeatherForCity(String city) {
        
        java.net.URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build() 
                .toUri();
        
        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        return response;
    }
}
