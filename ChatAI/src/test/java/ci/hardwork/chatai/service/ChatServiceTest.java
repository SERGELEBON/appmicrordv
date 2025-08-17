package ci.hardwork.chatai.service;

import ci.hardwork.chatai.core.dto.ChatRequest;
import ci.hardwork.chatai.core.dto.ChatResponse;
import ci.hardwork.chatai.core.models.Conversation;
import ci.hardwork.chatai.core.models.Message;
import ci.hardwork.chatai.core.models.enums.MessageRole;
import ci.hardwork.chatai.core.repository.ConversationRepository;
import ci.hardwork.chatai.core.repository.MessageRepository;
import ci.hardwork.chatai.core.service.ConversationService;
import ci.hardwork.chatai.core.service.UserSessionService;
import ci.hardwork.chatai.core.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatModel chatModel;

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationService conversationService;

    @Mock
    private UserSessionService userSessionService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private ChatRequest chatRequest;
    private Conversation conversation;
    private Message userMessage;
    private Message assistantMessage;

    @BeforeEach
    void setUp() {
        // Configuration des valeurs par défaut
        ReflectionTestUtils.setField(chatService, "defaultModel", "llama3");
        ReflectionTestUtils.setField(chatService, "defaultTemperature", 0.7);
        ReflectionTestUtils.setField(chatService, "defaultMaxTokens", 2000);
        ReflectionTestUtils.setField(chatService, "maxHistoryMessages", 50);

        // Préparation des objets de test
        chatRequest = new ChatRequest();
        chatRequest.setMessage("Bonjour, comment allez-vous ?");
        
        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setUserId(123L);
        conversation.setTitle("Test Conversation");
        conversation.setModelName("llama3");

        userMessage = new Message();
        userMessage.setId(1L);
        userMessage.setConversation(conversation);
        userMessage.setRole(MessageRole.USER);
        userMessage.setContent("Bonjour, comment allez-vous ?");

        assistantMessage = new Message();
        assistantMessage.setId(2L);
        assistantMessage.setConversation(conversation);
        assistantMessage.setRole(MessageRole.ASSISTANT);
        assistantMessage.setContent("Bonjour ! Je vais très bien, merci. Comment puis-je vous aider aujourd'hui ?");
    }

    @Test
    void testSendMessage_NouvelleConversation_Succes() {
        // Given
        Long userId = 123L;
        
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(messageRepository.save(any(Message.class)))
                .thenReturn(userMessage)
                .thenReturn(assistantMessage);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of(userMessage));

        // Mock de la réponse IA
        org.springframework.ai.chat.model.ChatResponse aiResponse = mock(org.springframework.ai.chat.model.ChatResponse.class);
        List<Generation> generations = List.of(mock(Generation.class));
        when(aiResponse.getResults()).thenReturn(generations);
        
        Generation generation = generations.get(0);
        org.springframework.ai.chat.messages.AssistantMessage assistantMsg = 
                new org.springframework.ai.chat.messages.AssistantMessage("Bonjour ! Je vais très bien, merci. Comment puis-je vous aider aujourd'hui ?");
        when(generation.getOutput()).thenReturn(assistantMsg);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(aiResponse);

        // When
        ChatResponse response = chatService.sendMessage(userId, chatRequest);

        // Then
        assertNotNull(response);
        assertEquals("Bonjour ! Je vais très bien, merci. Comment puis-je vous aider aujourd'hui ?", response.getMessage());
        assertEquals(conversation.getId(), response.getConversationId());
        assertTrue(response.getIsComplete());
        assertNotNull(response.getTimestamp());

        verify(conversationRepository).save(any(Conversation.class));
        verify(messageRepository, times(2)).save(any(Message.class));
        verify(userSessionService).recordRequest(eq(userId), isNull());
    }

    @Test
    void testSendMessage_ConversationExistante_Succes() {
        // Given
        Long userId = 123L;
        chatRequest.setConversationId(1L);
        
        when(conversationRepository.findByIdAndUserId(1L, userId))
                .thenReturn(Optional.of(conversation));
        when(messageRepository.save(any(Message.class)))
                .thenReturn(userMessage)
                .thenReturn(assistantMessage);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of(userMessage));

        // Mock de la réponse IA
        org.springframework.ai.chat.model.ChatResponse aiResponse = mock(org.springframework.ai.chat.model.ChatResponse.class);
        List<Generation> generations = List.of(mock(Generation.class));
        when(aiResponse.getResults()).thenReturn(generations);
        
        Generation generation = generations.get(0);
        org.springframework.ai.chat.messages.AssistantMessage assistantMsg = 
                new org.springframework.ai.chat.messages.AssistantMessage("Parfait ! Comment puis-je vous aider ?");
        when(generation.getOutput()).thenReturn(assistantMsg);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(aiResponse);

        // When
        ChatResponse response = chatService.sendMessage(userId, chatRequest);

        // Then
        assertNotNull(response);
        assertEquals("Parfait ! Comment puis-je vous aider ?", response.getMessage());
        assertEquals(1L, response.getConversationId());

        verify(conversationRepository, never()).save(any(Conversation.class));
        verify(conversationRepository).findByIdAndUserId(1L, userId);
    }

    @Test
    void testSendMessage_ConversationIntrouvable_ExceptionLevee() {
        // Given
        Long userId = 123L;
        chatRequest.setConversationId(999L);
        
        when(conversationRepository.findByIdAndUserId(999L, userId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> chatService.sendMessage(userId, chatRequest));
        
        assertEquals("Conversation not found", exception.getMessage());
        
        verify(conversationRepository).findByIdAndUserId(999L, userId);
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testSendMessage_ErreurIA_ExceptionLevee() {
        // Given
        Long userId = 123L;
        
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(messageRepository.save(any(Message.class))).thenReturn(userMessage);
        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(anyLong()))
                .thenReturn(List.of(userMessage));
        
        when(chatModel.call(any(Prompt.class)))
                .thenThrow(new RuntimeException("Erreur de connexion au modèle IA"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> chatService.sendMessage(userId, chatRequest));
        
        assertTrue(exception.getMessage().contains("Failed to generate AI response"));
        
        verify(chatModel, times(2)).call(any(Prompt.class)); // 1 for title generation + 1 for message
    }

    @Test
    void testGenerateTitle_MessageCourt_TitreGenere() {
        // Given
        String message = "Expliquez-moi la photosynthèse";
        
        org.springframework.ai.chat.model.ChatResponse aiResponse = mock(org.springframework.ai.chat.model.ChatResponse.class);
        List<Generation> generations = List.of(mock(Generation.class));
        when(aiResponse.getResults()).thenReturn(generations);
        
        Generation generation = generations.get(0);
        org.springframework.ai.chat.messages.AssistantMessage assistantMsg = 
                new org.springframework.ai.chat.messages.AssistantMessage("Explication de la photosynthèse");
        when(generation.getOutput()).thenReturn(assistantMsg);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(aiResponse);

        // When
        String title = chatService.generateTitle(message);

        // Then
        assertNotNull(title);
        assertEquals("Explication de la photosynthèse", title);
        
        verify(chatModel).call(any(Prompt.class));
    }

    @Test
    void testGenerateTitle_MessageLong_TitreTronque() {
        // Given
        String longMessage = "Ceci est un très long message qui dépasse largement les 50 caractères autorisés pour un titre";
        
        org.springframework.ai.chat.model.ChatResponse aiResponse = mock(org.springframework.ai.chat.model.ChatResponse.class);
        List<Generation> generations = List.of(mock(Generation.class));
        when(aiResponse.getResults()).thenReturn(generations);
        
        Generation generation = generations.get(0);
        org.springframework.ai.chat.messages.AssistantMessage assistantMsg = 
                new org.springframework.ai.chat.messages.AssistantMessage("Ceci est un très long titre qui dépasse vraiment les limites autorisées pour les titres");
        when(generation.getOutput()).thenReturn(assistantMsg);
        
        when(chatModel.call(any(Prompt.class))).thenReturn(aiResponse);

        // When
        String title = chatService.generateTitle(longMessage);

        // Then
        assertNotNull(title);
        assertTrue(title.length() <= 50);
    }

    @Test
    void testGenerateTitle_ErreurIA_TitreParDefaut() {
        // Given
        String message = "Test message";
        
        when(chatModel.call(any(Prompt.class)))
                .thenThrow(new RuntimeException("Erreur IA"));

        // When
        String title = chatService.generateTitle(message);

        // Then
        assertEquals("New Conversation", title);
    }

    @Test
    void testValidateRateLimit_AppelServiceSession() {
        // Given
        Long userId = 123L;
        
        doNothing().when(userSessionService).validateRateLimit(userId);

        // When
        chatService.validateRateLimit(userId);

        // Then
        verify(userSessionService).validateRateLimit(userId);
    }
}