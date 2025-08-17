package ci.hardwork.gestionrdvservice.web.controller;

import ci.hardwork.gestionrdvservice.core.dto.StatistiquesDTO;
import ci.hardwork.gestionrdvservice.core.service.RendezVousService;
import ci.hardwork.gestionrdvservice.core.service.PatientService;
import ci.hardwork.gestionrdvservice.core.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Administration", description = "API de statistiques et gestion pour les administrateurs")
public class AdminController {
    
    private final RendezVousService rendezVousService;
    private final PatientService patientService;
    private final MedecinService medecinService;
    
    @GetMapping("/stats/globales")
    @Operation(summary = "Récupérer les statistiques globales du système")
    @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès")
    public ResponseEntity<Map<String, Object>> getStatistiquesGlobales() {
        log.info("Récupération des statistiques globales");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques des rendez-vous
        stats.put("totalRendezVous", rendezVousService.countAllRendezVous());
        stats.put("rendezVousPlanifies", rendezVousService.countRendezVousByStatus("PLANIFIE"));
        stats.put("rendezVousConfirmes", rendezVousService.countRendezVousByStatus("CONFIRME"));
        stats.put("rendezVousTermines", rendezVousService.countRendezVousByStatus("TERMINE"));
        stats.put("rendezVousAnnules", rendezVousService.countRendezVousByStatus("ANNULE"));
        
        // Statistiques des utilisateurs
        stats.put("totalPatients", patientService.countAllPatients());
        stats.put("totalMedecins", medecinService.countAllMedecins());
        stats.put("medecinActifs", medecinService.countMedecinsActifs());
        stats.put("patientActifs", patientService.countPatientsActifs());
        
        // Statistiques du jour
        stats.put("rendezVousAujourdhui", rendezVousService.countRendezVousByDate(LocalDate.now()));
        stats.put("nouveauxPatientsAujourdhui", patientService.countPatientsByDateCreation(LocalDate.now()));
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/periode")
    @Operation(summary = "Récupérer les statistiques sur une période")
    public ResponseEntity<Map<String, Object>> getStatistiquesPeriode(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        log.info("Récupération des statistiques pour la période {} - {}", debut, fin);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Convertir en LocalDateTime pour les requêtes
        LocalDateTime debutDateTime = debut.atStartOfDay();
        LocalDateTime finDateTime = fin.atTime(23, 59, 59);
        
        // Statistiques de la période
        stats.put("rendezVousPeriode", rendezVousService.countRendezVousByDateRange(debutDateTime, finDateTime));
        stats.put("nouveauxPatientsPeriode", patientService.countPatientsByDateRange(debutDateTime, finDateTime));
        stats.put("nouveauxMedecinsPeriode", medecinService.countMedecinsByDateRange(debutDateTime, finDateTime));
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/medecins")
    @Operation(summary = "Récupérer les statistiques par médecin")
    public ResponseEntity<Map<String, Object>> getStatistiquesMedecins() {
        log.info("Récupération des statistiques par médecin");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Top médecins par nombre de RDV
        stats.put("topMedecinsParRdv", rendezVousService.getTopMedecinsByRendezVous(10));
        
        // Répartition par spécialité
        stats.put("repartitionSpecialites", medecinService.getRepartitionParSpecialite());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/dashboard")
    @Operation(summary = "Récupérer toutes les données pour le dashboard admin")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("Récupération des données complètes du dashboard admin");
        
        Map<String, Object> dashboard = new HashMap<>();
        
        // Stats globales
        dashboard.put("globales", getStatistiquesGlobales().getBody());
        
        // Stats de la semaine
        LocalDate now = LocalDate.now();
        LocalDate debutSemaine = now.minusDays(7);
        dashboard.put("semaine", getStatistiquesPeriode(debutSemaine, now).getBody());
        
        // Stats du mois
        LocalDate debutMois = now.withDayOfMonth(1);
        dashboard.put("mois", getStatistiquesPeriode(debutMois, now).getBody());
        
        // Évolution mensuelle (6 derniers mois)
        Map<String, Integer> evolutionMensuelle = new HashMap<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate debutMoisI = now.minusMonths(i).withDayOfMonth(1);
            LocalDate finMoisI = debutMoisI.withDayOfMonth(debutMoisI.lengthOfMonth());
            
            int rdvMois = rendezVousService.countRendezVousByDateRange(
                debutMoisI.atStartOfDay(), 
                finMoisI.atTime(23, 59, 59)
            );
            
            String moisNom = debutMoisI.getMonth().name();
            evolutionMensuelle.put(moisNom, rdvMois);
        }
        dashboard.put("evolutionMensuelle", evolutionMensuelle);
        
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/stats/activite-recente")
    @Operation(summary = "Récupérer l'activité récente du système")
    public ResponseEntity<Map<String, Object>> getActiviteRecente() {
        log.info("Récupération de l'activité récente");
        
        Map<String, Object> activite = new HashMap<>();
        
        LocalDateTime il24h = LocalDateTime.now().minusHours(24);
        
        // Activité des dernières 24h
        activite.put("rdvCreesRecemment", rendezVousService.countRendezVousCreatedAfter(il24h));
        activite.put("nouveauxPatientsRecemment", patientService.countPatientsCreatedAfter(il24h));
        activite.put("nouveauxMedecinsRecemment", medecinService.countMedecinsCreatedAfter(il24h));
        
        // Derniers rendez-vous créés
        activite.put("derniersRendezVous", rendezVousService.getRecentRendezVous(10));
        
        return ResponseEntity.ok(activite);
    }
}