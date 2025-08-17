package ci.hardwork.chatai.core.dto;

import ci.hardwork.chatai.core.models.enums.ConversationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDto {
    private Long id;
    private Long userId;
    private String title;
    private String description;
    private ConversationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
    private String modelName;
    private String systemPrompt;
    private Double temperature;
    private Integer maxTokens;
    private Integer messageCount;
    private List<MessageDto> recentMessages;
}