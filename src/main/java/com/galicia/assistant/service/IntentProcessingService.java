package com.galicia.assistant.service;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.entity.ConversationEntry;
import com.galicia.assistant.repository.ConversationRepository;
import com.galicia.assistant.strategy.IntentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class IntentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(IntentProcessingService.class);

    private final ConversationRepository conversationRepository;
    private final List<IntentStrategy> strategies;
    
    public IntentProcessingService(
            ConversationRepository conversationRepository,
            List<IntentStrategy> strategies //,
            // ResponseSenderService responseSenderService
    ) {
        this.conversationRepository = conversationRepository;
        this.strategies = strategies;
        // this.responseSenderService = responseSenderService;
    }
    
    @RabbitListener(queues = "${assistant.rabbitmq.queue.request}")
    public void processQueryAsync(QueryRequest request) {
        
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
        }
        
        List<ConversationEntry> history = request.getConversationId() != null && !request.getConversationId().isEmpty() ?
                conversationRepository.findByConversationIdOrderByTimestampAsc(request.getConversationId()) :
                Collections.emptyList();
        
        String userQuery = request.getUserQuery().toLowerCase();
        QueryResponse response;

        IntentStrategy matchingStrategy = strategies.stream()
                .filter(strategy -> strategy.matches(userQuery))
                .findFirst()
                .orElse(null);

        if (matchingStrategy != null) {
            // History esta listo para que lo tomen las strategias si lo necesitan.
            response = matchingStrategy.process(request, conversationId);
        } else {
            response = createUnknownResponse(request.getUserId(), conversationId);
        }
        
        saveConversationHistory(request, response);

        log.info("Procesamiento completado y respuesta enviada para ConvID: {}", conversationId);
    }
    
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
