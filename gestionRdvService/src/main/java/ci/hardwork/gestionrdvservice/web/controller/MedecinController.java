package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.*;
import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import ci.hardwork.gestionrdvservice.core.service.MedecinService;
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
@RequestMapping("/medecins")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Médecin", description = "API de gestion des médecins")
public class MedecinController {
    
    private final MedecinService medecinService;
    
    @PostMapping
    @Operation(summary = "Créer un nouveau médecin", description = "Crée un nouveau médecin avec les informations fournies")
    @ApiResponse(responseCode = "201", description = "Médecin créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    public ResponseEntity<MedecinResponseDTO> createMedecin(@Valid @RequestBody MedecinCreateDTO medecinCreateDTO) {
        log.info("Création d'un nouveau médecin: {}", medecinCreateDTO.getEmail());
        try {
            MedecinResponseDTO createdMedecin = medecinService.createMedecin(medecinCreateDTO);
            return new ResponseEntity<>(createdMedecin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Erreur lors de la création du médecin: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un médecin par ID")
    @ApiResponse(responseCode = "200", description = "Médecin trouvé")
    @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    public ResponseEntity<MedecinResponseDTO> getMedecinById(@PathVariable Long id) {
        return medecinService.getMedecinById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les médecins")
    public ResponseEntity<Page<MedecinResponseDTO>> getAllMedecins(Pageable pageable) {
        Page<MedecinResponseDTO> medecins = medecinService.getAllMedecins(pageable);
        return ResponseEntity.ok(medecins);
    }
    
    @GetMapping("/actifs")
    @Operation(summary = "Récupérer les médecins actifs")
    public ResponseEntity<List<MedecinResponseDTO>> getActiveMedecins() {
        List<MedecinResponseDTO> medecins = medecinService.getActiveMedecins();
        return ResponseEntity.ok(medecins);
    }
    
    @GetMapping("/specialite/{specialite}")
    @Operation(summary = "Rechercher des médecins par spécialité")
    public ResponseEntity<List<MedecinResponseDTO>> getMedecinsBySpecialite(@PathVariable SpecialiteMedicale specialite) {
        List<MedecinResponseDTO> medecins = medecinService.getMedecinsBySpecialite(specialite);
        return ResponseEntity.ok(medecins);
    }
    
    @GetMapping("/ville/{ville}")
    @Operation(summary = "Rechercher des médecins par ville")
    public ResponseEntity<List<MedecinResponseDTO>> getMedecinsByVille(@PathVariable String ville) {
        List<MedecinResponseDTO> medecins = medecinService.getMedecinsByVille(ville);
        return ResponseEntity.ok(medecins);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médecin")
    public ResponseEntity<MedecinResponseDTO> updateMedecin(@PathVariable Long id, @Valid @RequestBody MedecinCreateDTO medecinUpdateDTO) {
        try {
            MedecinResponseDTO updatedMedecin = medecinService.updateMedecin(id, medecinUpdateDTO);
            return ResponseEntity.ok(updatedMedecin);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médecin")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        try {
            medecinService.deleteMedecin(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un médecin")
    public ResponseEntity<Void> deactivateMedecin(@PathVariable Long id) {
        try {
            medecinService.deactivateMedecin(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activer un médecin")
    public ResponseEntity<Void> activateMedecin(@PathVariable Long id) {
        try {
            medecinService.activateMedecin(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Compter les médecins actifs")
    public ResponseEntity<Long> countActiveMedecins() {
        long count = medecinService.countActiveMedecins();
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/{id}/creneaux")
    @Operation(summary = "Récupérer les créneaux de disponibilité d'un médecin")
    public ResponseEntity<List<CreneauDisponibiliteDTO>> getCreneauxDisponibilite(@PathVariable Long id) {
        try {
            List<CreneauDisponibiliteDTO> creneaux = medecinService.getCreneauxDisponibilite(id);
            return ResponseEntity.ok(creneaux);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/creneaux")
    @Operation(summary = "Ajouter un créneau de disponibilité à un médecin")
    public ResponseEntity<CreneauDisponibiliteDTO> addCreneauDisponibilite(
            @PathVariable Long id, @Valid @RequestBody CreneauDisponibiliteDTO creneauDTO) {
        try {
            CreneauDisponibiliteDTO createdCreneau = medecinService.addCreneauDisponibilite(id, creneauDTO);
            return new ResponseEntity<>(createdCreneau, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/creneaux/{creneauId}")
    @Operation(summary = "Supprimer un créneau de disponibilité")
    public ResponseEntity<Void> removeCreneauDisponibilite(@PathVariable Long creneauId) {
        try {
            medecinService.removeCreneauDisponibilite(creneauId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}