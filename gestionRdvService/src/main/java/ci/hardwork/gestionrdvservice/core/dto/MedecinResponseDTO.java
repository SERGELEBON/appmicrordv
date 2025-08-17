package ci.hardwork.gestionrdvservice.core.dto;

import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MedecinResponseDTO {
    
    private Long id;
    private String nom;
    private String prenom;
    private String numeroRPPS;
    private SpecialiteMedicale specialite;
    private String email;
    private String telephone;
    private String adresseCabinet;
    private String codePostalCabinet;
    private String villeCabinet;
    private String telephoneCabinet;
    private String emailCabinet;
    private Integer dureeConsultationDefaut;
    private BigDecimal tarifConsultation;
    private String informationsComplementaires;
    private Boolean conventionne;
    private Boolean carteVitaleAcceptee;
    private String moyensPaiementAcceptes;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private boolean actif;
}