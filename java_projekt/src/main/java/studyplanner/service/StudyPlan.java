package studyplanner.service;

import studyplanner.model.Einstellung;
import studyplanner.model.Erinnerung;
import studyplanner.model.Lernblock;
import studyplanner.model.Modul;
import studyplanner.model.Termin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Zentrale Datenstruktur, die alle gespeicherten Anwendungsdaten enthaelt.
 */
public class StudyPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Gespeicherte Module.
     */
    private final List<Modul> module = new ArrayList<>();

    /**
     * Gespeicherte Termine.
     */
    private final List<Termin> termine = new ArrayList<>();

    /**
     * Gespeicherte Lernbloecke.
     */
    private final List<Lernblock> lernbloecke = new ArrayList<>();

    /**
     * Gespeicherte Erinnerungen.
     */
    private final List<Erinnerung> erinnerungen = new ArrayList<>();

    /**
     * Globale Einstellungen.
     */
    private final Einstellung einstellung = new Einstellung();

    /**
     * Erstellt einen leeren Study-Plan.
     */
    public StudyPlan() {
    }

    /**
     * Gibt die gespeicherten Module zurueck.
     *
     * @return Veraenderbare Liste der Module.
     */
    public List<Modul> getModule() {
        return module;
    }

    /**
     * Gibt die gespeicherten Termine zurueck.
     *
     * @return Veraenderbare Liste der Termine.
     */
    public List<Termin> getTermine() {
        return termine;
    }

    /**
     * Gibt die gespeicherten Lernbloecke zurueck.
     *
     * @return Veraenderbare Liste der Lernbloecke.
     */
    public List<Lernblock> getLernbloecke() {
        return lernbloecke;
    }

    /**
     * Gibt die gespeicherten Erinnerungen zurueck.
     *
     * @return Veraenderbare Liste der Erinnerungen.
     */
    public List<Erinnerung> getErinnerungen() {
        return erinnerungen;
    }

    /**
     * Gibt die globalen Einstellungen zurueck.
     *
     * @return Einstellungen der Anwendung.
     */
    public Einstellung getEinstellung() {
        return einstellung;
    }
}
