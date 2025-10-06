package com.galicia.assistant.repository;

import com.galicia.assistant.entity.ConversationEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de Integración dedicada a la capa de Persistencia (Repository).
 * Usa @DataJpaTest para un contexto ligero de JPA/H2.
 */
@DataJpaTest
class ConversationRepositoryIntegrationTest {

    // Inyecta el repositorio real (no mockeado)
    @Autowired
    private ConversationRepository conversationRepository;

    @Test
    void conversationEntry_shouldBeSavedAndFoundById() {

        // ARRANGE: Crear una entrada de conversación
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

        // ACT: Guardar en la base de datos H2 en memoria
        ConversationEntry savedEntry = conversationRepository.save(newEntry);

        // ASSERT 1: Verificar que el objeto guardado no es nulo y tiene ID
        assertNotNull(savedEntry.getId());
        assertEquals(conversationId, savedEntry.getConversationId());

        // ACT 2: Buscar por el ID de conversación (no el ID autogenerado de la tabla)
        ConversationEntry foundEntry = conversationRepository.findByConversationId(conversationId).orElse(null);

        // ASSERT 2: Verificar que se recuperó correctamente
        assertNotNull(foundEntry);
        assertEquals("Test Response", foundEntry.getAssistantResponse());
    }

    @Test
    void conversationEntry_shouldIncrementCount() {

        // ARRANGE & ACT: Contar, guardar, y volver a contar
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

        // ASSERT: Verificar que el conteo se incrementó
        assertEquals(initialCount + 1, finalCount);
    }
}

// Nota: Necesitarías un método 'findByConversationId' en tu ConversationRepository
// Si no lo tienes, puedes probar con 'count()' como en el segundo test.
