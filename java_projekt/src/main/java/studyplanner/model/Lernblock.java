package studyplanner.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Beschreibt eine geplante Lernphase fuer ein Modul.
 */
public class Lernblock implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String STATUS_OFFEN = "offen";
    public static final String STATUS_ERLEDIGT = "erledigt";

    /**
     * Eindeutige ID des Lernblocks.
     */
    private final UUID id;

    /**
     * ID des zugeordneten Moduls.
     */
    private UUID modulId;

    /**
     * Thema des Lernblocks.
     */
    private String thema;

    /**
     * Datum des Lernblocks.
     */
    private LocalDate datum;

    /**
     * Startzeit des Lernblocks.
     */
    private LocalTime startzeit;

    /**
     * Dauer des Lernblocks in Minuten.
     */
    private int dauerMinuten;

    /**
     * Bearbeitungsstatus des Lernblocks.
     */
    private String status;

    /**
     * Erstellt einen neuen Lernblock.
     *
     * @param modulId ID des zugeordneten Moduls.
     * @param thema Thema des Lernblocks.
     * @param datum Datum des Lernblocks.
     * @param startzeit Startzeit des Lernblocks.
     * @param dauerMinuten Dauer in Minuten.
     */
    public Lernblock(UUID modulId, String thema, LocalDate datum, LocalTime startzeit, int dauerMinuten) {
        this.id = UUID.randomUUID();
        this.modulId = validateModulId(modulId);
        this.thema = validateThema(thema);
        this.datum = validateDatum(datum);
        this.startzeit = validateStartzeit(startzeit);
        this.dauerMinuten = validateDauerMinuten(dauerMinuten);
        this.status = STATUS_OFFEN;
    }

    /**
     * Gibt die eindeutige ID des Lernblocks zurueck.
     *
     * @return Lernblock-ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gibt die ID des zugeordneten Moduls zurueck.
     *
     * @return Modul-ID.
     */
    public UUID getModulId() {
        return modulId;
    }

    /**
     * Setzt das zugeordnete Modul.
     *
     * @param modulId ID des Moduls.
     * @throws IllegalArgumentException wenn keine Modul-ID uebergeben wird.
     */
    public void setModulId(UUID modulId) {
        this.modulId = validateModulId(modulId);
    }

    /**
     * Gibt das Thema des Lernblocks zurueck.
     *
     * @return Thema.
     */
    public String getThema() {
        return thema;
    }

    /**
     * Setzt das Thema des Lernblocks.
     *
     * @param thema Neues Thema.
     * @throws IllegalArgumentException wenn das Thema leer ist.
     */
    public void setThema(String thema) {
        this.thema = validateThema(thema);
    }

    /**
     * Gibt das Datum des Lernblocks zurueck.
     *
     * @return Datum.
     */
    public LocalDate getDatum() {
        return datum;
    }

    /**
     * Setzt das Datum des Lernblocks.
     *
     * @param datum Neues Datum.
     * @throws IllegalArgumentException wenn kein Datum uebergeben wird.
     */
    public void setDatum(LocalDate datum) {
        this.datum = validateDatum(datum);
    }

    /**
     * Gibt die Startzeit des Lernblocks zurueck.
     *
     * @return Startzeit.
     */
    public LocalTime getStartzeit() {
        return startzeit;
    }

    /**
     * Setzt die Startzeit des Lernblocks.
     *
     * @param startzeit Neue Startzeit.
     * @throws IllegalArgumentException wenn keine Startzeit uebergeben wird.
     */
    public void setStartzeit(LocalTime startzeit) {
        this.startzeit = validateStartzeit(startzeit);
    }

    /**
     * Gibt die Dauer des Lernblocks zurueck.
     *
     * @return Dauer in Minuten.
     */
    public int getDauerMinuten() {
        return dauerMinuten;
    }

    /**
     * Setzt die Dauer des Lernblocks.
     *
     * @param dauerMinuten Dauer in Minuten zwischen 1 und 1440.
     * @throws IllegalArgumentException wenn die Dauer ausserhalb des erlaubten Bereichs liegt.
     */
    public void setDauerMinuten(int dauerMinuten) {
        this.dauerMinuten = validateDauerMinuten(dauerMinuten);
    }

    /**
     * Gibt den aktuellen Status des Lernblocks zurueck.
     *
     * @return Status offen oder erledigt.
     */
    public String getStatus() {
        if (status == null || status.isBlank()) {
            return STATUS_OFFEN;
        }
        return status;
    }

    /**
     * Setzt den Status des Lernblocks.
     *
     * @param status Neuer Status.
     * @throws IllegalArgumentException wenn der Status nicht erlaubt ist.
     */
    public void setStatus(String status) {
        this.status = validateStatus(status);
    }

    private static UUID validateModulId(UUID modulId) {
        if (modulId == null) {
            throw new IllegalArgumentException("Ein Lernblock braucht ein Modul.");
        }
        return modulId;
    }

    private static String validateThema(String thema) {
        if (thema == null || thema.isBlank()) {
            throw new IllegalArgumentException("Das Thema darf nicht leer sein.");
        }
        return thema.trim();
    }

    private static LocalDate validateDatum(LocalDate datum) {
        if (datum == null) {
            throw new IllegalArgumentException("Ein Lernblock braucht ein Datum.");
        }
        return datum;
    }

    private static LocalTime validateStartzeit(LocalTime startzeit) {
        if (startzeit == null) {
            throw new IllegalArgumentException("Ein Lernblock braucht eine Startzeit.");
        }
        return startzeit;
    }

    private static int validateDauerMinuten(int dauerMinuten) {
        if (dauerMinuten <= 0 || dauerMinuten > 1440) {
            throw new IllegalArgumentException("Die Dauer muss zwischen 1 und 1440 Minuten liegen.");
        }
        return dauerMinuten;
    }

    private static String validateStatus(String status) {
        if (STATUS_OFFEN.equals(status) || STATUS_ERLEDIGT.equals(status)) {
            return status;
        }
        throw new IllegalArgumentException("Der Status muss offen oder erledigt sein.");
    }
}
