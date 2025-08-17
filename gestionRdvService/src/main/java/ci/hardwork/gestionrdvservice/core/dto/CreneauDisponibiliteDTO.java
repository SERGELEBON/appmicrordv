package ci.hardwork.gestionrdvservice.core.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class CreneauDisponibiliteDTO {
    
    private Long id;
    
    @NotNull(message = "L'ID du médecin est obligatoire")
    private Long medecinId;
    
    private String medecinNom;
    private String medecinPrenom;
    
    @NotNull(message = "Le jour de la semaine est obligatoire")
    private DayOfWeek jourSemaine;
    
    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;
    
    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;
    
    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean actif;
}