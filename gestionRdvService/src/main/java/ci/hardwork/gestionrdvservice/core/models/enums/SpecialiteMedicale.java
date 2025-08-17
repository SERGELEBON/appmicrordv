package ci.hardwork.gestionrdvservice.core.models.enums;

public enum SpecialiteMedicale {
    GENERALISTE("Médecin généraliste"),
    CARDIOLOGIE("Cardiologue"),
    DERMATOLOGIE("Dermatologue"),
    NEUROLOGIE("Neurologue"),
    PEDIATRIE("Pédiatre"),
    GYNECOLOGIE("Gynécologue"),
    OPHTALMOLOGIE("Ophtalmologue"),
    ORL("ORL"),
    PSYCHIATRIE("Psychiatre"),
    RADIOLOGIE("Radiologue"),
    CHIRURGIE("Chirurgien"),
    ANESTHESIE("Anesthésiste"),
    DENTAIRE("Dentiste");

    private final String displayName;

    SpecialiteMedicale(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}