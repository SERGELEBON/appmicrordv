package ci.hardwork.chatai.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Données de validation invalides",
            errors,
            request.getDescription(false)
        );

        log.warn("Erreur de validation: {}", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RUNTIME_ERROR",
            ex.getMessage(),
            null,
            request.getDescription(false)
        );

        log.error("Erreur d'exécution: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "ACCESS_DENIED",
            "Accès refusé. Vous n'avez pas les permissions nécessaires.",
            null,
            request.getDescription(false)
        );

        log.warn("Tentative d'accès non autorisé: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConversationNotFound(
            ConversationNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "CONVERSATION_NOT_FOUND",
            ex.getMessage(),
            null,
            request.getDescription(false)
        );

        log.warn("Conversation introuvable: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "RATE_LIMIT_EXCEEDED",
            ex.getMessage(),
            null,
            request.getDescription(false)
        );

        log.warn("Limite de taux dépassée: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ErrorResponse> handleAIServiceException(
            AIServiceException ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "AI_SERVICE_ERROR",
            "Erreur du service IA: " + ex.getMessage(),
            null,
            request.getDescription(false)
        );

        log.error("Erreur du service IA: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_ERROR",
            "Une erreur interne s'est produite",
            null,
            request.getDescription(false)
        );

        log.error("Erreur interne non gérée: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Classe de réponse d'erreur
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private Object details;
        private String path;
        private LocalDateTime timestamp;

        public ErrorResponse(String errorCode, String message, Object details, String path) {
            this.errorCode = errorCode;
            this.message = message;
            this.details = details;
            this.path = path;
            this.timestamp = LocalDateTime.now();
        }

        // Getters et setters
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Object getDetails() { return details; }
        public void setDetails(Object details) { this.details = details; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    // Exceptions personnalisées
    public static class ConversationNotFoundException extends RuntimeException {
        public ConversationNotFoundException(String message) {
            super(message);
        }
    }

    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String message) {
            super(message);
        }
    }

    public static class AIServiceException extends RuntimeException {
        public AIServiceException(String message) {
            super(message);
        }
        
        public AIServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}