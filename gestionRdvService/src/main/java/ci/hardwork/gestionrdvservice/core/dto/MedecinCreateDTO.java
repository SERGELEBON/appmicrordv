package ci.hardwork.gestionrdvservice.core.dto;

import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MedecinCreateDTO {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;
    
    @NotBlank(message = "Le numéro RPPS est obligatoire")
    @Pattern(regexp = "^[0-9]{11}$", message = "Le numéro RPPS doit contenir exactement 11 chiffres")
    private String numeroRPPS;
    
    @NotNull(message = "La spécialité est obligatoire")
    private SpecialiteMedicale specialite;
    
    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    
    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Le numéro de téléphone n'est pas valide")
    private String telephone;
    
    @NotBlank(message = "L'adresse du cabinet est obligatoire")
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresseCabinet;
    
    @NotBlank(message = "Le code postal est obligatoire")
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir 5 chiffres")
    private String codePostalCabinet;
    
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 50, message = "La ville ne peut pas dépasser 50 caractères")
    private String villeCabinet;
    
    @Pattern(regexp = "^[0-9+\\-\\s]{8,15}$", message = "Le téléphone du cabinet n'est pas valide")
    private String telephoneCabinet;
    
    @Email(message = "L'email du cabinet doit être valide")
    private String emailCabinet;
    
    @Min(value = 5, message = "La durée de consultation doit être d'au moins 5 minutes")
    @Max(value = 180, message = "La durée de consultation ne peut pas dépasser 3 heures")
    private Integer dureeConsultationDefaut;
    
    @DecimalMin(value = "0.0", message = "Le tarif ne peut pas être négatif")
    @DecimalMax(value = "9999.99", message = "Le tarif ne peut pas dépasser 9999.99")
    private BigDecimal tarifConsultation;
    
    @Size(max = 1000, message = "Les informations complémentaires ne peuvent pas dépasser 1000 caractères")
    private String informationsComplementaires;
    
    @NotNull(message = "Le statut conventionné est obligatoire")
    private Boolean conventionne;
    
    @NotNull(message = "Le statut carte vitale est obligatoire")
    private Boolean carteVitaleAcceptee;
    
    @Size(max = 500, message = "Les moyens de paiement ne peuvent pas dépasser 500 caractères")
    private String moyensPaiementAcceptes;
}