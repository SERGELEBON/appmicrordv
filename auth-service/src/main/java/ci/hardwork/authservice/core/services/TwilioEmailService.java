package ci.hardwork.authservice.core.services;

import ci.hardwork.authservice.config.TwilioConfig;
import ci.hardwork.authservice.core.models.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioEmailService {

    private final TwilioConfig twilioConfig;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    // Service ID for Twilio Verify
    @Value("${app.twilio.verify-service-sid:VAxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx}")
    private String verifyServiceSid;

    /**
     * Envoie un email de vérification via Twilio Verify
     */
    public void sendEmailVerification(String email) {
        try {
            Verification verification = Verification.creator(
                    verifyServiceSid,
                    email,
                    "email"
            ).create();

            log.info("Email verification sent to: {} with SID: {}", email, verification.getSid());
        } catch (Exception e) {
            log.error("Failed to send email verification to: {}", email, e);
            throw new RuntimeException("Failed to send email verification", e);
        }
    }

    /**
     * Vérifie le code d'email envoyé via Twilio Verify
     */
    public boolean verifyEmailCode(String email, String code) {
        try {
            VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                    .setTo(email)
                    .setCode(code)
                    .create();

            boolean isValid = "approved".equals(verificationCheck.getStatus());
            log.info("Email verification for {} status: {}", email, verificationCheck.getStatus());
            return isValid;
        } catch (Exception e) {
            log.error("Failed to verify email code for: {}", email, e);
            return false;
        }
    }

    /**
     * Envoie un email personnalisé via SendGrid
     */
    public void sendCustomEmail(String toEmail, String subject, String htmlContent) {
        try {
            Email from = new Email(twilioConfig.getFromEmail(), twilioConfig.getFromName());
            Email to = new Email(toEmail);
            Content content = new Content("text/html", htmlContent);
            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(twilioConfig.getSendGridApiKey());
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Email sent successfully to: {} - Status: {}", toEmail, response.getStatusCode());
            } else {
                log.error("Failed to send email to: {} - Status: {} - Body: {}", 
                         toEmail, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Email sending failed with status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("Failed to send custom email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Envoie un email de demande de validation médecin
     */
    public void sendDoctorValidationRequestEmail(User user) {
        String subject = "Demande de validation reçue - RDV360";
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2563eb;">Bonjour Dr %s %s,</h2>
                
                <p>Nous avons bien reçu votre demande de validation de compte médecin sur <strong>RDV360</strong>.</p>
                
                <div style="background-color: #f8fafc; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <h3 style="color: #374151; margin-top: 0;">Détails de votre demande :</h3>
                    <ul style="color: #6b7280;">
                        <li><strong>Nom :</strong> %s %s</li>
                        <li><strong>Email :</strong> %s</li>
                        <li><strong>Spécialité :</strong> %s</li>
                        <li><strong>Numéro d'ordre :</strong> %s</li>
                        <li><strong>Date de demande :</strong> %s</li>
                    </ul>
                </div>
                
                <p>Votre demande est actuellement <strong>en cours d'examen</strong> par notre équipe d'administration.</p>
                <p>Vous recevrez un email de confirmation une fois la validation effectuée.</p>
                
                <div style="background-color: #fef3c7; padding: 15px; border-radius: 6px; margin: 20px 0;">
                    <p style="margin: 0; color: #92400e;">⏱️ Le processus de validation peut prendre entre 24 et 48 heures.</p>
                </div>
                
                <p style="margin-top: 30px;">Cordialement,<br><strong>L'équipe RDV360</strong></p>
                
                <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;">
                <p style="font-size: 12px; color: #9ca3af;">
                    Cet email a été envoyé automatiquement, merci de ne pas y répondre.
                </p>
            </div>
            """,
            user.getFirstName(), user.getLastName(),
            user.getFirstName(), user.getLastName(),
            user.getEmail(),
            user.getSpeciality(),
            user.getLicenseNumber(),
            user.getCreatedAt()
        );

        sendCustomEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Envoie un email d'approbation de validation médecin
     */
    public void sendDoctorValidationApprovedEmail(User user) {
        String subject = "🎉 Compte médecin validé - RDV360";
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #059669;">🎉 Félicitations Dr %s %s !</h2>
                
                <p>Votre compte médecin <strong>RDV360</strong> a été validé avec succès.</p>
                
                <div style="background-color: #f0fdf4; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #059669;">
                    <h3 style="color: #059669; margin-top: 0;">Vous pouvez maintenant :</h3>
                    <ul style="color: #166534;">
                        <li>✅ Gérer vos rendez-vous</li>
                        <li>✅ Consulter vos patients</li>
                        <li>✅ Utiliser notre assistant IA médical</li>
                        <li>✅ Générer des résumés de consultation automatiques</li>
                    </ul>
                </div>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s/login" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                        🚀 Connectez-vous maintenant
                    </a>
                </div>
                
                <p>Bienvenue dans la communauté <strong>RDV360</strong> !</p>
                
                <p style="margin-top: 30px;">Cordialement,<br><strong>L'équipe RDV360</strong></p>
            </div>
            """,
            user.getFirstName(), user.getLastName(), frontendUrl
        );

        sendCustomEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Envoie un email de rejet de validation médecin
     */
    public void sendDoctorValidationRejectedEmail(User user) {
        String subject = "Demande de validation refusée - RDV360";
        String rejectionReason = user.getRejectionReason() != null ? 
            user.getRejectionReason() : "Documents non conformes ou incomplets";
            
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #dc2626;">Bonjour Dr %s %s,</h2>
                
                <p>Nous regrettons de vous informer que votre demande de validation de compte médecin a été <strong>refusée</strong>.</p>
                
                <div style="background-color: #fef2f2; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #dc2626;">
                    <h3 style="color: #dc2626; margin-top: 0;">Motif du refus :</h3>
                    <p style="color: #991b1b; margin: 0;">%s</p>
                </div>
                
                <p>Vous pouvez soumettre une nouvelle demande avec les documents requis en vous inscrivant à nouveau sur notre plateforme.</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s/register" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                        📝 Soumettre une nouvelle demande
                    </a>
                </div>
                
                <p>Si vous avez des questions, n'hésitez pas à nous contacter.</p>
                
                <p style="margin-top: 30px;">Cordialement,<br><strong>L'équipe RDV360</strong></p>
            </div>
            """,
            user.getFirstName(), user.getLastName(), rejectionReason, frontendUrl
        );

        sendCustomEmail(user.getEmail(), subject, htmlContent);
    }

    /**
     * Envoie un email de bienvenue patient
     */
    public void sendWelcomePatientEmail(String email, String firstName, String lastName) {
        String subject = "🎉 Bienvenue sur RDV360 !";
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2563eb;">🎉 Bienvenue %s %s !</h2>
                
                <p>Votre compte patient <strong>RDV360</strong> a été créé avec succès.</p>
                
                <div style="background-color: #eff6ff; padding: 20px; border-radius: 8px; margin: 20px 0;">
                    <h3 style="color: #2563eb; margin-top: 0;">Vous pouvez maintenant :</h3>
                    <ul style="color: #1e40af;">
                        <li>📅 Prendre rendez-vous avec vos médecins</li>
                        <li>📋 Consulter votre dossier médical</li>
                        <li>🤖 Utiliser notre assistant IA pour vos questions de santé</li>
                        <li>📊 Recevoir des suivis personnalisés après vos consultations</li>
                    </ul>
                </div>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s/login" style="background-color: #2563eb; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                        🚀 Connectez-vous maintenant
                    </a>
                </div>
                
                <p>Bienvenue dans <strong>RDV360</strong> !</p>
                
                <p style="margin-top: 30px;">Cordialement,<br><strong>L'équipe RDV360</strong></p>
            </div>
            """,
            firstName, lastName, frontendUrl
        );

        sendCustomEmail(email, subject, htmlContent);
    }

    /**
     * Envoie un email de réinitialisation de mot de passe
     */
    public void sendPasswordResetEmail(String email, String resetToken) {
        String subject = "🔐 Réinitialisation de mot de passe - RDV360";
        String resetUrl = String.format("%s/reset-password?token=%s", frontendUrl, resetToken);
        
        String htmlContent = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #2563eb;">🔐 Réinitialisation de mot de passe</h2>
                
                <p>Vous avez demandé la réinitialisation de votre mot de passe <strong>RDV360</strong>.</p>
                
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #dc2626; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold;">
                        🔑 Créer un nouveau mot de passe
                    </a>
                </div>
                
                <div style="background-color: #fef3c7; padding: 15px; border-radius: 6px; margin: 20px 0;">
                    <p style="margin: 0; color: #92400e;">⏱️ Ce lien est valide pendant <strong>24 heures</strong>.</p>
                </div>
                
                <p>Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email.</p>
                
                <p style="margin-top: 30px;">Cordialement,<br><strong>L'équipe RDV360</strong></p>
                
                <hr style="border: none; border-top: 1px solid #e5e7eb; margin: 30px 0;">
                <p style="font-size: 12px; color: #9ca3af;">
                    Pour votre sécurité, ne partagez jamais ce lien avec quelqu'un d'autre.
                </p>
            </div>
            """,
            resetUrl
        );

        sendCustomEmail(email, subject, htmlContent);
    }
}