package studyplanner;

import studyplanner.persistence.Storage;
import studyplanner.service.StudyService;
import studyplanner.ui.StudyPlannerFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Einstiegspunkt der Study-Planner-Anwendung.
 */
public class Main {
    /**
     * Verhindert Instanziierung der Startklasse.
     */
    private Main() {
    }

    /**
     * Startet die Swing-Oberflaeche und initialisiert die zentrale Anwendungslogik.
     *
     * @param args Kommandozeilenargumente, die aktuell nicht verwendet werden.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
                // Default look and feel is fine if the system theme is unavailable.
            }

            Storage storage = new Storage();
            StudyService service = new StudyService(storage);
            StudyPlannerFrame frame = new StudyPlannerFrame(service);
            frame.setVisible(true);
        });
    }
}
