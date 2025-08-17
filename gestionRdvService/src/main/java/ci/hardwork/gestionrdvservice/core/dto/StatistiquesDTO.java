package ci.hardwork.gestionrdvservice.core.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatistiquesDTO {
    
    // Statistiques générales
    private Long totalRendezVous;
    private Long totalPatients;
    private Long totalMedecins;
    
    // Statistiques des rendez-vous
    private Long rendezVousPlanifies;
    private Long rendezVousConfirmes;
    private Long rendezVousTermines;
    private Long rendezVousAnnules;
    
    // Statistiques du jour
    private Long rendezVousAujourdhui;
    private Long nouveauxPatientsAujourdhui;
    
    // Statistiques d'activité
    private Long medecinActifs;
    private Long patientActifs;
    
    // Ratios et pourcentages
    private Double tauxAnnulation;
    private Double tauxCompletion;
    private Double moyenneRendezVousParMedecin;
    private Double moyenneRendezVousParPatient;
}