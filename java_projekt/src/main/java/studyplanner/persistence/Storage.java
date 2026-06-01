package studyplanner.persistence;

import studyplanner.service.StudyPlan;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Speichert und laedt den Study-Plan lokal als serialisierte Datei.
 */
public class Storage {
    private static final String CONFIG_DATA_FILE_KEY = "dataFile";
    private static final Path DEFAULT_DIRECTORY = Path.of(System.getProperty("user.home"), ".studyplanner");
    private static final Path DEFAULT_DATA_FILE = DEFAULT_DIRECTORY.resolve("studyplanner.dat");
    private static final Path DEFAULT_CONFIG_FILE = DEFAULT_DIRECTORY.resolve("config.properties");

    private Path dataFile;
    private final Path configFile;

    /**
     * Erstellt einen Speicher mit dem Standardpfad im Benutzerordner.
     */
    public Storage() {
        this(readConfiguredDataFile(DEFAULT_CONFIG_FILE), DEFAULT_CONFIG_FILE);
    }

    /**
     * Erstellt einen Speicher mit einem frei waehlbaren Dateipfad.
     *
     * @param dataFile Pfad zur Datendatei.
     */
    public Storage(Path dataFile) {
        this(dataFile, null);
    }

    Storage(Path dataFile, Path configFile) {
        this.dataFile = normalizeDataFile(dataFile);
        this.configFile = configFile;
    }

    static Storage fromConfiguration(Path configFile) {
        return new Storage(readConfiguredDataFile(configFile), configFile);
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
            Path parent = dataFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
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

    /**
     * Aendert den Speicherort der Datendatei.
     *
     * @param dataFile Neuer Pfad zur Datendatei.
     */
    public void setDataFile(Path dataFile) {
        this.dataFile = normalizeDataFile(dataFile);
        saveConfiguration();
    }

    /**
     * Gibt den Standardpfad fuer die Datendatei zurueck.
     *
     * @return Standard-Datendatei im Benutzerordner.
     */
    public static Path getDefaultDataFile() {
        return DEFAULT_DATA_FILE;
    }

    private static Path readConfiguredDataFile(Path configFile) {
        if (Files.exists(configFile)) {
            Properties properties = new Properties();
            try (var input = Files.newInputStream(configFile)) {
                properties.load(input);
                String configuredPath = properties.getProperty(CONFIG_DATA_FILE_KEY);
                if (configuredPath != null && !configuredPath.isBlank()) {
                    return normalizeDataFile(Path.of(configuredPath));
                }
            } catch (IOException ignored) {
                // Eine defekte Konfiguration setzt die Anwendung auf den Standardpfad zurueck.
            }
        }
        return DEFAULT_DATA_FILE;
    }

    private void saveConfiguration() {
        if (configFile == null) {
            return;
        }
        Properties properties = new Properties();
        properties.setProperty(CONFIG_DATA_FILE_KEY, dataFile.toString());
        try {
            Path parent = configFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (var output = Files.newOutputStream(configFile)) {
                properties.store(output, "Study Planner configuration");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Speicherort konnte nicht gesichert werden: " + e.getMessage(), e);
        }
    }

    private static Path normalizeDataFile(Path dataFile) {
        if (dataFile == null || dataFile.toString().isBlank()) {
            throw new IllegalArgumentException("Bitte einen Speicherort auswaehlen.");
        }
        Path normalized = dataFile.toAbsolutePath().normalize();
        if (Files.isDirectory(normalized)) {
            throw new IllegalArgumentException("Bitte eine Datei als Speicherort auswaehlen.");
        }
        return normalized;
    }
}
