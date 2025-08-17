package ci.hardwork.gestionrdvservice.core.models;

import ci.hardwork.gestionrdvservice.core.models.enums.SpecialiteMedicale;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "medecins")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Medecin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "Le numéro RPPS est obligatoire")
    @Size(min = 11, max = 11, message = "Le numéro RPPS doit contenir exactement 11 caractères")
    @Column(name = "numero_rpps", unique = true, nullable = false)
    private String numeroRPPS;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull(message = "La spécialité est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "specialite", nullable = false, columnDefinition = "VARCHAR(50)")
    private SpecialiteMedicale specialite;

    @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
    private String telephone;

    @Email(message = "L'email doit avoir un format valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    @Column(name = "adresse_cabinet")
    private String adresseCabinet;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Column(name = "code_postal_cabinet")
    private String codePostalCabinet;

    @Size(max = 50, message = "La ville ne peut pas dépasser 50 caractères")
    @Column(name = "ville_cabinet")
    private String villeCabinet;

    @Column(name = "telephone_cabinet")
    private String telephoneCabinet;

    @Column(name = "horaires_consultation", columnDefinition = "TEXT")
    private String horairesConsultation;

    @Column(name = "duree_consultation_defaut")
    private Integer dureeConsultationDefaut = 30;

    @Column(name = "tarif", precision = 10, scale = 2)
    private java.math.BigDecimal tarif;
    
    @Column(name = "conventionne")
    private Boolean conventionne = true;
    
    @Column(name = "carte_vitale_acceptee")
    private Boolean carteVitaleAcceptee = true;

    @Column(name = "secteur_conventionnement")
    private String secteurConventionnement;

    @Column(name = "accepte_carte_vitale")
    private Boolean accepteCarteVitale = true;

    @Column(name = "presentation", columnDefinition = "TEXT")
    private String presentation;

    @Column(name = "diplomes", columnDefinition = "TEXT")
    private String diplomes;

    @Column(name = "langues_parlees")
    private String languesParlees;

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList;

    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CreneauDisponibilite> creneauxDisponibilite;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public String getNomComplet() {
        return "Dr. " + prenom + " " + nom;
    }

    public String getAdresseComplete() {
        if (adresseCabinet == null) return null;
        StringBuilder sb = new StringBuilder(adresseCabinet);
        if (codePostalCabinet != null) {
            sb.append(", ").append(codePostalCabinet);
        }
        if (villeCabinet != null) {
            sb.append(" ").append(villeCabinet);
        }
        return sb.toString();
    }
}