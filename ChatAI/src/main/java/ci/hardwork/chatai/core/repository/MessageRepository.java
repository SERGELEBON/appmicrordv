package ci.hardwork.chatai.core.repository;

import ci.hardwork.chatai.core.models.Message;
import ci.hardwork.chatai.core.models.enums.MessageRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByConversationIdOrderByCreatedAtAsc(Long conversationId);
    
    Page<Message> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId ORDER BY m.createdAt DESC")
    List<Message> findLastMessages(@Param("conversationId") Long conversationId, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId")
    Long countByConversationId(@Param("conversationId") Long conversationId);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.userId = :userId AND m.role = :role AND m.createdAt >= :fromDate")
    List<Message> findUserMessagesSince(@Param("userId") Long userId, 
                                       @Param("role") MessageRole role, 
                                       @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT SUM(m.tokenCount) FROM Message m WHERE m.conversation.userId = :userId AND m.createdAt >= :fromDate")
    Long getTotalTokensUsedSince(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT AVG(m.responseTimeMs) FROM Message m WHERE m.role = :role AND m.responseTimeMs IS NOT NULL AND m.createdAt >= :fromDate")
    Double getAverageResponseTime(@Param("role") MessageRole role, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.content LIKE %:searchTerm%")
    List<Message> searchInConversation(@Param("conversationId") Long conversationId, 
                                      @Param("searchTerm") String searchTerm);
    
    void deleteByConversationId(Long conversationId);
}