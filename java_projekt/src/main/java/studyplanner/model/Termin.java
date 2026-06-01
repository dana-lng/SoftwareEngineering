package studyplanner.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Beschreibt einen festen Termin eines Moduls.
 */
public class Termin implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String STATUS_OFFEN = "offen";
    public static final String STATUS_ERLEDIGT = "erledigt";

    /**
     * Eindeutige ID des Termins.
     */
    private final UUID id;

    /**
     * ID des zugeordneten Moduls.
     */
    private UUID modulId;

    /**
     * Titel des Termins.
     */
    private String titel;

    /**
     * Datum des Termins.
     */
    private LocalDate datum;

    /**
     * Uhrzeit des Termins.
     */
    private LocalTime uhrzeit;

    /**
     * Optionale Notizen zum Termin.
     */
    private String notizen;

    /**
     * Bearbeitungsstatus des Termins.
     */
    private String status;

    /**
     * Erstellt einen neuen Termin.
     *
     * @param modulId ID des zugeordneten Moduls.
     * @param titel Titel des Termins.
     * @param datum Datum des Termins.
     * @param uhrzeit Uhrzeit des Termins.
     * @param notizen Optionale Notizen.
     */
    public Termin(UUID modulId, String titel, LocalDate datum, LocalTime uhrzeit, String notizen) {
        this.id = UUID.randomUUID();
        this.modulId = validateModulId(modulId);
        this.titel = validateTitel(titel);
        this.datum = validateDatum(datum);
        this.uhrzeit = validateUhrzeit(uhrzeit);
        this.notizen = normalizeText(notizen);
        this.status = STATUS_OFFEN;
    }

    /**
     * Gibt die eindeutige ID des Termins zurueck.
     *
     * @return Termin-ID.
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
     * Gibt den Titel des Termins zurueck.
     *
     * @return Termintitel.
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Setzt den Titel des Termins.
     *
     * @param titel Neuer Titel.
     * @throws IllegalArgumentException wenn der Titel leer ist.
     */
    public void setTitel(String titel) {
        this.titel = validateTitel(titel);
    }

    /**
     * Gibt das Datum des Termins zurueck.
     *
     * @return Termin-Datum.
     */
    public LocalDate getDatum() {
        return datum;
    }

    /**
     * Setzt das Datum des Termins.
     *
     * @param datum Neues Datum.
     * @throws IllegalArgumentException wenn kein Datum uebergeben wird.
     */
    public void setDatum(LocalDate datum) {
        this.datum = validateDatum(datum);
    }

    /**
     * Gibt die Uhrzeit des Termins zurueck.
     *
     * @return Termin-Uhrzeit.
     */
    public LocalTime getUhrzeit() {
        return uhrzeit;
    }

    /**
     * Setzt die Uhrzeit des Termins.
     *
     * @param uhrzeit Neue Uhrzeit.
     * @throws IllegalArgumentException wenn keine Uhrzeit uebergeben wird.
     */
    public void setUhrzeit(LocalTime uhrzeit) {
        this.uhrzeit = validateUhrzeit(uhrzeit);
    }

    /**
     * Gibt die Notizen zum Termin zurueck.
     *
     * @return Notiztext oder leerer Text.
     */
    public String getNotizen() {
        return notizen;
    }

    /**
     * Setzt die Notizen zum Termin.
     *
     * @param notizen Neuer Notiztext.
     */
    public void setNotizen(String notizen) {
        this.notizen = normalizeText(notizen);
    }

    /**
     * Gibt den aktuellen Status des Termins zurueck.
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
     * Setzt den Status des Termins.
     *
     * @param status Neuer Status.
     * @throws IllegalArgumentException wenn der Status nicht erlaubt ist.
     */
    public void setStatus(String status) {
        this.status = validateStatus(status);
    }

    private static UUID validateModulId(UUID modulId) {
        if (modulId == null) {
            throw new IllegalArgumentException("Ein Termin braucht ein Modul.");
        }
        return modulId;
    }

    private static String validateTitel(String titel) {
        if (titel == null || titel.isBlank()) {
            throw new IllegalArgumentException("Der Termintitel darf nicht leer sein.");
        }
        return titel.trim();
    }

    private static LocalDate validateDatum(LocalDate datum) {
        if (datum == null) {
            throw new IllegalArgumentException("Ein Termin braucht ein Datum.");
        }
        return datum;
    }

    private static LocalTime validateUhrzeit(LocalTime uhrzeit) {
        if (uhrzeit == null) {
            throw new IllegalArgumentException("Ein Termin braucht eine Uhrzeit.");
        }
        return uhrzeit;
    }

    private static String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }

    private static String validateStatus(String status) {
        if (STATUS_OFFEN.equals(status) || STATUS_ERLEDIGT.equals(status)) {
            return status;
        }
        throw new IllegalArgumentException("Der Status muss offen oder erledigt sein.");
    }
}
