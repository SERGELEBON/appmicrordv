package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import ci.hardwork.gestionrdvservice.core.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rdv")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Rendez-vous", description = "API de gestion des rendez-vous")
public class RendezVousController {
    
    private final RendezVousService rendezVousService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau rendez-vous", description = "Planifie un nouveau rendez-vous entre un patient et un médecin")
    @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou créneau non disponible")
    public ResponseEntity<RendezVousResponseDTO> createRendezVous(@Valid @RequestBody RendezVousCreateDTO rendezVousCreateDTO) {
        log.info("Création d'un nouveau rendez-vous - Patient: {}, Médecin: {}", 
                rendezVousCreateDTO.getPatientId(), rendezVousCreateDTO.getMedecinId());
        try {
            RendezVousResponseDTO createdRendezVous = rendezVousService.createRendezVous(rendezVousCreateDTO);
            return new ResponseEntity<>(createdRendezVous, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création du rendez-vous: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rendez-vous par ID")
    @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé")
    @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    public ResponseEntity<RendezVousResponseDTO> getRendezVousById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les rendez-vous")
    public ResponseEntity<Page<RendezVousResponseDTO>> getAllRendezVous(Pageable pageable) {
        Page<RendezVousResponseDTO> rendezVous = rendezVousService.getAllRendezVous(pageable);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Récupérer les rendez-vous d'un patient")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousByPatient(@PathVariable Long patientId) {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousByPatient(patientId);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/medecin/{medecinId}")
    @Operation(summary = "Récupérer les rendez-vous d'un médecin")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousByMedecin(@PathVariable Long medecinId) {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousByMedecin(medecinId);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/medecin/{medecinId}/date/{date}")
    @Operation(summary = "Récupérer les rendez-vous d'un médecin pour une date donnée")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousByMedecinAndDate(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousByMedecinAndDate(medecinId, date);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/patient/{patientId}/periode")
    @Operation(summary = "Récupérer les rendez-vous d'un patient sur une période")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousByPatientAndDateRange(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousByPatientAndDateRange(patientId, debut, fin);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/statut/{status}")
    @Operation(summary = "Récupérer les rendez-vous par statut")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousByStatus(@PathVariable RendezVousStatus status) {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousByStatus(status);
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/aujourdhui")
    @Operation(summary = "Récupérer les rendez-vous du jour")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousDuJour() {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousDuJour(LocalDate.now());
        return ResponseEntity.ok(rendezVous);
    }
    
    @GetMapping("/rappels-requis")
    @Operation(summary = "Récupérer les rendez-vous nécessitant un rappel")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousRequiringReminder() {
        List<RendezVousResponseDTO> rendezVous = rendezVousService.getRendezVousRequiringReminder();
        return ResponseEntity.ok(rendezVous);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un rendez-vous")
    @ApiResponse(responseCode = "200", description = "Rendez-vous mis à jour avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé")
    public ResponseEntity<RendezVousResponseDTO> updateRendezVous(
            @PathVariable Long id, @Valid @RequestBody RendezVousCreateDTO rendezVousUpdateDTO) {
        try {
            RendezVousResponseDTO updatedRendezVous = rendezVousService.updateRendezVous(id, rendezVousUpdateDTO);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/statut")
    @Operation(summary = "Mettre à jour le statut d'un rendez-vous")
    public ResponseEntity<RendezVousResponseDTO> updateStatut(
            @PathVariable Long id, @RequestParam RendezVousStatus nouveauStatut) {
        try {
            RendezVousResponseDTO updatedRendezVous = rendezVousService.updateStatut(id, nouveauStatut);
            return ResponseEntity.ok(updatedRendezVous);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}/confirmer")
    @Operation(summary = "Confirmer un rendez-vous")
    public ResponseEntity<Void> confirmRendezVous(@PathVariable Long id) {
        try {
            rendezVousService.confirmRendezVous(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/annuler")
    @Operation(summary = "Annuler un rendez-vous")
    public ResponseEntity<Void> cancelRendezVous(
            @PathVariable Long id, @RequestParam String motifAnnulation) {
        try {
            rendezVousService.cancelRendezVous(id, motifAnnulation);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rendez-vous")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        try {
            rendezVousService.deleteRendezVous(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/creneau-disponible")
    @Operation(summary = "Vérifier la disponibilité d'un créneau")
    public ResponseEntity<Boolean> isCreneauDisponible(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(required = false) Long excludeRendezVousId) {
        boolean disponible = rendezVousService.isCreneauDisponible(medecinId, debut, fin, excludeRendezVousId);
        return ResponseEntity.ok(disponible);
    }
    
    @GetMapping("/creneaux-libres")
    @Operation(summary = "Récupérer les créneaux libres d'un médecin pour une date")
    public ResponseEntity<List<LocalDateTime>> getCreneauxLibres(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "30") int dureeEnMinutes) {
        List<LocalDateTime> creneauxLibres = rendezVousService.getCreneauxLibres(medecinId, date, dureeEnMinutes);
        return ResponseEntity.ok(creneauxLibres);
    }
    
    @PostMapping("/{id}/rappel")
    @Operation(summary = "Envoyer un rappel pour un rendez-vous")
    public ResponseEntity<Void> envoyerRappel(@PathVariable Long id) {
        try {
            rendezVousService.envoyerRappel(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/statistiques/{status}")
    @Operation(summary = "Compter les rendez-vous par statut")
    public ResponseEntity<Long> countRendezVousByStatus(@PathVariable RendezVousStatus status) {
        long count = rendezVousService.countRendezVousByStatus(status);
        return ResponseEntity.ok(count);
    }
}