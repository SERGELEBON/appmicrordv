package ci.hardwork.gestionrdvservice.core.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientCreateDTO {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;
    
    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;
    
    @NotBlank(message = "Le numéro de sécurité sociale est obligatoire")
    @Pattern(regexp = "^[0-9]{13}$", message = "Le NSS doit contenir exactement 13 chiffres")
    private String numeroSecuriteSociale;
    
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    
    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Le numéro de téléphone n'est pas valide")
    private String telephone;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresse;
    
    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir 5 chiffres")
    private String codePostal;
    
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 50, message = "La ville ne peut pas dépasser 50 caractères")
    private String ville;
    
    @Size(max = 20, message = "Le groupe sanguin ne peut pas dépasser 20 caractères")
    private String groupeSanguin;
    
    @Size(max = 1000, message = "Les allergies ne peuvent pas dépasser 1000 caractères")
    private String allergies;
    
    @Size(max = 1000, message = "Les antécédents ne peuvent pas dépasser 1000 caractères")
    private String antecedentsMedicaux;
    
    @Size(max = 500, message = "Les traitements ne peuvent pas dépasser 500 caractères")
    private String traitementEnCours;
    
    @Size(max = 100, message = "Le nom du médecin traitant ne peut pas dépasser 100 caractères")
    private String medecinTraitant;
    
    @Size(max = 100, message = "Le nom de la personne à contacter ne peut pas dépasser 100 caractères")
    private String personneAContacter;
    
    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Le téléphone d'urgence n'est pas valide")
    private String telephoneUrgence;
    
    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;
}