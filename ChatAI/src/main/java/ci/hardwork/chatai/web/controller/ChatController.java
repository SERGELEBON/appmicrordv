package ci.hardwork.chatai.web.controller;

import ci.hardwork.chatai.core.dto.ChatRequest;
import ci.hardwork.chatai.core.dto.ChatResponse;
import ci.hardwork.chatai.core.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "Endpoints pour les conversations avec l'IA")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Envoyer un message", description = "Envoie un message à l'IA et reçoit une réponse")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message envoyé avec succès",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatResponse.class))),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "429", description = "Limite de taux dépassée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PostMapping("/message")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ChatResponse> sendMessage(@Valid @RequestBody ChatRequest request,
                                                   HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Réception d'un message de l'utilisateur {} pour la conversation {}", 
                userId, request.getConversationId());

        try {
            ChatResponse response = chatService.sendMessage(userId, request);
            
            log.info("Réponse générée avec succès pour l'utilisateur {} en {}ms", 
                    userId, response.getResponseTimeMs());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Erreur lors du traitement du message pour l'utilisateur {}: {}", 
                    userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors du traitement de votre message: " + e.getMessage());
        }
    }

    @Operation(summary = "Conversation en streaming", description = "Envoie un message et reçoit la réponse en streaming SSE")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stream de réponse initié"),
        @ApiResponse(responseCode = "400", description = "Requête invalide"),
        @ApiResponse(responseCode = "429", description = "Limite de taux dépassée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PostMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public SseEmitter sendMessageStream(@Valid @RequestBody ChatRequest request,
                                       HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        log.info("Début du streaming pour l'utilisateur {} conversation {}", 
                userId, request.getConversationId());

        try {
            return chatService.sendMessageStream(userId, request);
            
        } catch (Exception e) {
            log.error("Erreur lors du streaming pour l'utilisateur {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors du streaming: " + e.getMessage());
        }
    }

    @Operation(summary = "Vérifier un modèle", description = "Vérifie si un modèle d'IA est disponible")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vérification effectuée"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/models/{modelName}/check")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<ModelStatusResponse> checkModel(@PathVariable String modelName) {
        
        log.info("Vérification de la disponibilité du modèle: {}", modelName);
        
        boolean available = chatService.isModelAvailable(modelName);
        
        return ResponseEntity.ok(new ModelStatusResponse(modelName, available, 
                available ? "Modèle disponible" : "Modèle non disponible"));
    }

    @Operation(summary = "Générer un titre", description = "Génère un titre pour une conversation basé sur le premier message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Titre généré"),
        @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PostMapping("/generate-title")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<TitleResponse> generateTitle(@RequestBody TitleRequest request) {
        
        log.info("Génération de titre pour le message: {}", request.getMessage().substring(0, Math.min(50, request.getMessage().length())));
        
        try {
            String title = chatService.generateTitle(request.getMessage());
            return ResponseEntity.ok(new TitleResponse(title));
            
        } catch (Exception e) {
            log.error("Erreur lors de la génération du titre: {}", e.getMessage(), e);
            return ResponseEntity.ok(new TitleResponse("Nouvelle conversation"));
        }
    }

    // Classes de réponse internes
    public static class ModelStatusResponse {
        private String modelName;
        private boolean available;
        private String message;

        public ModelStatusResponse(String modelName, boolean available, String message) {
            this.modelName = modelName;
            this.available = available;
            this.message = message;
        }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class TitleRequest {
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class TitleResponse {
        private String title;

        public TitleResponse(String title) { this.title = title; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
}