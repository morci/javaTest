package com.galicia.assistant.repository;

import com.galicia.assistant.entity.ConversationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationEntry, Long> {
    
    List<ConversationEntry> findByUserIdOrderByTimestampDesc(String userId);
    
    Optional<ConversationEntry> findByConversationId(String conversationId);
}
