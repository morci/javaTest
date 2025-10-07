package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.service.WeatherApiClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WeatherIntentStrategyTest {

    private final WeatherApiClient weatherApiClient = Mockito.mock(WeatherApiClient.class);
    private final WeatherIntentStrategy strategy = new WeatherIntentStrategy(weatherApiClient);

    @Test
    void matches_shouldReturnTrueForWeatherKeywords() {
        assertTrue(strategy.matches("¿Qué clima hace en Madrid?"));
        assertTrue(strategy.matches("¿Cuál es la temperatura?"));
        assertTrue(strategy.matches("¿Cómo está el tiempo?"));
    }

    @Test
    void matches_shouldReturnFalseForOtherQueries() {
        assertFalse(strategy.matches("¿Cuál es mi saldo?"));
        assertFalse(strategy.matches("Hola, asistente"));
    }

    @Test
    void process_shouldReturnMissingCityIntentIfNoCityProvided() {
        QueryRequest request = new QueryRequest("user1", "¿Qué clima hace?");
        QueryResponse response = strategy.process(request, "conv-2");
        assertEquals("INTENT_WEATHER_MISSING_CITY", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("necesito la ciudad"));
        assertEquals("ERROR_BAD_REQUEST", response.getServiceStatus());
    }

    @Test
    void process_shouldReturnWeatherQueryIntentIfApiReturnsData() {
        QueryRequest request = new QueryRequest("user2", "ciudad:Madrid. ¿Qué clima hace?");
        Map<String, Object> main = new LinkedHashMap<>();
        main.put("temp", 22.0);
        Map<String, Object> apiResponse = new LinkedHashMap<>();
        apiResponse.put("main", main);
        when(weatherApiClient.getWeatherForCity("Madrid")).thenReturn(apiResponse);
        QueryResponse response = strategy.process(request, "conv-3");
        assertEquals("INTENT_WEATHER_QUERY", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("Madrid"));
        assertTrue(response.getAssistantResponse().contains("22.0°C"));
        assertEquals("OK", response.getServiceStatus());
    }

    @Test
    void process_shouldReturnErrorIfApiThrowsHttpClientErrorException() {
        QueryRequest request = new QueryRequest("user3", "ciudad: Desconocida. ¿Qué clima hace?");
        when(weatherApiClient.getWeatherForCity(anyString())).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        QueryResponse response = strategy.process(request, "conv-4");
        assertEquals("INTENT_WEATHER_QUERY_ERROR", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("No pude encontrar el clima"));
        assertEquals("ERROR_API_CLIENT", response.getServiceStatus());
    }

    @Test
    void process_shouldReturnGeneralErrorIfApiThrowsOtherException() {
        QueryRequest request = new QueryRequest("user4", "ciudad: Error. ¿Qué clima hace?");
        when(weatherApiClient.getWeatherForCity(anyString())).thenThrow(new RuntimeException("Fallo inesperado"));
        QueryResponse response = strategy.process(request, "conv-5");
        assertEquals("INTENT_GENERAL_ERROR", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("problema técnico"));
        assertEquals("ERROR_INTERNAL", response.getServiceStatus());
    }
}

