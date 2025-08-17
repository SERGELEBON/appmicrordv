package ci.hardwork.gestionrdvservice.core.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientResponseDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String numeroSecuriteSociale;
    private String email;
    private String telephone;
    private String adresse;
    private String codePostal;
    private String ville;
    private String groupeSanguin;
    private String allergies;
    private String antecedentsMedicaux;
    private String traitementEnCours;
    private String medecinTraitant;
    private String personneAContacter;
    private String telephoneUrgence;
    private String notes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean actif;
}