package studyplanner.persistence;

import studyplanner.service.StudyPlan;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Speichert und laedt den Study-Plan lokal als serialisierte Datei.
 */
public class Storage {
    private final Path dataFile;

    /**
     * Erstellt einen Speicher mit dem Standardpfad im Benutzerordner.
     */
    public Storage() {
        this(Path.of(System.getProperty("user.home"), ".studyplanner", "studyplanner.dat"));
    }

    /**
     * Erstellt einen Speicher mit einem frei waehlbaren Dateipfad.
     *
     * @param dataFile Pfad zur Datendatei.
     */
    public Storage(Path dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * Laedt den gespeicherten Study-Plan.
     *
     * @return Geladener Study-Plan oder ein leerer Plan, wenn noch keine Daten existieren.
     */
    public StudyPlan load() {
        if (!Files.exists(dataFile)) {
            return new StudyPlan();
        }

        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(dataFile))) {
            Object loaded = input.readObject();
            if (loaded instanceof StudyPlan studyPlan) {
                return studyPlan;
            }
        } catch (IOException | ClassNotFoundException ignored) {
            // A damaged file should not crash startup. The app starts with empty data instead.
        }

        return new StudyPlan();
    }

    /**
     * Speichert den aktuellen Study-Plan lokal.
     *
     * @param studyPlan Zu speichernder Plan.
     * @throws IllegalStateException wenn die Daten nicht geschrieben werden koennen.
     */
    public void save(StudyPlan studyPlan) {
        try {
            Files.createDirectories(dataFile.getParent());
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(dataFile))) {
                output.writeObject(studyPlan);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Daten konnten nicht gespeichert werden: " + e.getMessage(), e);
        }
    }

    /**
     * Gibt den Pfad zur lokalen Datendatei zurueck.
     *
     * @return Pfad zur Datendatei.
     */
    public Path getDataFile() {
        return dataFile;
    }
}
