package studyplanner.service;

import studyplanner.model.Einstellung;
import studyplanner.model.Erinnerung;
import studyplanner.model.Lernblock;
import studyplanner.model.Modul;
import studyplanner.model.Termin;
import studyplanner.persistence.Storage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Zentrale Serviceschicht fuer alle fachlichen Aktionen der Anwendung.
 */
public class StudyService {
    private final Storage storage;
    private final StudyPlan studyPlan;

    /**
     * Erstellt den Service und laedt vorhandene Daten aus dem Speicher.
     *
     * @param storage Speicherkomponente fuer lokale Daten.
     */
    public StudyService(Storage storage) {
        this.storage = storage;
        this.studyPlan = storage.load();
    }

    /**
     * Gibt alle Module sortiert nach Namen zurueck.
     *
     * @return Sortierte Modulliste.
     */
    public List<Modul> getModule() {
        return studyPlan.getModule().stream()
                .sorted(Comparator.comparing(Modul::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Gibt alle Termine sortiert nach Datum und Uhrzeit zurueck.
     *
     * @return Sortierte Terminliste.
     */
    public List<Termin> getTermine() {
        return studyPlan.getTermine().stream()
                .sorted(Comparator.comparing(Termin::getDatum).thenComparing(Termin::getUhrzeit))
                .toList();
    }

    /**
     * Gibt alle Lernbloecke sortiert nach Datum und Startzeit zurueck.
     *
     * @return Sortierte Lernblockliste.
     */
    public List<Lernblock> getLernbloecke() {
        return studyPlan.getLernbloecke().stream()
                .sorted(Comparator.comparing(Lernblock::getDatum).thenComparing(Lernblock::getStartzeit))
                .toList();
    }

    /**
     * Gibt alle Erinnerungen als unveraenderliche Kopie zurueck.
     *
     * @return Liste der Erinnerungen.
     */
    public List<Erinnerung> getErinnerungen() {
        return List.copyOf(studyPlan.getErinnerungen());
    }

    /**
     * Gibt die globalen Einstellungen zurueck.
     *
     * @return Einstellungen der Anwendung.
     */
    public Einstellung getEinstellung() {
        return studyPlan.getEinstellung();
    }

    /**
     * Fuegt ein neues Modul mit Semester hinzu.
     *
     * @param name Name des Moduls.
     * @param farbe Farbe des Moduls.
     * @param notizen Optionale Notizen.
     * @param semester Semester des Moduls.
     * @return Neu angelegtes Modul.
     */
    public Modul addModul(String name, String farbe, String notizen, int semester) {
        ensureUniqueModuleName(null, name);
        Modul modul = new Modul(name, farbe, notizen, semester);
        studyPlan.getModule().add(modul);
        save();
        return modul;
    }

    /**
     * Aktualisiert ein vorhandenes Modul inklusive Semester.
     *
     * @param id ID des Moduls.
     * @param name Neuer Name.
     * @param farbe Neue Farbe.
     * @param notizen Neue Notizen.
     * @param semester Neues Semester.
     */
    public void updateModul(UUID id, String name, String farbe, String notizen, int semester) {
        Modul modul = findModul(id).orElseThrow(() -> new IllegalArgumentException("Modul wurde nicht gefunden."));
        ensureUniqueModuleName(id, name);
        modul.setName(name);
        modul.setFarbe(farbe);
        modul.setSemester(semester);
        modul.setNotizen(notizen);
        save();
    }

    /**
     * Loescht ein Modul, sofern keine Termine oder Lernbloecke mehr darauf verweisen.
     *
     * @param id ID des zu loeschenden Moduls.
     */
    public void deleteModul(UUID id) {
        boolean inUse = studyPlan.getTermine().stream().anyMatch(termin -> termin.getModulId().equals(id))
                || studyPlan.getLernbloecke().stream().anyMatch(block -> block.getModulId().equals(id));
        if (inUse) {
            throw new IllegalArgumentException("Das Modul kann nicht geloescht werden, solange Termine oder Lernbloecke darauf verweisen.");
        }
        studyPlan.getModule().removeIf(modul -> modul.getId().equals(id));
        save();
    }

    /**
     * Fuegt einen neuen Termin inklusive Erinnerung hinzu.
     *
     * @param modulId ID des zugeordneten Moduls.
     * @param titel Titel des Termins.
     * @param datum Datum des Termins.
     * @param uhrzeit Uhrzeit des Termins.
     * @param notizen Optionale Notizen.
     * @param vorlaufMinuten Vorlauf der Erinnerung in Minuten.
     * @return Neu angelegter Termin.
     */
    public Termin addTermin(UUID modulId, String titel, LocalDate datum, LocalTime uhrzeit, String notizen, int vorlaufMinuten) {
        ensureModuleExists(modulId);
        Termin termin = new Termin(modulId, titel, datum, uhrzeit, notizen);
        studyPlan.getTermine().add(termin);
        studyPlan.getErinnerungen().add(new Erinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId(), vorlaufMinuten, true));
        save();
        return termin;
    }

    /**
     * Aktualisiert einen vorhandenen Termin und seine Erinnerung.
     *
     * @param id ID des Termins.
     * @param modulId ID des zugeordneten Moduls.
     * @param titel Neuer Titel.
     * @param datum Neues Datum.
     * @param uhrzeit Neue Uhrzeit.
     * @param notizen Neue Notizen.
     * @param vorlaufMinuten Vorlauf der Erinnerung in Minuten.
     * @param erinnerungAktiv Gibt an, ob die Erinnerung aktiv ist.
     */
    public void updateTermin(UUID id, UUID modulId, String titel, LocalDate datum, LocalTime uhrzeit, String notizen, int vorlaufMinuten, boolean erinnerungAktiv) {
        ensureModuleExists(modulId);
        Termin termin = findTermin(id).orElseThrow(() -> new IllegalArgumentException("Termin wurde nicht gefunden."));
        termin.setModulId(modulId);
        termin.setTitel(titel);
        termin.setDatum(datum);
        termin.setUhrzeit(uhrzeit);
        termin.setNotizen(notizen);
        upsertErinnerung(Erinnerung.ZielTyp.TERMIN, id, vorlaufMinuten, erinnerungAktiv);
        save();
    }

    /**
     * Loescht einen Termin und die zugehoerige Erinnerung.
     *
     * @param id ID des Termins.
     */
    public void deleteTermin(UUID id) {
        studyPlan.getTermine().removeIf(termin -> termin.getId().equals(id));
        studyPlan.getErinnerungen().removeIf(erinnerung -> erinnerung.getZielTyp() == Erinnerung.ZielTyp.TERMIN && erinnerung.getZielId().equals(id));
        save();
    }

    /**
     * Fuegt einen neuen Lernblock inklusive Erinnerung hinzu.
     *
     * @param modulId ID des zugeordneten Moduls.
     * @param thema Thema des Lernblocks.
     * @param datum Datum des Lernblocks.
     * @param startzeit Startzeit des Lernblocks.
     * @param dauerMinuten Dauer in Minuten.
     * @param vorlaufMinuten Vorlauf der Erinnerung in Minuten.
     * @return Neu angelegter Lernblock.
     */
    public Lernblock addLernblock(UUID modulId, String thema, LocalDate datum, LocalTime startzeit, int dauerMinuten, int vorlaufMinuten) {
        ensureModuleExists(modulId);
        Lernblock lernblock = new Lernblock(modulId, thema, datum, startzeit, dauerMinuten);
        studyPlan.getLernbloecke().add(lernblock);
        studyPlan.getErinnerungen().add(new Erinnerung(Erinnerung.ZielTyp.LERNBLOCK, lernblock.getId(), vorlaufMinuten, true));
        save();
        return lernblock;
    }

    /**
     * Aktualisiert einen vorhandenen Lernblock und seine Erinnerung.
     *
     * @param id ID des Lernblocks.
     * @param modulId ID des zugeordneten Moduls.
     * @param thema Neues Thema.
     * @param datum Neues Datum.
     * @param startzeit Neue Startzeit.
     * @param dauerMinuten Neue Dauer in Minuten.
     * @param vorlaufMinuten Vorlauf der Erinnerung in Minuten.
     * @param erinnerungAktiv Gibt an, ob die Erinnerung aktiv ist.
     */
    public void updateLernblock(UUID id, UUID modulId, String thema, LocalDate datum, LocalTime startzeit, int dauerMinuten, int vorlaufMinuten, boolean erinnerungAktiv) {
        ensureModuleExists(modulId);
        Lernblock lernblock = findLernblock(id).orElseThrow(() -> new IllegalArgumentException("Lernblock wurde nicht gefunden."));
        lernblock.setModulId(modulId);
        lernblock.setThema(thema);
        lernblock.setDatum(datum);
        lernblock.setStartzeit(startzeit);
        lernblock.setDauerMinuten(dauerMinuten);
        upsertErinnerung(Erinnerung.ZielTyp.LERNBLOCK, id, vorlaufMinuten, erinnerungAktiv);
        save();
    }

    /**
     * Loescht einen Lernblock und die zugehoerige Erinnerung.
     *
     * @param id ID des Lernblocks.
     */
    public void deleteLernblock(UUID id) {
        studyPlan.getLernbloecke().removeIf(lernblock -> lernblock.getId().equals(id));
        studyPlan.getErinnerungen().removeIf(erinnerung -> erinnerung.getZielTyp() == Erinnerung.ZielTyp.LERNBLOCK && erinnerung.getZielId().equals(id));
        save();
    }

    /**
     * Aktualisiert die globalen Einstellungen.
     *
     * @param standardVorlaufMinuten Standard-Vorlauf fuer neue Erinnerungen.
     * @param erinnerungenAktiv Gibt an, ob Erinnerungen global aktiv sind.
     */
    public void updateEinstellung(int standardVorlaufMinuten, boolean erinnerungenAktiv) {
        studyPlan.getEinstellung().setStandardVorlaufMinuten(standardVorlaufMinuten);
        studyPlan.getEinstellung().setErinnerungenAktiv(erinnerungenAktiv);
        save();
    }

    /**
     * Ermittelt Kalender-Eintraege fuer einen Zeitraum und optionale Filter.
     *
     * @param von Startdatum des Zeitraums.
     * @param bis Enddatum des Zeitraums.
     * @param modulId Optionaler Modulfilter oder {@code null}.
     * @param start Optionaler Startzeitfilter oder {@code null}.
     * @param ende Optionaler Endzeitfilter oder {@code null}.
     * @return Sortierte Liste passender Kalender-Eintraege.
     */
    public List<KalenderEintrag> getKalenderEintraege(LocalDate von, LocalDate bis, UUID modulId, LocalTime start, LocalTime ende) {
        List<KalenderEintrag> termine = studyPlan.getTermine().stream()
                .filter(termin -> isInDateRange(termin.getDatum(), von, bis))
                .filter(termin -> modulId == null || termin.getModulId().equals(modulId))
                .filter(termin -> isInTimeRange(termin.getUhrzeit(), start, ende))
                .map(termin -> new KalenderEintrag("Termin", termin.getId(), termin.getModulId(), termin.getTitel(), termin.getDatum(), termin.getUhrzeit(), 0))
                .toList();

        List<KalenderEintrag> lernbloecke = studyPlan.getLernbloecke().stream()
                .filter(block -> isInDateRange(block.getDatum(), von, bis))
                .filter(block -> modulId == null || block.getModulId().equals(modulId))
                .filter(block -> isInTimeRange(block.getStartzeit(), start, ende))
                .map(block -> new KalenderEintrag("Lernblock", block.getId(), block.getModulId(), block.getThema(), block.getDatum(), block.getStartzeit(), block.getDauerMinuten()))
                .toList();

        return java.util.stream.Stream.concat(termine.stream(), lernbloecke.stream())
                .sorted(Comparator.comparing(KalenderEintrag::datum).thenComparing(KalenderEintrag::zeit))
                .toList();
    }

    /**
     * Ermittelt Erinnerungen, die zum angegebenen Zeitpunkt faellig sind.
     *
     * @param now Aktueller Pruefzeitpunkt.
     * @return Liste faelliger Erinnerungen.
     */
    public List<FaelligeErinnerung> getFaelligeErinnerungen(LocalDateTime now) {
        if (!studyPlan.getEinstellung().isErinnerungenAktiv()) {
            return List.of();
        }

        return studyPlan.getErinnerungen().stream()
                .filter(Erinnerung::isAktiv)
                .flatMap(erinnerung -> toFaelligeErinnerung(erinnerung, now).stream())
                .toList();
    }

    /**
     * Markiert eine Erinnerung als angezeigt.
     *
     * @param erinnerungId ID der angezeigten Erinnerung.
     * @param shownAt Zeitpunkt der Anzeige.
     */
    public void markErinnerungAngezeigt(UUID erinnerungId, LocalDateTime shownAt) {
        studyPlan.getErinnerungen().stream()
                .filter(erinnerung -> erinnerung.getId().equals(erinnerungId))
                .findFirst()
                .ifPresent(erinnerung -> erinnerung.setZuletztAngezeigt(shownAt));
        save();
    }

    /**
     * Sucht ein Modul anhand seiner ID.
     *
     * @param id Modul-ID.
     * @return Optional mit Modul, falls es gefunden wurde.
     */
    public Optional<Modul> findModul(UUID id) {
        return studyPlan.getModule().stream().filter(modul -> modul.getId().equals(id)).findFirst();
    }

    /**
     * Gibt den Namen eines Moduls zurueck.
     *
     * @param id Modul-ID.
     * @return Modulname oder ein Platzhalter, wenn das Modul nicht existiert.
     */
    public String getModulName(UUID id) {
        return findModul(id).map(Modul::getName).orElse("(kein Modul)");
    }

    /**
     * Sucht eine Erinnerung zu einem bestimmten Zieleintrag.
     *
     * @param zielTyp Typ des Zieleintrags.
     * @param zielId ID des Zieleintrags.
     * @return Optional mit Erinnerung, falls sie gefunden wurde.
     */
    public Optional<Erinnerung> findErinnerung(Erinnerung.ZielTyp zielTyp, UUID zielId) {
        return studyPlan.getErinnerungen().stream()
                .filter(erinnerung -> erinnerung.getZielTyp() == zielTyp && erinnerung.getZielId().equals(zielId))
                .findFirst();
    }

    /**
     * Sucht einen Termin anhand seiner ID.
     *
     * @param id Termin-ID.
     * @return Optional mit Termin, falls er gefunden wurde.
     */
    public Optional<Termin> findTermin(UUID id) {
        return studyPlan.getTermine().stream().filter(termin -> termin.getId().equals(id)).findFirst();
    }

    /**
     * Sucht einen Lernblock anhand seiner ID.
     *
     * @param id Lernblock-ID.
     * @return Optional mit Lernblock, falls er gefunden wurde.
     */
    public Optional<Lernblock> findLernblock(UUID id) {
        return studyPlan.getLernbloecke().stream().filter(lernblock -> lernblock.getId().equals(id)).findFirst();
    }

    /**
     * Speichert den aktuellen Zustand der Anwendung.
     */
    public void save() {
        storage.save(studyPlan);
    }

    private Optional<FaelligeErinnerung> toFaelligeErinnerung(Erinnerung erinnerung, LocalDateTime now) {
        LocalDateTime eventTime;
        String titel;
        String typ;
        UUID modulId;

        if (erinnerung.getZielTyp() == Erinnerung.ZielTyp.TERMIN) {
            Optional<Termin> termin = findTermin(erinnerung.getZielId());
            if (termin.isEmpty()) {
                return Optional.empty();
            }
            Termin t = termin.get();
            eventTime = LocalDateTime.of(t.getDatum(), t.getUhrzeit());
            titel = t.getTitel();
            typ = "Termin";
            modulId = t.getModulId();
        } else {
            Optional<Lernblock> lernblock = findLernblock(erinnerung.getZielId());
            if (lernblock.isEmpty()) {
                return Optional.empty();
            }
            Lernblock b = lernblock.get();
            eventTime = LocalDateTime.of(b.getDatum(), b.getStartzeit());
            titel = b.getThema();
            typ = "Lernblock";
            modulId = b.getModulId();
        }

        LocalDateTime reminderTime = eventTime.minusMinutes(erinnerung.getVorlaufMinuten());
        boolean due = !now.isBefore(reminderTime) && now.isBefore(eventTime.plusMinutes(1));
        boolean alreadyShown = erinnerung.getZuletztAngezeigt() != null
                && erinnerung.getZuletztAngezeigt().toLocalDate().equals(now.toLocalDate())
                && !erinnerung.getZuletztAngezeigt().isBefore(reminderTime);

        if (!due || alreadyShown) {
            return Optional.empty();
        }

        return Optional.of(new FaelligeErinnerung(erinnerung.getId(), typ, titel, getModulName(modulId), eventTime));
    }

    private void upsertErinnerung(Erinnerung.ZielTyp zielTyp, UUID zielId, int vorlaufMinuten, boolean aktiv) {
        findErinnerung(zielTyp, zielId).ifPresentOrElse(erinnerung -> {
            erinnerung.setVorlaufMinuten(vorlaufMinuten);
            erinnerung.setAktiv(aktiv);
            erinnerung.setZuletztAngezeigt(null);
        }, () -> studyPlan.getErinnerungen().add(new Erinnerung(zielTyp, zielId, vorlaufMinuten, aktiv)));
    }

    private void ensureUniqueModuleName(UUID currentId, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Der Modulname darf nicht leer sein.");
        }
        boolean exists = studyPlan.getModule().stream()
                .anyMatch(modul -> !modul.getId().equals(currentId) && modul.getName().equalsIgnoreCase(name.trim()));
        if (exists) {
            throw new IllegalArgumentException("Ein Modul mit diesem Namen existiert bereits.");
        }
    }

    private void ensureModuleExists(UUID modulId) {
        if (findModul(modulId).isEmpty()) {
            throw new IllegalArgumentException("Bitte zuerst ein gueltiges Modul auswaehlen.");
        }
    }

    private boolean isInDateRange(LocalDate date, LocalDate von, LocalDate bis) {
        return !date.isBefore(von) && !date.isAfter(bis);
    }

    private boolean isInTimeRange(LocalTime time, LocalTime start, LocalTime ende) {
        return (start == null || !time.isBefore(start)) && (ende == null || !time.isAfter(ende));
    }

    /**
     * Kompakte Darstellung eines Termins oder Lernblocks fuer die Kalenderansicht.
     *
     * @param typ Eintragstyp, zum Beispiel Termin oder Lernblock.
     * @param id ID des Eintrags.
     * @param modulId ID des zugeordneten Moduls.
     * @param titel Anzeige-Titel.
     * @param datum Datum des Eintrags.
     * @param zeit Uhrzeit des Eintrags.
     * @param dauerMinuten Dauer in Minuten; bei Terminen 0.
     */
    public record KalenderEintrag(String typ, UUID id, UUID modulId, String titel, LocalDate datum, LocalTime zeit, int dauerMinuten) {
    }

    /**
     * Kompakte Darstellung einer aktuell faelligen Erinnerung fuer die Oberflaeche.
     *
     * @param erinnerungId ID der Erinnerung.
     * @param typ Zieltyp der Erinnerung.
     * @param titel Titel des Zieleintrags.
     * @param modulName Name des zugeordneten Moduls.
     * @param zeitpunkt Zeitpunkt des Zieleintrags.
     */
    public record FaelligeErinnerung(UUID erinnerungId, String typ, String titel, String modulName, LocalDateTime zeitpunkt) {
    }
}
