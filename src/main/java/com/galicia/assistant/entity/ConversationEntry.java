package com.galicia.assistant.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversation_history")
public class ConversationEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String conversationId; // UUID

    @Column(nullable = false, updatable = false)
    private String userId; // UUID

    @Column(columnDefinition = "TEXT")
    private String userQuery;

    @Column(nullable = false)
    private String processedIntent;

    @Column(columnDefinition = "TEXT")
    private String assistantResponse;

    @Column(updatable = false)
    private Instant timestamp;

    private String status;

    // Constructor vacío (JPA)
    public ConversationEntry() {
    }

    // Constructor de mapeo
    public ConversationEntry(String conversationId, String userId, String userQuery, String processedIntent, String assistantResponse, Instant timestamp, String status) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.userQuery = userQuery;
        this.processedIntent = processedIntent;
        this.assistantResponse = assistantResponse;
        this.timestamp = timestamp;
        this.status = status;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    // Omito setID() ya que es generado

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

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

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
