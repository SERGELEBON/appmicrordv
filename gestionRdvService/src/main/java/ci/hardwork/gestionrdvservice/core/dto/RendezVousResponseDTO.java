package ci.hardwork.gestionrdvservice.core.dto;

import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RendezVousResponseDTO {
    
    private Long id;
    private Long patientId;
    private String patientNom;
    private String patientPrenom;
    private Long medecinId;
    private String medecinNom;
    private String medecinPrenom;
    private String medecinSpecialite;
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private String motifConsultation;
    private String symptomes;
    private RendezVousStatus statut;
    private String notes;
    private BigDecimal tarif;
    private Boolean rappelEnvoye;
    private LocalDateTime dateRappel;
    private String examensDemanges;
    private String traitementPrescrit;
    private String observations;
    private String prochainRendezVous;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}