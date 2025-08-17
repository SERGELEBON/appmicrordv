package ci.hardwork.gestionrdvservice.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "Le numéro de sécurité sociale est obligatoire")
    @Size(min = 13, max = 15, message = "Le numéro de sécurité sociale doit contenir entre 13 et 15 caractères")
    @Column(name = "numero_securite_sociale", unique = true, nullable = false)
    private String numeroSecuriteSociale;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    @Column(name = "nom", nullable = false)
    private String nom;

    @Past(message = "La date de naissance doit être dans le passé")
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Size(max = 10, message = "Le sexe ne peut pas dépasser 10 caractères")
    private String sexe;

    @Size(max = 15, message = "Le téléphone ne peut pas dépasser 15 caractères")
    private String telephone;

    @Email(message = "L'email doit avoir un format valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresse;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Column(name = "code_postal")
    private String codePostal;

    @Size(max = 50, message = "La ville ne peut pas dépasser 50 caractères")
    private String ville;

    @Size(max = 50, message = "Le pays ne peut pas dépasser 50 caractères")
    private String pays;

    @Size(max = 100, message = "La mutuelle ne peut pas dépasser 100 caractères")
    private String mutuelle;

    @Size(max = 20, message = "Le numéro de mutuelle ne peut pas dépasser 20 caractères")
    @Column(name = "numero_mutuelle")
    private String numeroMutuelle;

    @Column(name = "antecedents_medicaux", columnDefinition = "TEXT")
    private String antecedentsMedicaux;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "traitements_actuels", columnDefinition = "TEXT")
    private String traitementsActuels;

    @Column(name = "personne_contact_nom")
    private String personneContactNom;

    @Column(name = "personne_contact_telephone")
    private String personneContactTelephone;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVousList;

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
        return prenom + " " + nom;
    }

    public int getAge() {
        if (dateNaissance == null) {
            return 0;
        }
        return LocalDate.now().getYear() - dateNaissance.getYear();
    }
}