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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

@SpringBootTest
@AutoConfigureMockMvc
class QueryControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ConversationRepository conversationRepository;
    
    @MockBean
    private WeatherApiClient weatherApiClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void postQuery_shouldPublishToQueueAndReturnAccepted() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setUserId(UUID.randomUUID().toString());
        request.setUserQuery("¿Qué temperatura hace ciudad:Buenos Aires.");
        request.setTimestamp(Instant.now());
        
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), org.mockito.Mockito.any(QueryRequest.class));

        mockMvc.perform(post("/api/v1/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$", containsString("Solicitud aceptada. El procesamiento es asíncrono.")));
    }

    @Test
    void postQuery_shouldReturn400BadRequest_whenUserIdIsMissing() throws Exception {
        QueryRequest request = new QueryRequest();
        request.setUserQuery("Consulta sin ID");
        mockMvc.perform(post("/api/v1/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", is("Invalid input: userId and userQuery are required.")));
    }
}
