package ci.hardwork.chatai.web.controller;

import ci.hardwork.chatai.core.dto.ConversationDto;
import ci.hardwork.chatai.core.dto.CreateConversationRequest;
import ci.hardwork.chatai.core.dto.MessageDto;
import ci.hardwork.chatai.core.models.enums.ConversationStatus;
import ci.hardwork.chatai.core.service.ConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conversations", description = "Gestion des conversations")
public class ConversationController {

    private final ConversationService conversationService;

    @Operation(summary = "Créer une conversation", description = "Crée une nouvelle conversation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversation créée",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConversationDto.class))),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ConversationDto> createConversation(@Valid @RequestBody CreateConversationRequest request,
                                                             HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Création d'une nouvelle conversation pour l'utilisateur {}: {}", userId, request.getTitle());

        try {
            ConversationDto conversation = conversationService.createConversation(userId, request);
            return ResponseEntity.ok(conversation);
            
        } catch (Exception e) {
            log.error("Erreur lors de la création de conversation pour l'utilisateur {}: {}", 
                    userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de la conversation: " + e.getMessage());
        }
    }

    @Operation(summary = "Obtenir une conversation", description = "Récupère une conversation par ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversation trouvée"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/{conversationId}")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ConversationDto> getConversation(@PathVariable Long conversationId,
                                                          HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Récupération de la conversation {} pour l'utilisateur {}", conversationId, userId);

        try {
            ConversationDto conversation = conversationService.getConversation(conversationId, userId);
            return ResponseEntity.ok(conversation);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la conversation {} pour l'utilisateur {}: {}", 
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("Conversation introuvable");
        }
    }

    @Operation(summary = "Lister les conversations", description = "Récupère la liste des conversations de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des conversations"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Page<ConversationDto>> getUserConversations(
            @Parameter(description = "Statut des conversations") 
            @RequestParam(defaultValue = "ACTIVE") ConversationStatus status,
            @Parameter(description = "Numéro de page") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") 
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Champ de tri") 
            @RequestParam(defaultValue = "lastActivityAt") String sort,
            @Parameter(description = "Direction du tri") 
            @RequestParam(defaultValue = "desc") String direction,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        log.info("Récupération des conversations pour l'utilisateur {} (status: {}, page: {})", 
                userId, status, page);

        Page<ConversationDto> conversations = conversationService.getUserConversations(userId, status, pageable);
        return ResponseEntity.ok(conversations);
    }

    @Operation(summary = "Messages d'une conversation", description = "Récupère les messages d'une conversation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Messages récupérés"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/{conversationId}/messages")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<List<MessageDto>> getConversationMessages(
            @PathVariable Long conversationId,
            @Parameter(description = "Numéro de page") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") 
            @RequestParam(defaultValue = "50") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());

        log.info("Récupération des messages de la conversation {} pour l'utilisateur {}", 
                conversationId, userId);

        try {
            List<MessageDto> messages = conversationService.getConversationMessages(conversationId, userId, pageable);
            return ResponseEntity.ok(messages);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des messages de la conversation {} pour l'utilisateur {}: {}", 
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("Impossible de récupérer les messages");
        }
    }

    @Operation(summary = "Mettre à jour une conversation", description = "Met à jour les informations d'une conversation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Conversation mise à jour"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/{conversationId}")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ConversationDto> updateConversation(@PathVariable Long conversationId,
                                                            @Valid @RequestBody CreateConversationRequest request,
                                                            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Mise à jour de la conversation {} pour l'utilisateur {}", conversationId, userId);

        try {
            ConversationDto conversation = conversationService.updateConversation(conversationId, userId, request);
            return ResponseEntity.ok(conversation);
            
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de la conversation {} pour l'utilisateur {}: {}", 
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("Impossible de mettre à jour la conversation");
        }
    }

    @Operation(summary = "Supprimer une conversation", description = "Supprime définitivement une conversation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conversation supprimée"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @DeleteMapping("/{conversationId}")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId,
                                                   HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Suppression de la conversation {} pour l'utilisateur {}", conversationId, userId);

        try {
            conversationService.deleteConversation(conversationId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la conversation {} pour l'utilisateur {}: {}", 
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("Impossible de supprimer la conversation");
        }
    }

    @Operation(summary = "Archiver une conversation", description = "Archive une conversation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Conversation archivée"),
        @ApiResponse(responseCode = "404", description = "Conversation non trouvée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/{conversationId}/archive")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Void> archiveConversation(@PathVariable Long conversationId,
                                                    HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Archivage de la conversation {} pour l'utilisateur {}", conversationId, userId);

        try {
            conversationService.archiveConversation(conversationId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            log.error("Erreur lors de l'archivage de la conversation {} pour l'utilisateur {}: {}", 
                    conversationId, userId, e.getMessage(), e);
            throw new RuntimeException("Impossible d'archiver la conversation");
        }
    }

    @Operation(summary = "Rechercher des conversations", description = "Recherche dans les conversations de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Résultats de recherche"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<Page<ConversationDto>> searchConversations(
            @Parameter(description = "Terme de recherche") 
            @RequestParam String query,
            @Parameter(description = "Numéro de page") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de page") 
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("lastActivityAt").descending());

        log.info("Recherche de conversations pour l'utilisateur {} avec le terme: {}", userId, query);

        Page<ConversationDto> conversations = conversationService.searchConversations(userId, query, pageable);
        return ResponseEntity.ok(conversations);
    }

    @Operation(summary = "Statistiques utilisateur", description = "Obtient les statistiques de conversation de l'utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/stats")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<UserStatsResponse> getUserStats(HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Récupération des statistiques pour l'utilisateur {}", userId);

        Long conversationCount = conversationService.getUserConversationCount(userId);
        
        return ResponseEntity.ok(new UserStatsResponse(conversationCount));
    }

    // Classe de réponse pour les statistiques
    public static class UserStatsResponse {
        private Long totalConversations;

        public UserStatsResponse(Long totalConversations) {
            this.totalConversations = totalConversations;
        }

        public Long getTotalConversations() { return totalConversations; }
        public void setTotalConversations(Long totalConversations) { this.totalConversations = totalConversations; }
    }
}