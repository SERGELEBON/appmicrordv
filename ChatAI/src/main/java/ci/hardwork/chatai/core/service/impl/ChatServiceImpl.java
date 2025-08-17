package ci.hardwork.chatai.core.service.impl;

import ci.hardwork.chatai.core.dto.ChatRequest;
import ci.hardwork.chatai.core.dto.ChatResponse;
import ci.hardwork.chatai.core.models.Conversation;
import ci.hardwork.chatai.core.models.Message;
import ci.hardwork.chatai.core.models.enums.MessageRole;
import ci.hardwork.chatai.core.repository.ConversationRepository;
import ci.hardwork.chatai.core.repository.MessageRepository;
import ci.hardwork.chatai.core.service.ChatService;
import ci.hardwork.chatai.core.service.ConversationService;
import ci.hardwork.chatai.core.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatModel chatModel;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final UserSessionService userSessionService;

    @Value("${app.ai.ollama.model:llama3}")
    private String defaultModel;

    @Value("${app.ai.ollama.chat.options.temperature:0.7}")
    private Double defaultTemperature;

    @Value("${app.ai.ollama.chat.options.max-tokens:2000}")
    private Integer defaultMaxTokens;

    @Value("${app.ai.conversation.max-history:50}")
    private Integer maxHistoryMessages;

    @Override
    public ChatResponse sendMessage(Long userId, ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        validateRateLimit(userId);
        
        Conversation conversation = getOrCreateConversation(userId, request);
        
        // Save user message
        Message userMessage = createMessage(conversation, MessageRole.USER, request.getMessage());
        userMessage = messageRepository.save(userMessage);
        
        try {
            // Validate model availability first
            String modelToUse = request.getModel() != null ? request.getModel() : defaultModel;
            if (!isModelAvailable(modelToUse)) {
                throw new RuntimeException("Model " + modelToUse + " is not available. Please check Ollama service and ensure the model is downloaded.");
            }
            
            // Prepare conversation history
            List<Message> history = getConversationHistory(conversation.getId());
            
            // Create AI prompt with history
            Prompt prompt = buildPrompt(history, request);
            
            // Get AI response with proper error handling
            org.springframework.ai.chat.model.ChatResponse aiResponse = chatModel.call(prompt);
            
            if (aiResponse == null || aiResponse.getResults().isEmpty()) {
                throw new RuntimeException("Ollama returned empty response. Service may be unavailable.");
            }
            
            String responseContent = aiResponse.getResults().get(0).getOutput().getText();
            
            if (responseContent == null || responseContent.trim().isEmpty()) {
                throw new RuntimeException("Ollama returned empty content. Please try again.");
            }
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            // Save assistant message
            Message assistantMessage = createMessage(conversation, MessageRole.ASSISTANT, responseContent);
            assistantMessage.setResponseTimeMs(responseTime);
            assistantMessage.setModelName(request.getModel() != null ? request.getModel() : defaultModel);
            // tokenCount is already set in createMessage for ASSISTANT role
            assistantMessage = messageRepository.save(assistantMessage);
            
            // Update session statistics
            userSessionService.recordRequest(userId, assistantMessage.getTokenCount());
            
            log.info("Chat response generated for user {} in conversation {} ({}ms)", 
                    userId, conversation.getId(), responseTime);
            
            return new ChatResponse(
                responseContent,
                conversation.getId(),
                assistantMessage.getId(),
                assistantMessage.getModelName(),
                assistantMessage.getTokenCount(),
                responseTime,
                LocalDateTime.now(),
                true
            );
            
        } catch (Exception e) {
            log.error("Error generating chat response for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate AI response: " + e.getMessage());
        }
    }

    @Override
    public SseEmitter sendMessageStream(Long userId, ChatRequest request) {
        SseEmitter emitter = new SseEmitter(60000L); // 60 second timeout
        
        CompletableFuture.runAsync(() -> {
            try {
                ChatResponse response = sendMessage(userId, request);
                emitter.send(SseEmitter.event().name("message").data(response));
                emitter.complete();
            } catch (Exception e) {
                log.error("Error in streaming response: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @Override
    @Cacheable(value = "titleCache", key = "#firstMessage.hashCode()")
    public String generateTitle(String firstMessage) {
        try {
            String titlePrompt = "Generate a concise title (max 50 characters) for a conversation that starts with: \"" 
                    + firstMessage + "\". Return only the title, no quotes or explanations.";
            
            Prompt prompt = new Prompt(titlePrompt);
            
            org.springframework.ai.chat.model.ChatResponse response = chatModel.call(prompt);
            String title = response.getResults().get(0).getOutput().getText().trim();
            
            // Clean up the title
            title = title.replaceAll("^\"|\"$", ""); // Remove quotes
            title = title.substring(0, Math.min(title.length(), 50)); // Limit length
            
            return title.isEmpty() ? "New Conversation" : title;
            
        } catch (Exception e) {
            log.warn("Failed to generate title, using default: {}", e.getMessage());
            return "New Conversation";
        }
    }

    @Override
    @Cacheable(value = "modelCache", key = "#modelName")
    public boolean isModelAvailable(String modelName) {
        try {
            // Try a simple test call
            Prompt testPrompt = new Prompt("Hello");
            org.springframework.ai.chat.model.ChatResponse response = chatModel.call(testPrompt);
            return response != null && !response.getResults().isEmpty();
        } catch (Exception e) {
            log.warn("Model {} not available: {}", modelName, e.getMessage());
            return false;
        }
    }

    @Override
    public void validateRateLimit(Long userId) {
        userSessionService.validateRateLimit(userId);
    }

    private Conversation getOrCreateConversation(Long userId, ChatRequest request) {
        if (request.getConversationId() != null) {
            return conversationRepository.findByIdAndUserId(request.getConversationId(), userId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));
        }
        
        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(generateTitle(request.getMessage()));
        conversation.setModelName(request.getModel() != null ? request.getModel() : defaultModel);
        conversation.setSystemPrompt(request.getSystemPrompt());
        conversation.setTemperature(request.getTemperature() != null ? request.getTemperature() : defaultTemperature);
        conversation.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : defaultMaxTokens);
        
        return conversationRepository.save(conversation);
    }

    private Message createMessage(Conversation conversation, MessageRole role, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);
        
        if (role == MessageRole.USER || role == MessageRole.ASSISTANT) {
            message.setTokenCount(estimateTokenCount(content));
        }
        
        return message;
    }

    private List<Message> getConversationHistory(Long conversationId) {
        List<Message> allMessages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        
        // Limit history to avoid token limits
        if (allMessages.size() > maxHistoryMessages) {
            return allMessages.subList(allMessages.size() - maxHistoryMessages, allMessages.size());
        }
        
        return allMessages;
    }

    private Prompt buildPrompt(List<Message> history, ChatRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        
        // Add system prompt if provided
        if (request.getSystemPrompt() != null && !request.getSystemPrompt().trim().isEmpty()) {
            promptBuilder.append("System: ").append(request.getSystemPrompt()).append("\n\n");
        }
        
        // Add conversation history
        for (Message msg : history) {
            String rolePrefix = switch (msg.getRole()) {
                case USER -> "Human: ";
                case ASSISTANT -> "Assistant: ";
                case SYSTEM -> "System: ";
            };
            promptBuilder.append(rolePrefix).append(msg.getContent()).append("\n\n");
        }
        
        // Add current user message
        promptBuilder.append("Human: ").append(request.getMessage()).append("\n\nAssistant: ");
        
        return new Prompt(promptBuilder.toString());
    }

    private Integer estimateTokenCount(String text) {
        // Simple estimation: ~4 characters per token
        return Math.max(1, text.length() / 4);
    }
}