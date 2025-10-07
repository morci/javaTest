package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;

public interface IntentStrategy {
    
    boolean matches(String userQuery);
    QueryResponse process(QueryRequest request, String conversationId);
}
