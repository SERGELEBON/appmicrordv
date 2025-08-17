package ci.hardwork.chatai.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateConversationRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    
    @Size(max = 2000, message = "System prompt cannot exceed 2000 characters")
    private String systemPrompt;
    
    private String model;
    private Double temperature;
    private Integer maxTokens;
}