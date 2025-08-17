package ci.hardwork.gestionrdvservice.core.dto;

import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RendezVousCreateDTO {
    
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;
    
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;
    
    @NotNull(message = "La date et l'heure sont obligatoires")
    @Future(message = "La date du rendez-vous doit être dans le futur")
    private LocalDateTime dateHeureDebut;
    
    @NotNull(message = "La date et l'heure de fin sont obligatoires")
    @Future(message = "La date de fin du rendez-vous doit être dans le futur")
    private LocalDateTime dateHeureFin;
    
    @NotBlank(message = "Le motif de consultation est obligatoire")
    @Size(min = 5, max = 500, message = "Le motif doit contenir entre 5 et 500 caractères")
    private String motifConsultation;
    
    @Size(max = 1000, message = "Les symptômes ne peuvent pas dépasser 1000 caractères")
    private String symptomes;
    
    @NotNull(message = "Le statut est obligatoire")
    private RendezVousStatus statut;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
    
    @DecimalMin(value = "0.0", message = "Le tarif ne peut pas être négatif")
    @DecimalMax(value = "9999.99", message = "Le tarif ne peut pas dépasser 9999.99")
    private BigDecimal tarif;
    
    @NotNull(message = "Le statut de rappel est obligatoire")
    private Boolean rappelEnvoye;
    
    @Size(max = 500, message = "Les examens demandés ne peuvent pas dépasser 500 caractères")
    private String examensDemandesw;
    
    @Size(max = 1000, message = "Le traitement prescrit ne peut pas dépasser 1000 caractères")
    private String traitementPrescrit;
    
    @Size(max = 1000, message = "Les observations ne peuvent pas dépasser 1000 caractères")
    private String observations;
    
    @Size(max = 500, message = "Le prochain rendez-vous ne peut pas dépasser 500 caractères")
    private String prochainRendezVous;
}