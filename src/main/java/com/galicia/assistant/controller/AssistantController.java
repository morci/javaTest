package com.galicia.assistant.controller;

import com.galicia.assistant.dto.QueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant")
public class AssistantController { 

    private static final Logger log = LoggerFactory.getLogger(AssistantController.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    @Value("${assistant.rabbitmq.queue.request}")
    private String requestQueueName;

    public AssistantController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/query")
    public ResponseEntity<String> receiveQuery(@RequestBody QueryRequest request) {
        
        if (request.getUserId() == null || request.getUserQuery() == null) {
            return new ResponseEntity<>("Invalid input: userId and userQuery are required.", HttpStatus.BAD_REQUEST);
        }
        
        String conversationId = request.getConversationId();
        if (conversationId == null || conversationId.isEmpty()) {
            conversationId = UUID.randomUUID().toString();
            request.setConversationId(conversationId);
        }
        
        log.info("Solicitud asíncrona recibida para Usuario: {} (ConvID: {}). Publicando en cola: {}",
                request.getUserId(), request.getConversationId(), requestQueueName);

        try {
            rabbitTemplate.convertAndSend(requestQueueName, request);
            
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Solicitud aceptada. El procesamiento es asíncrono. ConvID: " + request.getConversationId());

        } catch (Exception e) {
            log.error("Fallo al publicar el mensaje en RabbitMQ: {}", e.getMessage(), e);
            
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Fallo al conectarse con el servicio de mensajería. Intente nuevamente.");
        }
    }
}
