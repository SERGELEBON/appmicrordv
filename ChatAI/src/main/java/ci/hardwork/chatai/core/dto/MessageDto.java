package ci.hardwork.chatai.core.dto;

import ci.hardwork.chatai.core.models.enums.MessageRole;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDto {
    private Long id;
    private Long conversationId;
    private MessageRole role;
    private String content;
    private LocalDateTime createdAt;
    private Integer tokenCount;
    private Long responseTimeMs;
    private String modelName;
}