package studyplanner.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import studyplanner.model.Modul;
import studyplanner.service.StudyPlan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Testet das lokale Speichern und Laden der Anwendung mit {@link Storage}.
 */
class StorageTest {

    /**
     * Temporärer Ordner fuer Testdateien, damit keine echten Benutzerdaten veraendert werden.
     */
    @TempDir
    private Path tempDir;

    /**
     * Prueft, ob ein leerer Study-Plan geladen wird, wenn noch keine Datei existiert.
     */
    @Test
    void loadReturnsEmptyStudyPlanWhenFileDoesNotExist() {
        Storage storage = new Storage(tempDir.resolve("studyplanner.dat"));

        StudyPlan loadedPlan = storage.load();

        Assertions.assertNotNull(loadedPlan);
        Assertions.assertTrue(loadedPlan.getModule().isEmpty());
        Assertions.assertTrue(loadedPlan.getTermine().isEmpty());
        Assertions.assertTrue(loadedPlan.getLernbloecke().isEmpty());
        Assertions.assertTrue(loadedPlan.getErinnerungen().isEmpty());
    }

    /**
     * Prueft, ob der konfigurierte Dateipfad wieder zurueckgegeben wird.
     */
    @Test
    void getDataFileReturnsConfiguredPath() {
        Path dataFile = tempDir.resolve("custom").resolve("studyplanner.dat");
        Storage storage = new Storage(dataFile);

        Assertions.assertEquals(dataFile, storage.getDataFile());
    }

    /**
     * Prueft, ob beim Speichern fehlende Ordner erstellt und die Datei geschrieben wird.
     */
    @Test
    void saveCreatesDirectoriesAndWritesDataFile() {
        Path dataFile = tempDir.resolve("nested").resolve("folder").resolve("studyplanner.dat");
        Storage storage = new Storage(dataFile);

        storage.save(new StudyPlan());

        Assertions.assertTrue(Files.exists(dataFile));
        Assertions.assertTrue(Files.isRegularFile(dataFile));
    }

    /**
     * Prueft, ob gespeicherte Module und Einstellungen wieder geladen werden.
     */
    @Test
    void saveAndLoadPreservesStudyPlanData() {
        Path dataFile = tempDir.resolve("studyplanner.dat");
        Storage storage = new Storage(dataFile);
        StudyPlan studyPlan = new StudyPlan();
        studyPlan.getModule().add(new Modul("Datenbanksysteme I", "#123456", "SQL", 2));
        studyPlan.getEinstellung().setStandardVorlaufMinuten(45);
        studyPlan.getEinstellung().setErinnerungenAktiv(false);

        storage.save(studyPlan);
        StudyPlan loadedPlan = storage.load();

        Assertions.assertEquals(1, loadedPlan.getModule().size());
        Assertions.assertEquals("Datenbanksysteme I", loadedPlan.getModule().get(0).getName());
        Assertions.assertEquals("#123456", loadedPlan.getModule().get(0).getFarbe());
        Assertions.assertEquals("SQL", loadedPlan.getModule().get(0).getNotizen());
        Assertions.assertEquals(2, loadedPlan.getModule().get(0).getSemester());
        Assertions.assertEquals(45, loadedPlan.getEinstellung().getStandardVorlaufMinuten());
        Assertions.assertFalse(loadedPlan.getEinstellung().isErinnerungenAktiv());
    }

    /**
     * Prueft, ob eine beschädigte Datei die Anwendung nicht abstuerzen laesst.
     *
     * @throws IOException falls die Testdatei nicht geschrieben werden kann.
     */
    @Test
    void loadReturnsEmptyStudyPlanWhenDataFileIsDamaged() throws IOException {
        Path dataFile = tempDir.resolve("studyplanner.dat");
        Files.writeString(dataFile, "Das ist keine serialisierte StudyPlan-Datei.");
        Storage storage = new Storage(dataFile);

        StudyPlan loadedPlan = storage.load();

        Assertions.assertNotNull(loadedPlan);
        Assertions.assertTrue(loadedPlan.getModule().isEmpty());
        Assertions.assertTrue(loadedPlan.getTermine().isEmpty());
        Assertions.assertTrue(loadedPlan.getLernbloecke().isEmpty());
        Assertions.assertTrue(loadedPlan.getErinnerungen().isEmpty());
    }
}
