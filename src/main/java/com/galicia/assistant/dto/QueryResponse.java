package com.galicia.assistant.dto;

import java.time.Instant;
import java.util.UUID;

// Este DTO será el mensaje de salida, conteniendo la respuesta del asistente.
public class QueryResponse {

    // ID único de la interacción (UUID)
    private String conversationId;

    private String userId;

    // La intención procesada (ej. PREGUNTAR_CLIMA)
    private String processedIntent;

    // La respuesta de texto final del asistente
    private String assistantResponse;

    // Estado del procesamiento (OK, ERROR, etc.)
    private String serviceStatus;

    private Instant processedTimestamp;

    // Constructor vacío
    public QueryResponse() {
    }

    // Constructor principal
    public QueryResponse(String userId, String processedIntent, String assistantResponse, String serviceStatus) {
        this.conversationId = UUID.randomUUID().toString(); // Genera el ID de conversación aquí
        this.userId = userId;
        this.processedIntent = processedIntent;
        this.assistantResponse = assistantResponse;
        this.serviceStatus = serviceStatus;
        this.processedTimestamp = Instant.now();
    }

    // Getters y Setters
    public String getConversationId() {
        return conversationId;
    }

    // Nota: No se proporciona setConversationId para mantenerlo inmutable después de la creación

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProcessedIntent() {
        return processedIntent;
    }

    public void setProcessedIntent(String processedIntent) {
        this.processedIntent = processedIntent;
    }

    public String getAssistantResponse() {
        return assistantResponse;
    }

    public void setAssistantResponse(String assistantResponse) {
        this.assistantResponse = assistantResponse;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public Instant getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(Instant processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
