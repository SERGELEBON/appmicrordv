package ci.hardwork.chatai.core.service.impl;

import ci.hardwork.chatai.core.models.UserSession;
import ci.hardwork.chatai.core.repository.UserSessionRepository;
import ci.hardwork.chatai.core.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserSessionServiceImpl implements UserSessionService {

    private final UserSessionRepository sessionRepository;

    @Value("${app.rate-limit.requests-per-minute:60}")
    private Integer requestsPerMinute;

    @Value("${app.rate-limit.burst-capacity:10}")
    private Integer burstCapacity;

    @Override
    public UserSession createSession(Long userId, HttpServletRequest request) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setSessionId(UUID.randomUUID().toString());
        session.setIpAddress(getClientIpAddress(request));
        session.setUserAgent(request.getHeader("User-Agent"));
        session.setExpiresAt(LocalDateTime.now().plusHours(24));
        
        session = sessionRepository.save(session);
        
        log.info("Created new session {} for user {}", session.getSessionId(), userId);
        return session;
    }

    @Override
    public UserSession getActiveSession(String sessionId) {
        return sessionRepository.findBySessionIdAndIsActiveTrue(sessionId)
                .orElseThrow(() -> new RuntimeException("Active session not found"));
    }

    @Override
    public void validateRateLimit(Long userId) {
        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        Long recentRequests = sessionRepository.getTotalRequestsByUserSince(userId, oneMinuteAgo);
        
        if (recentRequests != null && recentRequests >= requestsPerMinute) {
            throw new RuntimeException("Rate limit exceeded. Please wait before making more requests.");
        }
        
        // Additional burst protection
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(10);
        Long burstRequests = sessionRepository.getTotalRequestsByUserSince(userId, tenSecondsAgo);
        
        if (burstRequests != null && burstRequests >= burstCapacity) {
            throw new RuntimeException("Too many requests in short time. Please slow down.");
        }
    }

    @Override
    public void recordRequest(Long userId, Integer tokensUsed) {
        sessionRepository.findByUserIdAndIsActiveTrueOrderByLastActivityAtDesc(userId)
                .stream()
                .findFirst()
                .ifPresent(session -> {
                    session.incrementRequests();
                    if (tokensUsed != null) {
                        session.addTokensUsed(tokensUsed);
                    }
                    sessionRepository.save(session);
                });
    }

    @Override
    public void deactivateSession(String sessionId) {
        sessionRepository.deactivateSession(sessionId);
        log.info("Deactivated session {}", sessionId);
    }

    @Override
    public void deactivateAllUserSessions(Long userId) {
        sessionRepository.deactivateAllUserSessions(userId);
        log.info("Deactivated all sessions for user {}", userId);
    }

    @Override
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime inactiveThreshold = now.minusHours(24);
        
        int deactivated = sessionRepository.deactivateExpiredSessions(now, inactiveThreshold);
        
        if (deactivated > 0) {
            log.info("Deactivated {} expired sessions", deactivated);
        }
    }

    @Override
    public Long getUserTokenUsage(Long userId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Long tokens = sessionRepository.getTotalTokensByUserSince(userId, since);
        return tokens != null ? tokens : 0L;
    }

    @Override
    public Long getUserRequestCount(Long userId, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Long requests = sessionRepository.getTotalRequestsByUserSince(userId, since);
        return requests != null ? requests : 0L;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}