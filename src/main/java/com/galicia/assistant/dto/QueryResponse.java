package com.galicia.assistant.dto;

import java.time.Instant;
import java.util.UUID;

public class QueryResponse {
    
    private String conversationId;

    private String userId;
    
    private String processedIntent;
    
    private String assistantResponse;
    
    private String serviceStatus;

    private Instant processedTimestamp;
    
    public QueryResponse() {
    }
    
    public QueryResponse(String userId, String processedIntent, String assistantResponse, String serviceStatus) {
        this.conversationId = UUID.randomUUID().toString(); // Genera el ID de conversación aquí
        this.userId = userId;
        this.processedIntent = processedIntent;
        this.assistantResponse = assistantResponse;
        this.serviceStatus = serviceStatus;
        this.processedTimestamp = Instant.now();
    }
    
    public String getConversationId() {
        return conversationId;
    }
    

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
