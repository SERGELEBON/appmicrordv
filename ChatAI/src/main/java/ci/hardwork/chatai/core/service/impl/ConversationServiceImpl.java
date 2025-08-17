package ci.hardwork.chatai.core.service.impl;

import ci.hardwork.chatai.core.dto.ConversationDto;
import ci.hardwork.chatai.core.dto.CreateConversationRequest;
import ci.hardwork.chatai.core.dto.MessageDto;
import ci.hardwork.chatai.core.mapper.ConversationMapper;
import ci.hardwork.chatai.core.mapper.MessageMapper;
import ci.hardwork.chatai.core.models.Conversation;
import ci.hardwork.chatai.core.models.Message;
import ci.hardwork.chatai.core.models.enums.ConversationStatus;
import ci.hardwork.chatai.core.repository.ConversationRepository;
import ci.hardwork.chatai.core.repository.MessageRepository;
import ci.hardwork.chatai.core.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Value("${app.ai.conversation.auto-cleanup-days:30}")
    private Integer autoCleanupDays;

    @Value("${app.ai.ollama.model:llama3}")
    private String defaultModel;

    @Value("${app.ai.ollama.chat.options.temperature:0.7}")
    private Double defaultTemperature;

    @Value("${app.ai.ollama.chat.options.max-tokens:2000}")
    private Integer defaultMaxTokens;

    @Override
    public ConversationDto createConversation(Long userId, CreateConversationRequest request) {
        Conversation conversation = new Conversation();
        conversation.setUserId(userId);
        conversation.setTitle(request.getTitle());
        conversation.setDescription(request.getDescription());
        conversation.setSystemPrompt(request.getSystemPrompt());
        conversation.setModelName(request.getModel() != null ? request.getModel() : defaultModel);
        conversation.setTemperature(request.getTemperature() != null ? request.getTemperature() : defaultTemperature);
        conversation.setMaxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : defaultMaxTokens);
        
        conversation = conversationRepository.save(conversation);
        
        log.info("Création d'une nouvelle conversation {} pour l'utilisateur {}", conversation.getId(), userId);
        
        return conversationMapper.toDto(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDto getConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        
        ConversationDto dto = conversationMapper.toDto(conversation);
        
        // Ajouter les derniers messages
        List<Message> recentMessages = messageRepository.findLastMessages(conversationId, 
                PageRequest.of(0, 10));
        dto.setRecentMessages(messageMapper.toDto(recentMessages));
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationDto> getUserConversations(Long userId, ConversationStatus status, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.findByUserIdAndStatus(userId, status, pageable);
        return conversations.map(conversationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDto> getConversationMessages(Long conversationId, Long userId, Pageable pageable) {
        // Vérifier que l'utilisateur a accès à cette conversation
        conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        
        Page<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
        return messageMapper.toDto(messages.getContent());
    }

    @Override
    public ConversationDto updateConversation(Long conversationId, Long userId, CreateConversationRequest request) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        
        conversation.setTitle(request.getTitle());
        conversation.setDescription(request.getDescription());
        conversation.setSystemPrompt(request.getSystemPrompt());
        
        if (request.getModel() != null) {
            conversation.setModelName(request.getModel());
        }
        if (request.getTemperature() != null) {
            conversation.setTemperature(request.getTemperature());
        }
        if (request.getMaxTokens() != null) {
            conversation.setMaxTokens(request.getMaxTokens());
        }
        
        conversation = conversationRepository.save(conversation);
        
        log.info("Mise à jour de la conversation {} pour l'utilisateur {}", conversationId, userId);
        
        return conversationMapper.toDto(conversation);
    }

    @Override
    public void deleteConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        
        conversationRepository.delete(conversation);
        
        log.info("Suppression de la conversation {} pour l'utilisateur {}", conversationId, userId);
    }

    @Override
    public void archiveConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        
        conversation.setStatus(ConversationStatus.ARCHIVED);
        conversationRepository.save(conversation);
        
        log.info("Archivage de la conversation {} pour l'utilisateur {}", conversationId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationDto> searchConversations(Long userId, String searchTerm, Pageable pageable) {
        Page<Conversation> conversations = conversationRepository.searchConversations(
                userId, searchTerm, ConversationStatus.ACTIVE, pageable);
        return conversations.map(conversationMapper::toDto);
    }

    @Override
    public void cleanupOldConversations() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(autoCleanupDays);
        
        // Archiver les anciennes conversations actives
        int archived = conversationRepository.archiveOldConversations(
                null, ConversationStatus.ARCHIVED, cutoffDate.minusDays(30));
        
        // Supprimer les très anciennes conversations archivées
        int deleted = conversationRepository.deleteOldConversations(
                ConversationStatus.DELETED, cutoffDate.minusDays(90));
        
        if (archived > 0 || deleted > 0) {
            log.info("Nettoyage automatique: {} conversations archivées, {} supprimées", archived, deleted);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserConversationCount(Long userId) {
        return conversationRepository.countByUserIdAndStatus(userId, ConversationStatus.ACTIVE);
    }
}