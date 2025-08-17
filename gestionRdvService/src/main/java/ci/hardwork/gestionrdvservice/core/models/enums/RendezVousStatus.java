package ci.hardwork.gestionrdvservice.core.models.enums;

public enum RendezVousStatus {
    PLANIFIE("Planifié"),
    CONFIRME("Confirmé"), 
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    ANNULE("Annulé"),
    ABSENT("Absent");

    private final String displayName;

    RendezVousStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}