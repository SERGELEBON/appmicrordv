package ci.hardwork.chatai.service;

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
import ci.hardwork.chatai.core.service.impl.ConversationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private ConversationServiceImpl conversationService;

    private CreateConversationRequest createRequest;
    private Conversation conversation;
    private ConversationDto conversationDto;
    private Message message;
    private MessageDto messageDto;

    @BeforeEach
    void setUp() {
        // Configuration des valeurs par défaut
        ReflectionTestUtils.setField(conversationService, "autoCleanupDays", 30);
        ReflectionTestUtils.setField(conversationService, "defaultModel", "llama3");
        ReflectionTestUtils.setField(conversationService, "defaultTemperature", 0.7);
        ReflectionTestUtils.setField(conversationService, "defaultMaxTokens", 2000);

        // Préparation des objets de test
        createRequest = new CreateConversationRequest();
        createRequest.setTitle("Conversation de test");
        createRequest.setDescription("Description de test");
        createRequest.setSystemPrompt("Tu es un assistant IA serviable");

        conversation = new Conversation();
        conversation.setId(1L);
        conversation.setUserId(123L);
        conversation.setTitle("Conversation de test");
        conversation.setDescription("Description de test");
        conversation.setStatus(ConversationStatus.ACTIVE);

        conversationDto = new ConversationDto();
        conversationDto.setId(1L);
        conversationDto.setUserId(123L);
        conversationDto.setTitle("Conversation de test");
        conversationDto.setStatus(ConversationStatus.ACTIVE);

        message = new Message();
        message.setId(1L);
        message.setConversation(conversation);
        message.setContent("Message de test");

        messageDto = new MessageDto();
        messageDto.setId(1L);
        messageDto.setConversationId(1L);
        messageDto.setContent("Message de test");
    }

    @Test
    void testCreateConversation_DonneesValides_ConversationCreee() {
        // Given
        Long userId = 123L;
        
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);

        // When
        ConversationDto result = conversationService.createConversation(userId, createRequest);

        // Then
        assertNotNull(result);
        assertEquals(conversationDto.getId(), result.getId());
        assertEquals(conversationDto.getTitle(), result.getTitle());
        assertEquals(userId, result.getUserId());

        verify(conversationRepository).save(any(Conversation.class));
        verify(conversationMapper).toDto(conversation);
    }

    @Test
    void testGetConversation_ConversationExiste_ConversationRetournee() {
        // Given
        Long conversationId = 1L;
        Long userId = 123L;
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(conversation));
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);
        when(messageRepository.findLastMessages(eq(conversationId), any(PageRequest.class)))
                .thenReturn(List.of(message));
        when(messageMapper.toDto(List.of(message))).thenReturn(List.of(messageDto));

        // When
        ConversationDto result = conversationService.getConversation(conversationId, userId);

        // Then
        assertNotNull(result);
        assertEquals(conversationId, result.getId());
        assertNotNull(result.getRecentMessages());

        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(conversationMapper).toDto(conversation);
    }

    @Test
    void testGetConversation_ConversationInexistante_ExceptionLevee() {
        // Given
        Long conversationId = 999L;
        Long userId = 123L;
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> conversationService.getConversation(conversationId, userId));
        
        assertEquals("Conversation introuvable", exception.getMessage());
        
        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(conversationMapper, never()).toDto((Conversation)any());
    }

    @Test
    void testGetUserConversations_UtilisateurAvecConversations_ListeRetournee() {
        // Given
        Long userId = 123L;
        ConversationStatus status = ConversationStatus.ACTIVE;
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<Conversation> conversationPage = new PageImpl<>(List.of(conversation));
        when(conversationRepository.findByUserIdAndStatus(userId, status, pageable))
                .thenReturn(conversationPage);
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);

        // When
        Page<ConversationDto> result = conversationService.getUserConversations(userId, status, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals(conversationDto.getId(), result.getContent().get(0).getId());

        verify(conversationRepository).findByUserIdAndStatus(userId, status, pageable);
    }

    @Test
    void testGetConversationMessages_ConversationExiste_MessagesRetournes() {
        // Given
        Long conversationId = 1L;
        Long userId = 123L;
        Pageable pageable = PageRequest.of(0, 50);
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(conversation));
        
        Page<Message> messagePage = new PageImpl<>(List.of(message));
        when(messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable))
                .thenReturn(messagePage);
        when(messageMapper.toDto(List.of(message))).thenReturn(List.of(messageDto));

        // When
        List<MessageDto> result = conversationService.getConversationMessages(conversationId, userId, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(messageDto.getContent(), result.get(0).getContent());

        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(messageRepository).findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);
    }

    @Test
    void testUpdateConversation_ConversationExiste_ConversationMiseAJour() {
        // Given
        Long conversationId = 1L;
        Long userId = 123L;
        
        createRequest.setTitle("Nouveau titre");
        createRequest.setDescription("Nouvelle description");
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);

        // When
        ConversationDto result = conversationService.updateConversation(conversationId, userId, createRequest);

        // Then
        assertNotNull(result);
        assertEquals("Nouveau titre", conversation.getTitle());
        assertEquals("Nouvelle description", conversation.getDescription());

        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(conversationRepository).save(conversation);
    }

    @Test
    void testDeleteConversation_ConversationExiste_ConversationSupprimee() {
        // Given
        Long conversationId = 1L;
        Long userId = 123L;
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(conversation));

        // When
        conversationService.deleteConversation(conversationId, userId);

        // Then
        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(conversationRepository).delete(conversation);
    }

    @Test
    void testArchiveConversation_ConversationExiste_ConversationArchivee() {
        // Given
        Long conversationId = 1L;
        Long userId = 123L;
        
        when(conversationRepository.findByIdAndUserId(conversationId, userId))
                .thenReturn(Optional.of(conversation));
        when(conversationRepository.save(conversation)).thenReturn(conversation);

        // When
        conversationService.archiveConversation(conversationId, userId);

        // Then
        assertEquals(ConversationStatus.ARCHIVED, conversation.getStatus());
        
        verify(conversationRepository).findByIdAndUserId(conversationId, userId);
        verify(conversationRepository).save(conversation);
    }

    @Test
    void testSearchConversations_TermeRecherche_ResultatsRetournes() {
        // Given
        Long userId = 123L;
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 20);
        
        Page<Conversation> conversationPage = new PageImpl<>(List.of(conversation));
        when(conversationRepository.searchConversations(userId, searchTerm, ConversationStatus.ACTIVE, pageable))
                .thenReturn(conversationPage);
        when(conversationMapper.toDto(conversation)).thenReturn(conversationDto);

        // When
        Page<ConversationDto> result = conversationService.searchConversations(userId, searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());

        verify(conversationRepository).searchConversations(userId, searchTerm, ConversationStatus.ACTIVE, pageable);
    }

    @Test
    void testGetUserConversationCount_UtilisateurExiste_NombreRetourne() {
        // Given
        Long userId = 123L;
        Long expectedCount = 5L;
        
        when(conversationRepository.countByUserIdAndStatus(userId, ConversationStatus.ACTIVE))
                .thenReturn(expectedCount);

        // When
        Long result = conversationService.getUserConversationCount(userId);

        // Then
        assertEquals(expectedCount, result);
        
        verify(conversationRepository).countByUserIdAndStatus(userId, ConversationStatus.ACTIVE);
    }
}