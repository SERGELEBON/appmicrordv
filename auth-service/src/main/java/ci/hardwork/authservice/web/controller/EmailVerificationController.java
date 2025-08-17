package ci.hardwork.authservice.web.controller;

import ci.hardwork.authservice.core.dto.MessageResponse;
import ci.hardwork.authservice.core.services.TwilioEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmailVerificationController {

    private final TwilioEmailService twilioEmailService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendVerificationEmail(@RequestBody SendVerificationRequest request) {
        try {
            twilioEmailService.sendEmailVerification(request.getEmail());
            return ResponseEntity.ok(new MessageResponse(
                "Code de vérification envoyé à " + request.getEmail()
            ));
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError()
                                 .body(new MessageResponse("Erreur lors de l'envoi du code de vérification"));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<MessageResponse> verifyEmail(@RequestBody VerifyEmailRequest request) {
        try {
            boolean isValid = twilioEmailService.verifyEmailCode(request.getEmail(), request.getCode());
            
            if (isValid) {
                return ResponseEntity.ok(new MessageResponse("Email vérifié avec succès"));
            } else {
                return ResponseEntity.badRequest()
                                     .body(new MessageResponse("Code de vérification invalide ou expiré"));
            }
        } catch (Exception e) {
            log.error("Failed to verify email for: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError()
                                 .body(new MessageResponse("Erreur lors de la vérification"));
        }
    }

    // DTOs
    public static class SendVerificationRequest {
        private String email;

        public SendVerificationRequest() {}

        public SendVerificationRequest(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class VerifyEmailRequest {
        private String email;
        private String code;

        public VerifyEmailRequest() {}

        public VerifyEmailRequest(String email, String code) {
            this.email = email;
            this.code = code;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }
}