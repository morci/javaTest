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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    
    @Mock
    private ConversationRepository conversationRepository;
    @Mock
    private WeatherApiClient weatherApiClient;
    
    private IntentProcessingService intentProcessingService;
    private QueryRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new QueryRequest();
        validRequest.setUserId(UUID.randomUUID().toString());
        validRequest.setTimestamp(Instant.now());
        IntentStrategy balanceStrategy = new BalanceIntentStrategy();
        IntentStrategy weatherStrategy = new WeatherIntentStrategy(weatherApiClient);
        intentProcessingService = new IntentProcessingService(conversationRepository, Arrays.asList(balanceStrategy, weatherStrategy));
    }

    @Test
    void processQuery_shouldReturnUnknownIntent_whenQueryIsNotRecognized() {
        validRequest.setUserQuery("Quiero información sobre mi hipoteca.");
        
        QueryResponse response = intentProcessingService.processQuery(validRequest);
        
        assertEquals("INTENT_UNKNOWN", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("no entiendo la intención"));
        assertEquals("OK", response.getServiceStatus());
        
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQuery_shouldReturnCheckBalanceIntent() {
        validRequest.setUserQuery("Cuánto saldo tengo en mi cuenta de Galicia.");
        
        QueryResponse response = intentProcessingService.processQuery(validRequest);
        
        assertEquals("INTENT_CHECK_BALANCE", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("verificar su identidad"));
        assertEquals("OK", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQuery_shouldReturnWeather_whenApiCallIsSuccessful() {
        validRequest.setUserQuery("¿Qué tal está el clima ciudad:Buenos Aires.");
        
        Map<String, Object> mockWeatherMain = new LinkedHashMap<>();
        mockWeatherMain.put("temp", 18.5);

        Map<String, Object> mockResponse = new LinkedHashMap<>();
        mockResponse.put("main", mockWeatherMain);
        
        when(weatherApiClient.getWeatherForCity("Buenos Aires")).thenReturn(mockResponse);
        
        QueryResponse response = intentProcessingService.processQuery(validRequest);
        
        assertEquals("INTENT_WEATHER_QUERY", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("18.5°C"));
        assertEquals("OK", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQuery_shouldHandleApiError_whenCityIsNotFound() {
        validRequest.setUserQuery("¿Qué tal está el clima?");
        
        QueryResponse response = intentProcessingService.processQuery(validRequest);
        
        assertEquals("INTENT_WEATHER_MISSING_CITY", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("Para saber el clima, necesito la ciudad. Por favor, escriba: ciudad:[Nombre de la ciudad]."));
        assertEquals("ERROR_BAD_REQUEST", response.getServiceStatus());
        verify(conversationRepository, times(1)).save(any());
    }
}
