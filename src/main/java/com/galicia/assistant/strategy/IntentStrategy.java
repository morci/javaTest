package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;

/**
 * Define el contrato para el procesamiento de una intención específica.
 */
public interface IntentStrategy {

    /**
     * Determina si esta estrategia puede manejar la consulta del usuario.
     */
    boolean matches(String userQuery);

    /**
     * Ejecuta la lógica de procesamiento para la intención y devuelve la respuesta.
     */
    QueryResponse process(QueryRequest request, String conversationId);
}
