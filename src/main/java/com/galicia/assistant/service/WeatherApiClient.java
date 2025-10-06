package com.galicia.assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Cliente para interactuar con la API externa de clima (OpenWeatherMap).
 */
@Service
public class WeatherApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    // Usamos el constructor para inyectar RestTemplate y los valores de properties
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

        // 🌟 CORRECCIÓN CLAVE: Usar build().toUri() para forzar la codificación
        java.net.URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build() // Construye el componente de URI
                .toUri(); // Convierte a objeto URI codificado

        // 🌟 Usar getForObject con el objeto URI, no con el String de la URL
        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);

        return response;
    }
}
