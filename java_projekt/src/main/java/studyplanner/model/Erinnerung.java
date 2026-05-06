package studyplanner.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Beschreibt eine Popup-Erinnerung fuer einen Termin oder Lernblock.
 */
public class Erinnerung implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Typ des Eintrags, auf den eine Erinnerung verweist.
     */
    public enum ZielTyp {
        /**
         * Erinnerung fuer einen Termin.
         */
        TERMIN,

        /**
         * Erinnerung fuer einen Lernblock.
         */
        LERNBLOCK
    }

    /**
     * Eindeutige ID der Erinnerung.
     */
    private final UUID id;

    /**
     * Typ des Zieleintrags.
     */
    private ZielTyp zielTyp;

    /**
     * ID des Zieleintrags.
     */
    private UUID zielId;

    /**
     * Vorlaufzeit in Minuten.
     */
    private int vorlaufMinuten;

    /**
     * Gibt an, ob die Erinnerung aktiv ist.
     */
    private boolean aktiv;

    /**
     * Zeitpunkt, zu dem die Erinnerung zuletzt angezeigt wurde.
     */
    private LocalDateTime zuletztAngezeigt;

    /**
     * Erstellt eine neue Erinnerung.
     *
     * @param zielTyp Typ des Zieleintrags.
     * @param zielId ID des Zieleintrags.
     * @param vorlaufMinuten Vorlaufzeit in Minuten.
     * @param aktiv Gibt an, ob die Erinnerung aktiv ist.
     */
    public Erinnerung(ZielTyp zielTyp, UUID zielId, int vorlaufMinuten, boolean aktiv) {
        this.id = UUID.randomUUID();
        this.zielTyp = validateZielTyp(zielTyp);
        this.zielId = validateZielId(zielId);
        this.vorlaufMinuten = validateVorlaufMinuten(vorlaufMinuten);
        this.aktiv = aktiv;
    }

    /**
     * Gibt die eindeutige ID der Erinnerung zurueck.
     *
     * @return Erinnerungs-ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gibt den Zieltyp der Erinnerung zurueck.
     *
     * @return Zieltyp.
     */
    public ZielTyp getZielTyp() {
        return zielTyp;
    }

    /**
     * Setzt den Zieltyp der Erinnerung.
     *
     * @param zielTyp Neuer Zieltyp.
     * @throws IllegalArgumentException wenn kein Zieltyp uebergeben wird.
     */
    public void setZielTyp(ZielTyp zielTyp) {
        this.zielTyp = validateZielTyp(zielTyp);
    }

    /**
     * Gibt die ID des Zieleintrags zurueck.
     *
     * @return Ziel-ID.
     */
    public UUID getZielId() {
        return zielId;
    }

    /**
     * Setzt die ID des Zieleintrags.
     *
     * @param zielId ID des Termins oder Lernblocks.
     * @throws IllegalArgumentException wenn keine Ziel-ID uebergeben wird.
     */
    public void setZielId(UUID zielId) {
        this.zielId = validateZielId(zielId);
    }

    /**
     * Gibt die Vorlaufzeit der Erinnerung zurueck.
     *
     * @return Vorlauf in Minuten.
     */
    public int getVorlaufMinuten() {
        return vorlaufMinuten;
    }

    /**
     * Setzt die Vorlaufzeit der Erinnerung.
     *
     * @param vorlaufMinuten Vorlauf in Minuten zwischen 0 und 10080.
     * @throws IllegalArgumentException wenn der Vorlauf ausserhalb des erlaubten Bereichs liegt.
     */
    public void setVorlaufMinuten(int vorlaufMinuten) {
        this.vorlaufMinuten = validateVorlaufMinuten(vorlaufMinuten);
    }

    /**
     * Prueft, ob die Erinnerung aktiv ist.
     *
     * @return {@code true}, wenn die Erinnerung aktiv ist.
     */
    public boolean isAktiv() {
        return aktiv;
    }

    /**
     * Aktiviert oder deaktiviert die Erinnerung.
     *
     * @param aktiv {@code true}, wenn die Erinnerung aktiv sein soll.
     */
    public void setAktiv(boolean aktiv) {
        this.aktiv = aktiv;
    }

    /**
     * Gibt zurueck, wann die Erinnerung zuletzt angezeigt wurde.
     *
     * @return Zeitpunkt der letzten Anzeige oder {@code null}.
     */
    public LocalDateTime getZuletztAngezeigt() {
        return zuletztAngezeigt;
    }

    /**
     * Speichert, wann die Erinnerung zuletzt angezeigt wurde.
     *
     * @param zuletztAngezeigt Zeitpunkt der letzten Anzeige.
     */
    public void setZuletztAngezeigt(LocalDateTime zuletztAngezeigt) {
        this.zuletztAngezeigt = zuletztAngezeigt;
    }

    private static ZielTyp validateZielTyp(ZielTyp zielTyp) {
        if (zielTyp == null) {
            throw new IllegalArgumentException("Eine Erinnerung braucht einen Zieltyp.");
        }
        return zielTyp;
    }

    private static UUID validateZielId(UUID zielId) {
        if (zielId == null) {
            throw new IllegalArgumentException("Eine Erinnerung braucht ein Ziel.");
        }
        return zielId;
    }

    private static int validateVorlaufMinuten(int vorlaufMinuten) {
        if (vorlaufMinuten < 0 || vorlaufMinuten > 10080) {
            throw new IllegalArgumentException("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.");
        }
        return vorlaufMinuten;
    }
}
