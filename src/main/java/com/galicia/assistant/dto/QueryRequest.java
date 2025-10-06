package com.galicia.assistant.dto;

import java.time.Instant;

// Este DTO se usará para la entrada REST y el mensaje de Kafka
public class QueryRequest {

    // UUID del usuario (como String)
    private String userId;

    // Consulta de texto del usuario
    private String userQuery;

    // Marca de tiempo de la consulta
    private Instant timestamp;

    // Constructor vacío requerido por Spring/Jackson
    public QueryRequest() {
    }

    // Constructor con parámetros
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

    @Override
    public String toString() {
        return "QueryRequest{" +
                "userId='" + userId + '\'' +
                ", userQuery='" + userQuery + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
