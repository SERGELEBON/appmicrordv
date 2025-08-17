package ci.hardwork.chatai.core.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private LocalDateTime lastActivityAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "total_requests")
    private Integer totalRequests = 0;

    @Column(name = "total_tokens_used")
    private Long totalTokensUsed = 0L;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        lastActivityAt = now;
        if (expiresAt == null) {
            expiresAt = now.plusHours(24); // Default 24h session
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastActivityAt = LocalDateTime.now();
    }

    public void incrementRequests() {
        totalRequests = (totalRequests == null ? 0 : totalRequests) + 1;
    }

    public void addTokensUsed(int tokens) {
        totalTokensUsed = (totalTokensUsed == null ? 0L : totalTokensUsed) + tokens;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void extendSession(int hours) {
        expiresAt = LocalDateTime.now().plusHours(hours);
    }
}