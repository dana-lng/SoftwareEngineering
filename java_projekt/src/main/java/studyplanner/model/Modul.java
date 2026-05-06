package studyplanner.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Beschreibt ein Studienmodul, dem Termine und Lernbloecke zugeordnet werden.
 */
public class Modul implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Eindeutige ID des Moduls.
     */
    private final UUID id;

    /**
     * Anzeigename des Moduls.
     */
    private String name;

    /**
     * Farbe des Moduls als Textwert.
     */
    private String farbe;

    /**
     * Semester, in dem das Modul belegt wird.
     */
    private int semester;

    /**
     * Optionale Notizen zum Modul.
     */
    private String notizen;

    /**
     * Erstellt ein neues Modul mit Semester.
     *
     * @param name Name des Moduls.
     * @param farbe Optionale Farbe zur visuellen Unterscheidung.
     * @param notizen Optionale Notizen zum Modul.
     * @param semester Semester zwischen 1 und 12.
     */
    public Modul(String name, String farbe, String notizen, int semester) {
        this.id = UUID.randomUUID();
        this.name = validateName(name);
        this.farbe = normalizeFarbe(farbe);
        this.semester = validateSemester(semester);
        this.notizen = normalizeText(notizen);
    }

    /**
     * Gibt die eindeutige ID des Moduls zurueck.
     *
     * @return Modul-ID.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gibt den Modulnamen zurueck.
     *
     * @return Name des Moduls.
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Modulnamen.
     *
     * @param name Neuer Modulname.
     * @throws IllegalArgumentException wenn der Name leer ist.
     */
    public void setName(String name) {
        this.name = validateName(name);
    }

    /**
     * Gibt die gespeicherte Modulfarbe zurueck.
     *
     * @return Farbe als Textwert, zum Beispiel ein Hex-Wert.
     */
    public String getFarbe() {
        return farbe;
    }

    /**
     * Setzt die Farbe des Moduls.
     *
     * @param farbe Farbe als Textwert; bei leerem Wert wird eine Standardfarbe verwendet.
     */
    public void setFarbe(String farbe) {
        this.farbe = normalizeFarbe(farbe);
    }

    /**
     * Gibt das Semester des Moduls zurueck.
     *
     * @return Semesterzahl.
     */
    public int getSemester() {
        return semester <= 0 ? 1 : semester;
    }

    /**
     * Setzt das Semester des Moduls.
     *
     * @param semester Semester zwischen 1 und 12.
     * @throws IllegalArgumentException wenn das Semester ausserhalb des erlaubten Bereichs liegt.
     */
    public void setSemester(int semester) {
        this.semester = validateSemester(semester);
    }

    /**
     * Gibt die Notizen zum Modul zurueck.
     *
     * @return Notiztext oder leerer Text.
     */
    public String getNotizen() {
        return notizen;
    }

    /**
     * Setzt die Notizen zum Modul.
     *
     * @param notizen Neuer Notiztext.
     */
    public void setNotizen(String notizen) {
        this.notizen = normalizeText(notizen);
    }

    /**
     * Gibt den Modulnamen fuer UI-Elemente wie ComboBoxen zurueck.
     *
     * @return Name des Moduls.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Vergleicht Module anhand ihrer eindeutigen ID.
     *
     * @param o Vergleichsobjekt.
     * @return {@code true}, wenn beide Objekte dasselbe Modul darstellen.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Modul modul)) {
            return false;
        }
        return id.equals(modul.id);
    }

    /**
     * Erzeugt den Hash-Wert anhand der eindeutigen ID.
     *
     * @return Hash-Wert des Moduls.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Der Modulname darf nicht leer sein.");
        }
        return name.trim();
    }

    private static String normalizeFarbe(String farbe) {
        return farbe == null || farbe.isBlank() ? "#4F7CAC" : farbe.trim();
    }

    private static String normalizeText(String text) {
        return text == null ? "" : text.trim();
    }

    private static int validateSemester(int semester) {
        if (semester < 1 || semester > 12) {
            throw new IllegalArgumentException("Das Semester muss zwischen 1 und 12 liegen.");
        }
        return semester;
    }
}
