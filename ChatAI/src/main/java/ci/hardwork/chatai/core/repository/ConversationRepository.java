package ci.hardwork.chatai.core.repository;

import ci.hardwork.chatai.core.models.Conversation;
import ci.hardwork.chatai.core.models.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    Page<Conversation> findByUserIdAndStatus(Long userId, ConversationStatus status, Pageable pageable);
    
    List<Conversation> findByUserIdAndStatusOrderByLastActivityAtDesc(Long userId, ConversationStatus status);
    
    Optional<Conversation> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.userId = :userId AND c.status = :status ORDER BY c.lastActivityAt DESC")
    List<Conversation> findRecentConversations(@Param("userId") Long userId, 
                                             @Param("status") ConversationStatus status, 
                                             Pageable pageable);
    
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.userId = :userId AND c.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") ConversationStatus status);
    
    @Modifying
    @Query("UPDATE Conversation c SET c.status = :status WHERE c.userId = :userId AND c.lastActivityAt < :cutoffDate")
    int archiveOldConversations(@Param("userId") Long userId, 
                               @Param("status") ConversationStatus status, 
                               @Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Modifying
    @Query("DELETE FROM Conversation c WHERE c.status = :status AND c.updatedAt < :cutoffDate")
    int deleteOldConversations(@Param("status") ConversationStatus status, 
                              @Param("cutoffDate") LocalDateTime cutoffDate);
    
    @Query("SELECT c FROM Conversation c WHERE c.title LIKE %:searchTerm% OR c.description LIKE %:searchTerm% AND c.userId = :userId AND c.status = :status")
    Page<Conversation> searchConversations(@Param("userId") Long userId, 
                                         @Param("searchTerm") String searchTerm, 
                                         @Param("status") ConversationStatus status, 
                                         Pageable pageable);
}