package ci.hardwork.chatai.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {
    
    @NotBlank(message = "Message content is required")
    @Size(max = 10000, message = "Message content cannot exceed 10000 characters")
    private String message;
    
    private Long conversationId;
    
    private String systemPrompt;
    
    private Double temperature;
    
    private Integer maxTokens;
    
    private String model;
    
    private Boolean streamResponse = false;
}