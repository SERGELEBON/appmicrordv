package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Patient", description = "API de gestion des patients")
public class PatientController {
    
    private final PatientService patientService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau patient", description = "Crée un nouveau patient avec les informations fournies")
    @ApiResponse(responseCode = "201", description = "Patient créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientCreateDTO patientCreateDTO) {
        log.info("Réception de la demande de création de patient: {}", patientCreateDTO.getEmail());
        try {
            PatientResponseDTO createdPatient = patientService.createPatient(patientCreateDTO);
            return new ResponseEntity<>(createdPatient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création du patient: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un patient par ID", description = "Retourne les détails d'un patient spécifique")
    @ApiResponse(responseCode = "200", description = "Patient trouvé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<PatientResponseDTO> getPatientById(@Parameter(description = "ID du patient") @PathVariable Long id) {
        log.debug("Recherche du patient avec l'ID: {}", id);
        return patientService.getPatientById(id)
                .map(patient -> ResponseEntity.ok(patient))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les patients", description = "Retourne une liste paginée de tous les patients")
    @ApiResponse(responseCode = "200", description = "Liste des patients récupérée avec succès")
    public ResponseEntity<Page<PatientResponseDTO>> getAllPatients(Pageable pageable) {
        log.debug("Récupération de la liste des patients, page: {}", pageable.getPageNumber());
        Page<PatientResponseDTO> patients = patientService.getAllPatients(pageable);
        return ResponseEntity.ok(patients);
    }
    
    @GetMapping("/nss/{nss}")
    @Operation(summary = "Rechercher un patient par NSS", description = "Trouve un patient par son numéro de sécurité sociale")
    @ApiResponse(responseCode = "200", description = "Patient trouvé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<PatientResponseDTO> getPatientByNSS(@Parameter(description = "Numéro de sécurité sociale") @PathVariable String nss) {
        log.debug("Recherche du patient avec le NSS: {}", nss);
        return patientService.getPatientByNumeroSecuriteSociale(nss)
                .map(patient -> ResponseEntity.ok(patient))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    @Operation(summary = "Rechercher un patient par email", description = "Trouve un patient par son adresse email")
    @ApiResponse(responseCode = "200", description = "Patient trouvé")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<PatientResponseDTO> getPatientByEmail(@Parameter(description = "Adresse email du patient") @PathVariable String email) {
        log.debug("Recherche du patient avec l'email: {}", email);
        return patientService.getPatientByEmail(email)
                .map(patient -> ResponseEntity.ok(patient))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rechercher des patients par nom", description = "Recherche des patients par nom et prénom (partiel)")
    @ApiResponse(responseCode = "200", description = "Résultats de la recherche")
    public ResponseEntity<List<PatientResponseDTO>> searchPatients(
            @Parameter(description = "Nom (partiel)") @RequestParam(defaultValue = "") String nom,
            @Parameter(description = "Prénom (partiel)") @RequestParam(defaultValue = "") String prenom) {
        log.debug("Recherche de patients avec nom: '{}', prénom: '{}'", nom, prenom);
        List<PatientResponseDTO> patients = patientService.searchPatientsByName(nom, prenom);
        return ResponseEntity.ok(patients);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un patient", description = "Met à jour les informations d'un patient existant")
    @ApiResponse(responseCode = "200", description = "Patient mis à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @Parameter(description = "ID du patient") @PathVariable Long id,
            @Valid @RequestBody PatientUpdateDTO patientUpdateDTO) {
        log.info("Mise à jour du patient ID: {}", id);
        try {
            PatientResponseDTO updatedPatient = patientService.updatePatient(id, patientUpdateDTO);
            return ResponseEntity.ok(updatedPatient);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la mise à jour du patient: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un patient", description = "Supprime définitivement un patient")
    @ApiResponse(responseCode = "204", description = "Patient supprimé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Void> deletePatient(@Parameter(description = "ID du patient") @PathVariable Long id) {
        log.info("Suppression du patient ID: {}", id);
        try {
            patientService.deletePatient(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la suppression du patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un patient", description = "Désactive un patient sans le supprimer")
    @ApiResponse(responseCode = "200", description = "Patient désactivé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Void> deactivatePatient(@Parameter(description = "ID du patient") @PathVariable Long id) {
        log.info("Désactivation du patient ID: {}", id);
        try {
            patientService.deactivatePatient(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la désactivation du patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer un patient", description = "Active un patient désactivé")
    @ApiResponse(responseCode = "200", description = "Patient activé avec succès")
    @ApiResponse(responseCode = "404", description = "Patient non trouvé")
    public ResponseEntity<Void> activatePatient(@Parameter(description = "ID du patient") @PathVariable Long id) {
        log.info("Activation du patient ID: {}", id);
        try {
            patientService.activatePatient(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de l'activation du patient: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Compter les patients actifs", description = "Retourne le nombre total de patients actifs")
    @ApiResponse(responseCode = "200", description = "Nombre de patients actifs")
    public ResponseEntity<Long> countActivePatients() {
        long count = patientService.countActivePatients();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/exists")
    @Operation(summary = "Vérifier l'existence d'un patient", description = "Vérifie si un patient existe par son ID")
    @ApiResponse(responseCode = "200", description = "Statut d'existence du patient")
    public ResponseEntity<Boolean> patientExists(@Parameter(description = "ID du patient") @PathVariable Long id) {
        boolean exists = patientService.getPatientById(id).isPresent();
        return ResponseEntity.ok(exists);
    }
}