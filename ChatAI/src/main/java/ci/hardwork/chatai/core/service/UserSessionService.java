package ci.hardwork.chatai.core.service;

import ci.hardwork.chatai.core.models.UserSession;
import jakarta.servlet.http.HttpServletRequest;

public interface UserSessionService {
    
    UserSession createSession(Long userId, HttpServletRequest request);
    
    UserSession getActiveSession(String sessionId);
    
    void validateRateLimit(Long userId);
    
    void recordRequest(Long userId, Integer tokensUsed);
    
    void deactivateSession(String sessionId);
    
    void deactivateAllUserSessions(Long userId);
    
    void cleanupExpiredSessions();
    
    Long getUserTokenUsage(Long userId, int hours);
    
    Long getUserRequestCount(Long userId, int hours);
}