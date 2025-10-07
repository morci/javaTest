package com.galicia.assistant.service;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.entity.ConversationEntry;
import com.galicia.assistant.repository.ConversationRepository;
import com.galicia.assistant.strategy.IntentStrategy; // Nueva importación
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IntentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(IntentProcessingService.class);

    private final ConversationRepository conversationRepository;
    private final List<IntentStrategy> strategies; // Inyección de todas las estrategias

    // Constructor que inyecta el Repositorio y la lista de Estrategias
    public IntentProcessingService(
            ConversationRepository conversationRepository,
            List<IntentStrategy> strategies // Spring inyecta TODAS las clases que implementan IntentStrategy
    ) {
        this.conversationRepository = conversationRepository;
        this.strategies = strategies;
    }

    public QueryResponse processQuery(QueryRequest request) {

        // 1. MANEJO DEL CONVERSATION ID (Lógica centralizada)
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
            log.info("Iniciando nueva conversación con ID: {}", conversationId);
        }

        // 2. DELEGACIÓN A LA ESTRATEGIA (Corazón del patrón Strategy)
        String userQuery = request.getUserQuery().toLowerCase();
        QueryResponse response;

        // Busca la primera estrategia que coincida
        IntentStrategy matchingStrategy = strategies.stream()
                .filter(strategy -> strategy.matches(userQuery))
                .findFirst()
                .orElse(null); // Si no coincide ninguna, es nulo

        if (matchingStrategy != null) {
            // Si hay coincidencia, delega el procesamiento completo a la estrategia
            response = matchingStrategy.process(request, conversationId);
        } else {
            // Si no coincide ninguna, genera la respuesta de "INTENT_UNKNOWN"
            response = createUnknownResponse(request.getUserId(), conversationId);
        }

        // 3. Persistencia (Guardar Historial)
        saveConversationHistory(request, response);

        log.info("Procesamiento completado y respuesta generada para ConvID: {}", conversationId);

        return response;
    }

    // ------------------ Métodos Auxiliares ------------------

    private QueryResponse createUnknownResponse(String userId, String conversationId) {
        String intent = "INTENT_UNKNOWN";
        String responseText = "Disculpe, no entiendo la intención. Por favor, reformule su consulta.";
        String status = "OK";

        QueryResponse response = new QueryResponse(userId, intent, responseText, status);
        response.setConversationId(conversationId);
        return response;
    }

    private void saveConversationHistory(QueryRequest request, QueryResponse response) {
        ConversationEntry entry = new ConversationEntry(
                response.getConversationId(),
                request.getUserId(),
                request.getUserQuery(),
                response.getProcessedIntent(),
                response.getAssistantResponse(),
                request.getTimestamp(),
                response.getServiceStatus()
        );
        conversationRepository.save(entry);
        log.info("Historial guardado en H2 para ConvID: {}", entry.getConversationId());
    }
}
