package com.galicia.assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.repository.ConversationRepository;
import com.galicia.assistant.service.WeatherApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Inicia el contexto completo de Spring Boot para la prueba
@SpringBootTest
@AutoConfigureMockMvc
class QueryControllerIntegrationTest {

    // Simula las peticiones HTTP
    @Autowired
    private MockMvc mockMvc;

    // Herramienta para serializar JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Repositorio para verificar la persistencia REAL en H2
    @MockBean
    private ConversationRepository conversationRepository;

    // 🌟 Mockeamos el cliente externo para aislar la prueba y evitar llamadas reales
    @MockBean
    private WeatherApiClient weatherApiClient;

    @Test
    void postQuery_shouldProcessWeatherQueryAndPersistResult() throws Exception {
        // ARRANGE: Preparar la petición y mockear la respuesta de la API de Clima

        // 1. Datos de la petición
        QueryRequest request = new QueryRequest();
        request.setUserId(UUID.randomUUID().toString());
        request.setUserQuery("¿Qué temperatura hace ciudad:Buenos Aires.");
        request.setTimestamp(Instant.now());

        // 2. Mockear respuesta de la API externa
        Map<String, Object> mockWeatherMain = new LinkedHashMap<>();
        mockWeatherMain.put("temp", 25.5); // Mockeamos 25.5°C
        Map<String, Object> mockResponse = new LinkedHashMap<>();
        mockResponse.put("main", mockWeatherMain);

        when(weatherApiClient.getWeatherForCity(anyString())).thenReturn(mockResponse);

        // ACT: Ejecutar la petición HTTP simulada
        mockMvc.perform(post("/api/v1/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                // ASSERT HTTP: Verificar el status y el cuerpo de la respuesta
                .andExpect(status().isOk()) // Espera HTTP 200 OK
                .andExpect(jsonPath("$.processedIntent", is("INTENT_WEATHER_QUERY")))
                .andExpect(jsonPath("$.assistantResponse", containsString("25.5°C"))) // Verifica el dato mockeado
                .andExpect(jsonPath("$.serviceStatus", is("OK")));
        
        verify(conversationRepository, times(1)).save(any());
    }

    @Test
    void postQuery_shouldReturn400BadRequest_whenUserIdIsMissing() throws Exception {
        // ARRANGE: Petición sin userId
        QueryRequest request = new QueryRequest();
        request.setUserQuery("Consulta sin ID");

        // ACT & ASSERT: Ejecutar y esperar un código de error 400
        mockMvc.perform(post("/api/v1/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isBadRequest()) // Espera HTTP 400 Bad Request
                .andExpect(jsonPath("$", is("Invalid input: userId and userQuery are required.")));

        // ASSERT PERSISTENCIA: No debe haber guardado nada
        verify(conversationRepository, never()).save(any());
    }
}
