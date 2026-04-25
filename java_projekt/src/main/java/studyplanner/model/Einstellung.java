package studyplanner.model;

import java.io.Serializable;

/**
 * Speichert globale Benutzereinstellungen der Anwendung.
 */
public class Einstellung implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Standard-Vorlauf fuer neue Erinnerungen in Minuten.
     */
    private int standardVorlaufMinuten = 30;

    /**
     * Gibt an, ob Erinnerungen global eingeschaltet sind.
     */
    private boolean erinnerungenAktiv = true;

    /**
     * Erstellt Einstellungen mit Standardwerten.
     */
    public Einstellung() {
    }

    /**
     * Gibt den Standard-Vorlauf fuer neue Erinnerungen zurueck.
     *
     * @return Vorlauf in Minuten.
     */
    public int getStandardVorlaufMinuten() {
        return standardVorlaufMinuten;
    }

    /**
     * Setzt den Standard-Vorlauf fuer neue Erinnerungen.
     *
     * @param standardVorlaufMinuten Vorlauf in Minuten zwischen 0 und 10080.
     * @throws IllegalArgumentException wenn der Vorlauf ausserhalb des erlaubten Bereichs liegt.
     */
    public void setStandardVorlaufMinuten(int standardVorlaufMinuten) {
        if (standardVorlaufMinuten < 0 || standardVorlaufMinuten > 10080) {
            throw new IllegalArgumentException("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.");
        }
        this.standardVorlaufMinuten = standardVorlaufMinuten;
    }

    /**
     * Prueft, ob Erinnerungen grundsaetzlich aktiv sind.
     *
     * @return {@code true}, wenn Popup-Erinnerungen aktiv sind.
     */
    public boolean isErinnerungenAktiv() {
        return erinnerungenAktiv;
    }

    /**
     * Aktiviert oder deaktiviert Popup-Erinnerungen global.
     *
     * @param erinnerungenAktiv {@code true}, wenn Erinnerungen aktiv sein sollen.
     */
    public void setErinnerungenAktiv(boolean erinnerungenAktiv) {
        this.erinnerungenAktiv = erinnerungenAktiv;
    }
}
