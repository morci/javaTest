package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BalanceIntentStrategyTest {

    private final BalanceIntentStrategy strategy = new BalanceIntentStrategy();

    @Test
    void matches_shouldReturnTrueForSaldoKeywords() {
        assertTrue(strategy.matches("¿Cuál es mi saldo?"));
        assertTrue(strategy.matches("Cuánto tengo en mi cuenta?"));
    }

    @Test
    void matches_shouldReturnFalseForOtherQueries() {
        assertFalse(strategy.matches("¿Qué clima hace?"));
        assertFalse(strategy.matches("Hola, asistente"));
    }

    @Test
    void process_shouldReturnCheckBalanceIntentResponse() {
        QueryRequest request = new QueryRequest("user123", "¿Cuál es mi saldo?");
        QueryResponse response = strategy.process(request, "conv-1");
        assertEquals("INTENT_CHECK_BALANCE", response.getProcessedIntent());
        assertTrue(response.getAssistantResponse().contains("verificar su identidad"));
        assertEquals("OK", response.getServiceStatus());
        assertEquals("conv-1", response.getConversationId());
        assertEquals("user123", response.getUserId());
    }
}

