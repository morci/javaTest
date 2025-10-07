package com.galicia.assistant.service;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.repository.ConversationRepository;
import com.galicia.assistant.strategy.BalanceIntentStrategy;
import com.galicia.assistant.strategy.WeatherIntentStrategy;
import com.galicia.assistant.strategy.IntentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntentProcessingServiceTest {

    // Simula (Mock) el repositorio y el cliente de API
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private WeatherApiClient weatherApiClient;

    // Inyecta los mocks en la instancia del servicio que vamos a probar
    private IntentProcessingService intentProcessingService;
    private QueryRequest validRequest;

    @BeforeEach
    void setUp() {
        // Objeto de Request base para las pruebas
        validRequest = new QueryRequest();
        validRequest.setUserId(UUID.randomUUID().toString());
        validRequest.setTimestamp(Instant.now());
        // Inyecta las estrategias reales y mocks
        IntentStrategy balanceStrategy = new BalanceIntentStrategy();
        IntentStrategy weatherStrategy = new WeatherIntentStrategy(weatherApiClient);
        intentProcessingService = new IntentProcessingService(conversationRepository, Arrays.asList(balanceStrategy, weatherStrategy));
    }

    // --- PRUEBAS DE INTENCIÓN DESCONOCIDA ---

    @Test
    void processQuery_shouldReturnUnknownIntent_whenQueryIsNotRecognized() {
        // Arrange
        validRequest.setUserQuery("Quiero información sobre mi hipoteca.");

        // Act
        QueryResponse response = intentProcessingService.processQuery(validRequest);

        // Assert
        assertEquals("INTENT_UNKNOWN", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("no entiendo la intención"));
        assertEquals("OK", response.getServiceStatus());
        // Verifica que la persistencia se llamó una vez
        verify(conversationRepository, times(1)).save(any());
    }

    // --- PRUEBAS DE INTENCIÓN DE SALDO (Simulada) ---

    @Test
    void processQuery_shouldReturnCheckBalanceIntent() {
        // Arrange
        validRequest.setUserQuery("Cuánto saldo tengo en mi cuenta de Galicia.");

        // Act
        QueryResponse response = intentProcessingService.processQuery(validRequest);

        // Assert
        assertEquals("INTENT_CHECK_BALANCE", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("verificar su identidad"));
        assertEquals("OK", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }

    // --- PRUEBAS DE INTEGRACIÓN DE CLIMA (Mockeada) ---

    @Test
    void processQuery_shouldReturnWeather_whenApiCallIsSuccessful() {
        // Arrange
        validRequest.setUserQuery("¿Qué tal está el clima ciudad:Buenos Aires.");

        // Simulación del JSON de respuesta de la API (solo los campos que usamos)
        Map<String, Object> mockWeatherMain = new LinkedHashMap<>();
        mockWeatherMain.put("temp", 18.5); // Temperatura simulada

        Map<String, Object> mockResponse = new LinkedHashMap<>();
        mockResponse.put("main", mockWeatherMain); // Estructura anidada "main"

        // Define el comportamiento del mock: cuando se llame al cliente, devuelve la respuesta mockeada
        when(weatherApiClient.getWeatherForCity("Buenos Aires")).thenReturn(mockResponse);

        // Act
        QueryResponse response = intentProcessingService.processQuery(validRequest);

        // Assert
        assertEquals("INTENT_WEATHER_QUERY", response.getProcessedIntent());
        // Verifica que el texto de respuesta contenga la temperatura mockeada
        assertTrue(response.getAssistantResponse().contains("18.5°C"));
        assertEquals("OK", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQuery_shouldHandleApiError_whenCityIsNotFound() {
        // Arrange
        validRequest.setUserQuery("¿Qué tal está el clima?");

        // Act
        QueryResponse response = intentProcessingService.processQuery(validRequest);

        // Assert
        assertEquals("INTENT_WEATHER_MISSING_CITY", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("Para saber el clima, necesito la ciudad. Por favor, escriba: ciudad:[Nombre de la ciudad]."));
        assertEquals("ERROR_BAD_REQUEST", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }
}
