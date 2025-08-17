package ci.hardwork.gestionrdvservice.core.models;

import ci.hardwork.gestionrdvservice.core.models.enums.RendezVousStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le patient est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Le médecin est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @NotNull(message = "La date et heure de début sont obligatoires")
    @Column(name = "date_heure_debut", nullable = false)
    private LocalDateTime dateHeureDebut;
    
    @NotNull(message = "La date et heure de fin sont obligatoires")
    @Column(name = "date_heure_fin", nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(name = "duree_prevue_minutes")
    private Integer dureePrevueMinutes = 30;

    @NotNull(message = "Le statut est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, columnDefinition = "VARCHAR(20)")
    private RendezVousStatus statut = RendezVousStatus.PLANIFIE;

    @Size(max = 100, message = "Le motif ne peut pas dépasser 100 caractères")
    @Column(name = "motif_consultation")
    private String motifConsultation;

    @Column(name = "notes_patient", columnDefinition = "TEXT")
    private String notesPatient;

    @Column(name = "notes_medecin", columnDefinition = "TEXT")
    private String notesMedecin;

    @Column(name = "diagnostic", columnDefinition = "TEXT")
    private String diagnostic;

    @Column(name = "prescription", columnDefinition = "TEXT")
    private String prescription;

    @Column(name = "examens_demandes", columnDefinition = "TEXT")
    private String examensDemanges;

    @Column(name = "date_heure_debut_consultation")
    private LocalDateTime dateHeureDebutConsultation;

    @Column(name = "date_heure_fin_consultation")
    private LocalDateTime dateHeureFinConsultation;

    @Column(name = "tarif_applique", precision = 10, scale = 2)
    private java.math.BigDecimal tarif;

    @Column(name = "mode_paiement")
    private String modePaiement;

    @Column(name = "rappel_envoye")
    private Boolean rappelEnvoye = false;

    @Column(name = "date_rappel")
    private LocalDateTime dateRappel;

    @Column(name = "motif_annulation")
    private String motifAnnulation;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public boolean isConfirme() {
        return RendezVousStatus.CONFIRME.equals(statut);
    }

    public boolean isAnnule() {
        return RendezVousStatus.ANNULE.equals(statut);
    }

    public boolean isTermine() {
        return RendezVousStatus.TERMINE.equals(statut);
    }

    public boolean isPlanifie() {
        return RendezVousStatus.PLANIFIE.equals(statut);
    }

    public String getRendezVousInfo() {
        return String.format("RDV %s - %s avec %s le %s", 
                id, 
                patient != null ? patient.getNom() + " " + patient.getPrenom() : "Patient inconnu",
                medecin != null ? medecin.getNom() + " " + medecin.getPrenom() : "Médecin inconnu",
                dateHeureDebut != null ? dateHeureDebut.toString() : "Date inconnue");
    }
}