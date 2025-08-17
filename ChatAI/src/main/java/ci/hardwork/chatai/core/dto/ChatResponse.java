package ci.hardwork.chatai.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String message;
    private Long conversationId;
    private Long messageId;
    private String model;
    private Integer tokenCount;
    private Long responseTimeMs;
    private LocalDateTime timestamp;
    private Boolean isComplete = true;
}