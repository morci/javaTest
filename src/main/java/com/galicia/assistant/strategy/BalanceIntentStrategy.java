package com.galicia.assistant.strategy;

import com.galicia.assistant.dto.QueryRequest;
import com.galicia.assistant.dto.QueryResponse;
import org.springframework.stereotype.Component;


@Component
public class BalanceIntentStrategy implements IntentStrategy {

    @Override
    public boolean matches(String userQuery) {
        String lowerQuery = userQuery.toLowerCase();
        return lowerQuery.contains("saldo") || lowerQuery.contains("cuánto tengo");
    }

    @Override
    public QueryResponse process(QueryRequest request, String conversationId ) {
        String intent = "INTENT_CHECK_BALANCE";
        String responseText = "Para consultar su saldo, necesitaría verificar su identidad. ";
        String status = "OK";
        
        QueryResponse response = new QueryResponse(request.getUserId(), intent, responseText, status);
        response.setConversationId(conversationId);
        return response;
    }
}
