package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.RendezVousResponseDTO;
import ci.hardwork.gestionrdvservice.core.service.RendezVousService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Planning", description = "API de gestion des plannings et disponibilités")
public class PlanningController {
    
    private final RendezVousService rendezVousService;
    
    @GetMapping("/medecin/{medecinId}")
    @Operation(summary = "Récupérer le planning d'un médecin", 
               description = "Retourne tous les rendez-vous planifiés pour un médecin donné")
    @ApiResponse(responseCode = "200", description = "Planning récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningMedecin(@PathVariable Long medecinId) {
        log.debug("Récupération du planning pour le médecin ID: {}", medecinId);
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousByMedecin(medecinId);
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/medecin/{medecinId}/date/{date}")
    @Operation(summary = "Récupérer le planning d'un médecin pour une date", 
               description = "Retourne les rendez-vous d'un médecin pour une date spécifique")
    @ApiResponse(responseCode = "200", description = "Planning quotidien récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningMedecinParDate(
            @Parameter(description = "ID du médecin") @PathVariable Long medecinId,
            @Parameter(description = "Date au format YYYY-MM-DD") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.debug("Récupération du planning pour le médecin ID: {} à la date: {}", medecinId, date);
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousByMedecinAndDate(medecinId, date);
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Récupérer le planning d'un patient", 
               description = "Retourne tous les rendez-vous planifiés pour un patient donné")
    @ApiResponse(responseCode = "200", description = "Planning patient récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningPatient(@PathVariable Long patientId) {
        log.debug("Récupération du planning pour le patient ID: {}", patientId);
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousByPatient(patientId);
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/patient/{patientId}/periode")
    @Operation(summary = "Récupérer le planning d'un patient sur une période", 
               description = "Retourne les rendez-vous d'un patient entre deux dates")
    @ApiResponse(responseCode = "200", description = "Planning par période récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningPatientParPeriode(
            @Parameter(description = "ID du patient") @PathVariable Long patientId,
            @Parameter(description = "Date et heure de début") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @Parameter(description = "Date et heure de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        
        log.debug("Récupération du planning pour le patient ID: {} du {} au {}", patientId, debut, fin);
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousByPatientAndDateRange(patientId, debut, fin);
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/jour/{date}")
    @Operation(summary = "Récupérer le planning général du jour", 
               description = "Retourne tous les rendez-vous planifiés pour une date donnée")
    @ApiResponse(responseCode = "200", description = "Planning du jour récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningDuJour(
            @Parameter(description = "Date au format YYYY-MM-DD") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        log.debug("Récupération du planning général pour le: {}", date);
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousDuJour(date);
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/aujourd-hui")
    @Operation(summary = "Récupérer le planning d'aujourd'hui", 
               description = "Retourne tous les rendez-vous planifiés pour aujourd'hui")
    @ApiResponse(responseCode = "200", description = "Planning d'aujourd'hui récupéré avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getPlanningAujourdhui() {
        log.debug("Récupération du planning d'aujourd'hui");
        List<RendezVousResponseDTO> planning = rendezVousService.getRendezVousDuJour(LocalDate.now());
        return ResponseEntity.ok(planning);
    }
    
    @GetMapping("/disponibilites/medecin/{medecinId}")
    @Operation(summary = "Vérifier les disponibilités d'un médecin", 
               description = "Retourne les créneaux libres d'un médecin pour une date")
    @ApiResponse(responseCode = "200", description = "Disponibilités récupérées avec succès")
    public ResponseEntity<List<LocalDateTime>> getDisponibilitesMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long medecinId,
            @Parameter(description = "Date au format YYYY-MM-DD") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Durée du rendez-vous en minutes (défaut: 30)") 
            @RequestParam(defaultValue = "30") int dureeEnMinutes) {
        
        log.debug("Récupération des disponibilités pour le médecin ID: {} le {} (durée: {} min)", 
                medecinId, date, dureeEnMinutes);
        
        List<LocalDateTime> disponibilites = rendezVousService.getCreneauxLibres(medecinId, date, dureeEnMinutes);
        return ResponseEntity.ok(disponibilites);
    }
    
    @GetMapping("/validation-creneau")
    @Operation(summary = "Valider un créneau", 
               description = "Vérifie si un créneau est disponible pour un médecin")
    @ApiResponse(responseCode = "200", description = "Validation effectuée")
    public ResponseEntity<Boolean> validerCreneau(
            @Parameter(description = "ID du médecin") @RequestParam Long medecinId,
            @Parameter(description = "Date et heure de début") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @Parameter(description = "Date et heure de fin") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @Parameter(description = "ID du rendez-vous à exclure (pour modification)") 
            @RequestParam(required = false) Long excludeRendezVousId) {
        
        log.debug("Validation du créneau pour le médecin ID: {} du {} au {}", medecinId, debut, fin);
        
        boolean disponible = rendezVousService.isCreneauDisponible(medecinId, debut, fin, excludeRendezVousId);
        return ResponseEntity.ok(disponible);
    }
    
    @GetMapping("/rappels-requis")
    @Operation(summary = "Récupérer les rendez-vous nécessitant un rappel", 
               description = "Retourne la liste des rendez-vous pour lesquels un rappel doit être envoyé")
    @ApiResponse(responseCode = "200", description = "Liste des rappels récupérée avec succès")
    public ResponseEntity<List<RendezVousResponseDTO>> getRendezVousNecessitantRappel() {
        log.debug("Récupération des rendez-vous nécessitant un rappel");
        List<RendezVousResponseDTO> rappels = rendezVousService.getRendezVousRequiringReminder();
        return ResponseEntity.ok(rappels);
    }
}