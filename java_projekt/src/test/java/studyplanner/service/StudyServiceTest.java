package studyplanner.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studyplanner.model.Erinnerung;
import studyplanner.model.Lernblock;
import studyplanner.model.Modul;
import studyplanner.model.Termin;
import studyplanner.persistence.Storage;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Testet die fachliche Logik der Klasse {@link StudyService}.
 */
class StudyServiceTest {

    /**
     * Temporärer Ordner fuer die Test-Speicherdatei.
     */
    @TempDir
    private Path tempDir;

    /**
     * Prueft, ob Module angelegt, sortiert und gegen doppelte Namen geschuetzt werden.
     */
    @Test
    void addModulStoresSortedModulesAndRejectsDuplicateNames() {
        StudyService service = createService();

        Modul second = service.addModul("Zoologie", "#111111", "", 2);
        Modul first = service.addModul("Analysis", "#222222", "", 1);

        List<Modul> module = service.getModule();
        IllegalArgumentException duplicateException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.addModul(" analysis ", "#333333", "", 3)
        );

        Assertions.assertEquals(first.getId(), module.get(0).getId());
        Assertions.assertEquals(second.getId(), module.get(1).getId());
        Assertions.assertEquals("Ein Modul mit diesem Namen existiert bereits.", duplicateException.getMessage());
    }

    /**
     * Prueft, ob Module aktualisiert und geloescht werden koennen.
     */
    @Test
    void updateAndDeleteModulChangesStoredModules() {
        StudyService service = createService();
        Modul modul = service.addModul("Alt", "#000000", "Alt", 1);

        service.updateModul(modul.getId(), "Neu", "#ABCDEF", "Neue Notiz", 4);
        Modul updated = service.findModul(modul.getId()).orElseThrow();

        Assertions.assertEquals("Neu", updated.getName());
        Assertions.assertEquals("#ABCDEF", updated.getFarbe());
        Assertions.assertEquals("Neue Notiz", updated.getNotizen());
        Assertions.assertEquals(4, updated.getSemester());

        service.deleteModul(modul.getId());

        Assertions.assertTrue(service.getModule().isEmpty());
        Assertions.assertTrue(service.findModul(modul.getId()).isEmpty());
    }

    /**
     * Prueft, ob Module mit zugeordneten Terminen oder Lernbloecken nicht geloescht werden.
     */
    @Test
    void deleteModulRejectsModuleThatIsStillUsed() {
        StudyService service = createService();
        Modul terminModul = service.addModul("Terminmodul", "#111111", "", 1);
        Modul lernblockModul = service.addModul("Lernblockmodul", "#222222", "", 1);
        service.addTermin(terminModul.getId(), "Vorlesung", LocalDate.of(2026, 5, 4), LocalTime.of(10, 0), "", 15);
        service.addLernblock(lernblockModul.getId(), "Ueben", LocalDate.of(2026, 5, 5), LocalTime.of(12, 0), 60, 15);

        IllegalArgumentException terminModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteModul(terminModul.getId())
        );
        IllegalArgumentException lernblockModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.deleteModul(lernblockModul.getId())
        );

        Assertions.assertEquals("Das Modul kann nicht geloescht werden, solange Termine oder Lernbloecke darauf verweisen.", terminModuleException.getMessage());
        Assertions.assertEquals("Das Modul kann nicht geloescht werden, solange Termine oder Lernbloecke darauf verweisen.", lernblockModuleException.getMessage());
    }

    /**
     * Prueft den kompletten Termin-Ablauf inklusive automatischer Erinnerung.
     */
    @Test
    void addUpdateAndDeleteTerminMaintainsReminder() {
        StudyService service = createService();
        Modul modul = service.addModul("Datenbanken", "#111111", "", 2);
        Termin termin = service.addTermin(modul.getId(), "Klausur", LocalDate.of(2026, 5, 10), LocalTime.of(9, 0), "Raum A", 30);

        Erinnerung reminder = service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId()).orElseThrow();
        Assertions.assertEquals(30, reminder.getVorlaufMinuten());
        Assertions.assertTrue(reminder.isAktiv());

        service.updateTermin(termin.getId(), modul.getId(), "Nachklausur", LocalDate.of(2026, 5, 11), LocalTime.of(10, 0), "Raum B", 45, false);
        Termin updatedTermin = service.findTermin(termin.getId()).orElseThrow();
        Erinnerung updatedReminder = service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId()).orElseThrow();

        Assertions.assertEquals("Nachklausur", updatedTermin.getTitel());
        Assertions.assertEquals(LocalDate.of(2026, 5, 11), updatedTermin.getDatum());
        Assertions.assertEquals(LocalTime.of(10, 0), updatedTermin.getUhrzeit());
        Assertions.assertEquals("Raum B", updatedTermin.getNotizen());
        Assertions.assertEquals(45, updatedReminder.getVorlaufMinuten());
        Assertions.assertFalse(updatedReminder.isAktiv());

        service.deleteTermin(termin.getId());

        Assertions.assertTrue(service.findTermin(termin.getId()).isEmpty());
        Assertions.assertTrue(service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId()).isEmpty());
    }

    /**
     * Prueft den kompletten Lernblock-Ablauf inklusive automatischer Erinnerung.
     */
    @Test
    void addUpdateAndDeleteLernblockMaintainsReminder() {
        StudyService service = createService();
        Modul modul = service.addModul("Analysis", "#111111", "", 1);
        Lernblock lernblock = service.addLernblock(modul.getId(), "Ableitungen", LocalDate.of(2026, 5, 8), LocalTime.of(14, 0), 90, 20);

        Erinnerung reminder = service.findErinnerung(Erinnerung.ZielTyp.LERNBLOCK, lernblock.getId()).orElseThrow();
        Assertions.assertEquals(20, reminder.getVorlaufMinuten());
        Assertions.assertTrue(reminder.isAktiv());

        service.updateLernblock(lernblock.getId(), modul.getId(), "Integrale", LocalDate.of(2026, 5, 9), LocalTime.of(15, 0), 120, 10, false);
        Lernblock updatedLernblock = service.findLernblock(lernblock.getId()).orElseThrow();
        Erinnerung updatedReminder = service.findErinnerung(Erinnerung.ZielTyp.LERNBLOCK, lernblock.getId()).orElseThrow();

        Assertions.assertEquals("Integrale", updatedLernblock.getThema());
        Assertions.assertEquals(LocalDate.of(2026, 5, 9), updatedLernblock.getDatum());
        Assertions.assertEquals(LocalTime.of(15, 0), updatedLernblock.getStartzeit());
        Assertions.assertEquals(120, updatedLernblock.getDauerMinuten());
        Assertions.assertEquals(10, updatedReminder.getVorlaufMinuten());
        Assertions.assertFalse(updatedReminder.isAktiv());

        service.deleteLernblock(lernblock.getId());

        Assertions.assertTrue(service.findLernblock(lernblock.getId()).isEmpty());
        Assertions.assertTrue(service.findErinnerung(Erinnerung.ZielTyp.LERNBLOCK, lernblock.getId()).isEmpty());
    }

    /**
     * Prueft, ob Kalender-Eintraege nach Datum, Modul und Zeit gefiltert werden.
     */
    @Test
    void getKalenderEintraegeFiltersByDateModuleAndTime() {
        StudyService service = createService();
        Modul databases = service.addModul("Datenbanken", "#111111", "", 2);
        Modul math = service.addModul("Mathematik", "#222222", "", 1);
        service.addTermin(databases.getId(), "DB Termin", LocalDate.of(2026, 5, 4), LocalTime.of(10, 0), "", 0);
        service.addLernblock(databases.getId(), "DB Lernen", LocalDate.of(2026, 5, 4), LocalTime.of(12, 0), 60, 0);
        service.addTermin(math.getId(), "Mathe Termin", LocalDate.of(2026, 5, 4), LocalTime.of(11, 0), "", 0);
        service.addTermin(databases.getId(), "Spaeter Termin", LocalDate.of(2026, 5, 10), LocalTime.of(10, 0), "", 0);

        List<StudyService.KalenderEintrag> entries = service.getKalenderEintraege(
                LocalDate.of(2026, 5, 4),
                LocalDate.of(2026, 5, 4),
                databases.getId(),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        );

        Assertions.assertEquals(2, entries.size());
        Assertions.assertEquals("DB Termin", entries.get(0).titel());
        Assertions.assertEquals("DB Lernen", entries.get(1).titel());
    }

    /**
     * Prueft, ob faellige Erinnerungen erkannt und nach Anzeige nicht erneut geliefert werden.
     */
    @Test
    void getFaelligeErinnerungenFindsDueReminderAndRespectsShownState() {
        StudyService service = createService();
        Modul modul = service.addModul("Programmierung", "#111111", "", 1);
        Termin termin = service.addTermin(modul.getId(), "Abgabe", LocalDate.of(2026, 5, 20), LocalTime.of(12, 0), "", 30);

        LocalDateTime dueTime = LocalDateTime.of(2026, 5, 20, 11, 30);
        List<StudyService.FaelligeErinnerung> dueReminders = service.getFaelligeErinnerungen(dueTime);

        Assertions.assertEquals(1, dueReminders.size());
        Assertions.assertEquals("Termin", dueReminders.get(0).typ());
        Assertions.assertEquals("Abgabe", dueReminders.get(0).titel());
        Assertions.assertEquals("Programmierung", dueReminders.get(0).modulName());

        service.markErinnerungAngezeigt(dueReminders.get(0).erinnerungId(), dueTime);

        Assertions.assertTrue(service.getFaelligeErinnerungen(dueTime.plusMinutes(5)).isEmpty());
        Assertions.assertTrue(service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId()).orElseThrow().getZuletztAngezeigt().isEqual(dueTime));
    }

    /**
     * Prueft, ob globale Einstellungen gespeichert und von einem neuen Service wieder geladen werden.
     */
    @Test
    void updateEinstellungPersistsValues() {
        Path dataFile = tempDir.resolve("studyplanner.dat");
        StudyService service = createService(dataFile);

        service.updateEinstellung(60, false);
        StudyService reloadedService = createService(dataFile);

        Assertions.assertEquals(60, reloadedService.getEinstellung().getStandardVorlaufMinuten());
        Assertions.assertFalse(reloadedService.getEinstellung().isErinnerungenAktiv());
        Assertions.assertTrue(reloadedService.getFaelligeErinnerungen(LocalDateTime.now()).isEmpty());
    }

    /**
     * Prueft Hilfsmethoden fuer Modulnamen und ungueltige Modulverweise.
     */
    @Test
    void getModulNameAndInvalidModuleReferencesBehaveAsExpected() {
        StudyService service = createService();
        UUID unknownModuleId = UUID.randomUUID();

        IllegalArgumentException missingModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.addTermin(unknownModuleId, "Termin", LocalDate.now(), LocalTime.NOON, "", 0)
        );

        Assertions.assertEquals("(kein Modul)", service.getModulName(unknownModuleId));
        Assertions.assertEquals("Bitte zuerst ein gueltiges Modul auswaehlen.", missingModuleException.getMessage());
    }

    /**
     * Erstellt einen Service mit einer Test-Speicherdatei.
     *
     * @return Neuer Service.
     */
    private StudyService createService() {
        return createService(tempDir.resolve("studyplanner.dat"));
    }

    /**
     * Erstellt einen Service mit einem festen Test-Speicherpfad.
     *
     * @param dataFile Pfad der Test-Speicherdatei.
     * @return Neuer Service.
     */
    private StudyService createService(Path dataFile) {
        return new StudyService(new Storage(dataFile));
    }
}
