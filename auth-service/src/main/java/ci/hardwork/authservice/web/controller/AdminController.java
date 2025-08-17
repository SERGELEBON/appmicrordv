package ci.hardwork.authservice.web.controller;

import ci.hardwork.authservice.core.dto.MessageResponse;
import ci.hardwork.authservice.core.dto.UserDto;
import ci.hardwork.authservice.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final UserService userService;

    @GetMapping("/pending-doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getPendingDoctors() {
        try {
            List<UserDto> pendingDoctors = userService.getPendingDoctors();
            return ResponseEntity.ok(pendingDoctors);
        } catch (Exception e) {
            log.error("Error fetching pending doctors", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/approve-doctor/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> approveDoctor(@PathVariable Long userId) {
        try {
            // Note: Dans un vrai système, on récupérerait l'ID de l'admin depuis le JWT
            Long adminId = 1L; // Temporaire - à remplacer par l'ID réel de l'admin connecté
            
            UserDto approvedUser = userService.approveDoctorValidation(userId, adminId);
            
            return ResponseEntity.ok(new MessageResponse(
                String.format("Médecin %s %s approuvé avec succès", 
                             approvedUser.getFirstName(), approvedUser.getLastName())
            ));
        } catch (IllegalStateException e) {
            log.error("Invalid state for approval: {}", userId, e);
            return ResponseEntity.badRequest()
                                 .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving doctor: {}", userId, e);
            return ResponseEntity.internalServerError()
                                 .body(new MessageResponse("Erreur lors de l'approbation du médecin"));
        }
    }

    @PostMapping("/reject-doctor/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> rejectDoctor(@PathVariable Long userId, 
                                                       @RequestBody RejectDoctorRequest request) {
        try {
            // Note: Dans un vrai système, on récupérerait l'ID de l'admin depuis le JWT
            Long adminId = 1L; // Temporaire - à remplacer par l'ID réel de l'admin connecté
            
            UserDto rejectedUser = userService.rejectDoctorValidation(
                userId, request.getRejectionReason(), adminId
            );
            
            return ResponseEntity.ok(new MessageResponse(
                String.format("Médecin %s %s rejeté", 
                             rejectedUser.getFirstName(), rejectedUser.getLastName())
            ));
        } catch (IllegalStateException e) {
            log.error("Invalid state for rejection: {}", userId, e);
            return ResponseEntity.badRequest()
                                 .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Error rejecting doctor: {}", userId, e);
            return ResponseEntity.internalServerError()
                                 .body(new MessageResponse("Erreur lors du rejet du médecin"));
        }
    }

    @GetMapping("/pending-doctors/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Long> getPendingDoctorsCount() {
        try {
            long count = userService.getPendingDoctorsCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching pending doctors count", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> users = userService.findAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO pour la requête de rejet
    public static class RejectDoctorRequest {
        private String rejectionReason;

        public RejectDoctorRequest() {}

        public RejectDoctorRequest(String rejectionReason) {
            this.rejectionReason = rejectionReason;
        }

        public String getRejectionReason() {
            return rejectionReason;
        }

        public void setRejectionReason(String rejectionReason) {
            this.rejectionReason = rejectionReason;
        }
    }
}