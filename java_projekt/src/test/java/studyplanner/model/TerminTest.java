package studyplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Testet die Validierung und Normalisierung der Modellklasse {@link Termin}.
 */
class TerminTest {
    private static final String STATUS_OFFEN = "offen";
    private static final String STATUS_ERLEDIGT = "erledigt";

    /**
     * Prueft, ob ein Termin mit gueltigen Werten korrekt erzeugt wird.
     */
    @Test
    void constructorSetsValidValues() {
        UUID modulId = UUID.randomUUID();
        LocalDate datum = LocalDate.of(2026, 5, 3);
        LocalTime uhrzeit = LocalTime.of(10, 30);

        Termin termin = new Termin(modulId, "Klausur", datum, uhrzeit, "Raum A");

        Assertions.assertNotNull(termin.getId());
        Assertions.assertEquals(modulId, termin.getModulId());
        Assertions.assertEquals("Klausur", termin.getTitel());
        Assertions.assertEquals(datum, termin.getDatum());
        Assertions.assertEquals(uhrzeit, termin.getUhrzeit());
        Assertions.assertEquals("Raum A", termin.getNotizen());
        Assertions.assertEquals(STATUS_OFFEN, getStatus(termin));
    }

    /**
     * Prueft, ob Titel und Notizen normalisiert werden.
     */
    @Test
    void constructorTrimsTitleAndNotes() {
        Termin termin = new Termin(UUID.randomUUID(), "  Vorlesung  ", LocalDate.now(), LocalTime.NOON, "  Hoersaal  ");

        Assertions.assertEquals("Vorlesung", termin.getTitel());
        Assertions.assertEquals("Hoersaal", termin.getNotizen());
    }

    /**
     * Prueft, ob fehlende Notizen als leerer Text gespeichert werden.
     */
    @Test
    void constructorUsesEmptyTextWhenNotesAreNull() {
        Termin termin = new Termin(UUID.randomUUID(), "Seminar", LocalDate.now(), LocalTime.NOON, null);

        Assertions.assertEquals("", termin.getNotizen());
    }

    /**
     * Prueft, ob der Konstruktor fehlende Pflichtwerte ablehnt.
     */
    @Test
    void constructorRejectsMissingRequiredValues() {
        IllegalArgumentException missingModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Termin(null, "Termin", LocalDate.now(), LocalTime.NOON, "")
        );
        IllegalArgumentException missingTitleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Termin(UUID.randomUUID(), " ", LocalDate.now(), LocalTime.NOON, "")
        );
        IllegalArgumentException missingDateException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Termin(UUID.randomUUID(), "Termin", null, LocalTime.NOON, "")
        );
        IllegalArgumentException missingTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Termin(UUID.randomUUID(), "Termin", LocalDate.now(), null, "")
        );

        Assertions.assertEquals("Ein Termin braucht ein Modul.", missingModuleException.getMessage());
        Assertions.assertEquals("Der Termintitel darf nicht leer sein.", missingTitleException.getMessage());
        Assertions.assertEquals("Ein Termin braucht ein Datum.", missingDateException.getMessage());
        Assertions.assertEquals("Ein Termin braucht eine Uhrzeit.", missingTimeException.getMessage());
    }

    /**
     * Prueft, ob Setter gueltige Werte speichern und Texte normalisieren.
     */
    @Test
    void settersValidateAndNormalizeValues() {
        Termin termin = new Termin(UUID.randomUUID(), "Alt", LocalDate.of(2026, 1, 1), LocalTime.of(8, 0), "Alt");
        UUID newModulId = UUID.randomUUID();
        LocalDate newDate = LocalDate.of(2026, 6, 1);
        LocalTime newTime = LocalTime.of(14, 15);

        termin.setModulId(newModulId);
        termin.setTitel("  Neu  ");
        termin.setDatum(newDate);
        termin.setUhrzeit(newTime);
        termin.setNotizen("  Neue Notiz  ");
        setStatus(termin, STATUS_ERLEDIGT);

        Assertions.assertEquals(newModulId, termin.getModulId());
        Assertions.assertEquals("Neu", termin.getTitel());
        Assertions.assertEquals(newDate, termin.getDatum());
        Assertions.assertEquals(newTime, termin.getUhrzeit());
        Assertions.assertEquals("Neue Notiz", termin.getNotizen());
        Assertions.assertEquals(STATUS_ERLEDIGT, getStatus(termin));
    }

    /**
     * Prueft, ob Setter ungueltige Pflichtwerte ablehnen.
     */
    @Test
    void settersRejectMissingRequiredValues() {
        Termin termin = new Termin(UUID.randomUUID(), "Termin", LocalDate.now(), LocalTime.NOON, "");

        IllegalArgumentException missingModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> termin.setModulId(null)
        );
        IllegalArgumentException missingTitleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> termin.setTitel("")
        );
        IllegalArgumentException missingDateException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> termin.setDatum(null)
        );
        IllegalArgumentException missingTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> termin.setUhrzeit(null)
        );

        Assertions.assertEquals("Ein Termin braucht ein Modul.", missingModuleException.getMessage());
        Assertions.assertEquals("Der Termintitel darf nicht leer sein.", missingTitleException.getMessage());
        Assertions.assertEquals("Ein Termin braucht ein Datum.", missingDateException.getMessage());
        Assertions.assertEquals("Ein Termin braucht eine Uhrzeit.", missingTimeException.getMessage());
    }

    /**
     * Prueft, ob nur offene und erledigte Statuswerte erlaubt sind.
     */
    @Test
    void setStatusRejectsInvalidValue() {
        Termin termin = new Termin(UUID.randomUUID(), "Termin", LocalDate.now(), LocalTime.NOON, "");

        IllegalArgumentException invalidStatusException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> setStatus(termin, "in arbeit")
        );

        Assertions.assertEquals("Der Status muss offen oder erledigt sein.", invalidStatusException.getMessage());
    }

    private String getStatus(Termin termin) {
        try {
            return (String) Termin.class.getMethod("getStatus").invoke(termin);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError("Termin braucht eine Methode getStatus().", ex);
        }
    }

    private void setStatus(Termin termin, String status) {
        try {
            Termin.class.getMethod("setStatus", String.class).invoke(termin, status);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof IllegalArgumentException illegalArgumentException) {
                throw illegalArgumentException;
            }
            throw new AssertionError("setStatus hat eine unerwartete Exception geworfen.", cause);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError("Termin braucht eine Methode setStatus(String).", ex);
        }
    }
}
