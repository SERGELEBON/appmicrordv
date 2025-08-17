package ci.hardwork.chatai.core.service;

import ci.hardwork.chatai.core.dto.ConversationDto;
import ci.hardwork.chatai.core.dto.CreateConversationRequest;
import ci.hardwork.chatai.core.dto.MessageDto;
import ci.hardwork.chatai.core.models.enums.ConversationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConversationService {
    
    ConversationDto createConversation(Long userId, CreateConversationRequest request);
    
    ConversationDto getConversation(Long conversationId, Long userId);
    
    Page<ConversationDto> getUserConversations(Long userId, ConversationStatus status, Pageable pageable);
    
    List<MessageDto> getConversationMessages(Long conversationId, Long userId, Pageable pageable);
    
    ConversationDto updateConversation(Long conversationId, Long userId, CreateConversationRequest request);
    
    void deleteConversation(Long conversationId, Long userId);
    
    void archiveConversation(Long conversationId, Long userId);
    
    Page<ConversationDto> searchConversations(Long userId, String searchTerm, Pageable pageable);
    
    void cleanupOldConversations();
    
    Long getUserConversationCount(Long userId);
}