package ci.hardwork.chatai.core.repository;

import ci.hardwork.chatai.core.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    Optional<UserSession> findBySessionIdAndIsActiveTrue(String sessionId);
    
    List<UserSession> findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(Long userId);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.sessionId = :sessionId")
    void deactivateSession(@Param("sessionId") String sessionId);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.userId = :userId")
    void deactivateAllUserSessions(@Param("userId") Long userId);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.expiresAt < :now OR s.lastActivityAt < :inactiveThreshold")
    int deactivateExpiredSessions(@Param("now") LocalDateTime now, 
                                 @Param("inactiveThreshold") LocalDateTime inactiveThreshold);
    
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.userId = :userId AND s.isActive = true")
    Long countActiveSessionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT s FROM UserSession s WHERE s.userId = :userId AND s.createdAt >= :fromDate")
    List<UserSession> findUserSessionsSince(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT SUM(s.totalRequests) FROM UserSession s WHERE s.userId = :userId AND s.createdAt >= :fromDate")
    Long getTotalRequestsByUserSince(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT SUM(s.totalTokensUsed) FROM UserSession s WHERE s.userId = :userId AND s.createdAt >= :fromDate")
    Long getTotalTokensByUserSince(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);
}