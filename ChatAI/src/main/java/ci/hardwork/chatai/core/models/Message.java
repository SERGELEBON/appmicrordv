package ci.hardwork.chatai.core.models;

import ci.hardwork.chatai.core.models.enums.MessageRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isUserMessage() {
        return MessageRole.USER.equals(role);
    }

    public boolean isAssistantMessage() {
        return MessageRole.ASSISTANT.equals(role);
    }

    public boolean isSystemMessage() {
        return MessageRole.SYSTEM.equals(role);
    }
}