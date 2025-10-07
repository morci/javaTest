package com.galicia.assistant.dto;

import java.time.Instant;


public class QueryRequest {
    
    private String userId;
    
    private String userQuery;
    
    private Instant timestamp;

    private String conversationId;
    
    public QueryRequest() {
    }
    
    public QueryRequest(String userId, String userQuery) {
        this.userId = userId;
        this.userQuery = userQuery;
        this.timestamp = Instant.now();
    }

    // Getters y Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getConversationId() {
        return conversationId;
    }
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    @Override
    public String toString() {
        return "QueryRequest{" +
                "userId='" + userId + '\'' +
                ", userQuery='" + userQuery + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
