package com.galicia.assistant.repository;

import com.galicia.assistant.entity.ConversationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Esta interfaz hereda automáticamente métodos CRUD (save, findById, findAll, etc.)
@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntry, Long> {

    // Método de consulta personalizado para buscar el historial de un usuario específico.
    // Spring Data JPA lo implementa automáticamente por el nombre del método.
    List<ConversationEntry> findByUserIdOrderByTimestampDesc(String userId);

    // Método para buscar una entrada por el ID de conversación (útil para trazabilidad)
    Optional<ConversationEntry> findByConversationId(String conversationId);
}
