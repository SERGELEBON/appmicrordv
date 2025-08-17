package ci.hardwork.chatai.core.service;

import ci.hardwork.chatai.core.dto.ChatRequest;
import ci.hardwork.chatai.core.dto.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {
    
    ChatResponse sendMessage(Long userId, ChatRequest request);
    
    SseEmitter sendMessageStream(Long userId, ChatRequest request);
    
    String generateTitle(String firstMessage);
    
    boolean isModelAvailable(String modelName);
    
    void validateRateLimit(Long userId);
}