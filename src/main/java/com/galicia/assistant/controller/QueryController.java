package com.galicia.assistant.controller;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import com.galicia.assistant.service.IntentProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assistant")
public class QueryController {

    private static final Logger log = LoggerFactory.getLogger(QueryController.class);
    
    private final IntentProcessingService intentProcessingService;
    
    public QueryController(IntentProcessingService intentProcessingService) {
        this.intentProcessingService = intentProcessingService;
    }
    
    @PostMapping("/query")
    public ResponseEntity<?> receiveQuery(@RequestBody QueryRequest request) {
        
        if (request.getUserId() == null || request.getUserQuery() == null) {
            return new ResponseEntity<>("Invalid input: userId and userQuery are required.", HttpStatus.BAD_REQUEST);
        }

        log.info("Consulta síncrona recibida para Usuario: {}", request.getUserId());

        try {
            QueryResponse response = intentProcessingService.processQuery(request);
            
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Fallo durante el procesamiento de la consulta: {}", e.getMessage(), e);
            QueryResponse errorResponse = new QueryResponse(
                    request.getUserId(),
                    "ERROR",
                    "Internal system error during processing: " + e.getMessage(),
                    "ERROR"
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
