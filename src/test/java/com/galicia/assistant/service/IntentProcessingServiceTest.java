package com.galicia.assistant.service;

import com.galicia.assistant.dto.QueryRequest;
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
    void processQueryAsync_shouldPersistUnknownIntent_whenQueryIsNotRecognized() {
        validRequest.setUserQuery("Quiero información sobre mi hipoteca.");
        intentProcessingService.processQueryAsync(validRequest);
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQueryAsync_shouldPersistBalanceIntent() {
        validRequest.setUserQuery("Cuánto saldo tengo en mi cuenta de Galicia.");
        intentProcessingService.processQueryAsync(validRequest);
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQueryAsync_shouldPersistWeatherIntent_whenApiCallIsSuccessful() {
        validRequest.setUserQuery("¿Qué tal está el clima ciudad:Buenos Aires.");
        Map<String, Object> mockWeatherMain = new LinkedHashMap<>();
        mockWeatherMain.put("temp", 18.5);
        Map<String, Object> mockResponse = new LinkedHashMap<>();
        mockResponse.put("main", mockWeatherMain);
        when(weatherApiClient.getWeatherForCity("Buenos Aires")).thenReturn(mockResponse);
        intentProcessingService.processQueryAsync(validRequest);
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void processQueryAsync_shouldPersistWeatherError_whenCityIsMissing() {
        validRequest.setUserQuery("¿Qué tal está el clima?");
        intentProcessingService.processQueryAsync(validRequest);
        verify(conversationRepository, times(1)).save(any());
    }
}
