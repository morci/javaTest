package com.galicia.assistant.repository;

import com.galicia.assistant.entity.ConversationEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ConversationRepositoryIntegrationTest {
    
    @Autowired
    private ConversationRepository conversationRepository;

    @Test
    void conversationEntry_shouldBeSavedAndFoundById() {
        
        String conversationId = UUID.randomUUID().toString();
        ConversationEntry newEntry = new ConversationEntry(
                conversationId,
                UUID.randomUUID().toString(),
                "Test Query",
                "INTENT_TEST",
                "Test Response",
                Instant.now(),
                "OK"
        );
        
        ConversationEntry savedEntry = conversationRepository.save(newEntry);
        
        assertNotNull(savedEntry.getId());
        assertEquals(conversationId, savedEntry.getConversationId());
        
        ConversationEntry foundEntry = conversationRepository.findByConversationId(conversationId).orElse(null);
        
        assertNotNull(foundEntry);
        assertEquals("Test Response", foundEntry.getAssistantResponse());
    }

    @Test
    void conversationEntry_shouldIncrementCount() {
        
        long initialCount = conversationRepository.count();

        ConversationEntry newEntry = new ConversationEntry(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Another Test Query",
                "INTENT_TEST_2",
                "Test Response 2",
                Instant.now(),
                "OK"
        );
        conversationRepository.save(newEntry);

        long finalCount = conversationRepository.count();
        
        assertEquals(initialCount + 1, finalCount);
    }
}
