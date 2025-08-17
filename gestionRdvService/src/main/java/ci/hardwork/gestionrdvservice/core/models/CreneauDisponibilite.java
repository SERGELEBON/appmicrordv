package ci.hardwork.gestionrdvservice.core.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "creneaux_disponibilite")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreneauDisponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Le médecin est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Medecin medecin;

    @NotNull(message = "Le jour de la semaine est obligatoire")
    @Enumerated(EnumType.STRING)
    @Column(name = "jour_semaine", nullable = false, columnDefinition = "VARCHAR(20)")
    private DayOfWeek jourSemaine;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        dateCreation = LocalDateTime.now();
        dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dateModification = LocalDateTime.now();
    }

    public boolean isDisponibleACetteHeure(LocalTime heure) {
        return heure.isAfter(heureDebut) && heure.isBefore(heureFin);
    }

    public String getCreneauFormate() {
        return jourSemaine.name() + " " + heureDebut + " - " + heureFin;
    }
}