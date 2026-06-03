package studyplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Testet die Validierung und Normalisierung der Modellklasse {@link Lernblock}.
 */
class LernblockTest {

    /**
     * Prueft, ob ein Lernblock mit gueltigen Werten korrekt erzeugt wird.
     */
    @Test
    void constructorSetsValidValues() {
        UUID modulId = UUID.randomUUID();
        LocalDate datum = LocalDate.of(2026, 5, 3);
        LocalTime startzeit = LocalTime.of(9, 0);

        Lernblock lernblock = new Lernblock(modulId, "Datenbanken lernen", datum, startzeit, 90);

        Assertions.assertNotNull(lernblock.getId());
        Assertions.assertEquals(modulId, lernblock.getModulId());
        Assertions.assertEquals("Datenbanken lernen", lernblock.getThema());
        Assertions.assertEquals(datum, lernblock.getDatum());
        Assertions.assertEquals(startzeit, lernblock.getStartzeit());
        Assertions.assertEquals(90, lernblock.getDauerMinuten());
        Assertions.assertEquals(Lernblock.STATUS_OFFEN, lernblock.getStatus());
    }

    /**
     * Prueft, ob das Thema normalisiert wird.
     */
    @Test
    void constructorTrimsTopic() {
        Lernblock lernblock = new Lernblock(UUID.randomUUID(), "  Analysis ueben  ", LocalDate.now(), LocalTime.NOON, 45);

        Assertions.assertEquals("Analysis ueben", lernblock.getThema());
    }

    /**
     * Prueft, ob der Konstruktor fehlende Pflichtwerte ablehnt.
     */
    @Test
    void constructorRejectsMissingRequiredValues() {
        IllegalArgumentException missingModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(null, "Thema", LocalDate.now(), LocalTime.NOON, 60)
        );
        IllegalArgumentException missingTopicException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(UUID.randomUUID(), " ", LocalDate.now(), LocalTime.NOON, 60)
        );
        IllegalArgumentException missingDateException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(UUID.randomUUID(), "Thema", null, LocalTime.NOON, 60)
        );
        IllegalArgumentException missingStartTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(UUID.randomUUID(), "Thema", LocalDate.now(), null, 60)
        );

        Assertions.assertEquals("Ein Lernblock braucht ein Modul.", missingModuleException.getMessage());
        Assertions.assertEquals("Das Thema darf nicht leer sein.", missingTopicException.getMessage());
        Assertions.assertEquals("Ein Lernblock braucht ein Datum.", missingDateException.getMessage());
        Assertions.assertEquals("Ein Lernblock braucht eine Startzeit.", missingStartTimeException.getMessage());
    }

    /**
     * Prueft, ob nur Dauern zwischen 1 und 1440 Minuten erlaubt sind.
     */
    @Test
    void constructorRejectsInvalidDuration() {
        IllegalArgumentException zeroDurationException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(UUID.randomUUID(), "Thema", LocalDate.now(), LocalTime.NOON, 0)
        );
        IllegalArgumentException tooLargeDurationException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Lernblock(UUID.randomUUID(), "Thema", LocalDate.now(), LocalTime.NOON, 1441)
        );

        Assertions.assertEquals("Die Dauer muss zwischen 1 und 1440 Minuten liegen.", zeroDurationException.getMessage());
        Assertions.assertEquals("Die Dauer muss zwischen 1 und 1440 Minuten liegen.", tooLargeDurationException.getMessage());
    }

    /**
     * Prueft, ob Setter gueltige Werte speichern und Texte normalisieren.
     */
    @Test
    void settersValidateAndNormalizeValues() {
        Lernblock lernblock = new Lernblock(UUID.randomUUID(), "Alt", LocalDate.of(2026, 1, 1), LocalTime.of(8, 0), 30);
        UUID newModulId = UUID.randomUUID();
        LocalDate newDate = LocalDate.of(2026, 6, 1);
        LocalTime newStartTime = LocalTime.of(15, 45);

        lernblock.setModulId(newModulId);
        lernblock.setThema("  Neues Thema  ");
        lernblock.setDatum(newDate);
        lernblock.setStartzeit(newStartTime);
        lernblock.setDauerMinuten(1440);
        lernblock.setStatus(Lernblock.STATUS_ERLEDIGT);

        Assertions.assertEquals(newModulId, lernblock.getModulId());
        Assertions.assertEquals("Neues Thema", lernblock.getThema());
        Assertions.assertEquals(newDate, lernblock.getDatum());
        Assertions.assertEquals(newStartTime, lernblock.getStartzeit());
        Assertions.assertEquals(1440, lernblock.getDauerMinuten());
        Assertions.assertEquals(Lernblock.STATUS_ERLEDIGT, lernblock.getStatus());
    }

    /**
     * Prueft, ob Setter ungueltige Werte ablehnen.
     */
    @Test
    void settersRejectInvalidValues() {
        Lernblock lernblock = new Lernblock(UUID.randomUUID(), "Thema", LocalDate.now(), LocalTime.NOON, 60);

        IllegalArgumentException missingModuleException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setModulId(null)
        );
        IllegalArgumentException missingTopicException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setThema("")
        );
        IllegalArgumentException missingDateException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setDatum(null)
        );
        IllegalArgumentException missingStartTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setStartzeit(null)
        );
        IllegalArgumentException invalidDurationException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setDauerMinuten(-1)
        );

        Assertions.assertEquals("Ein Lernblock braucht ein Modul.", missingModuleException.getMessage());
        Assertions.assertEquals("Das Thema darf nicht leer sein.", missingTopicException.getMessage());
        Assertions.assertEquals("Ein Lernblock braucht ein Datum.", missingDateException.getMessage());
        Assertions.assertEquals("Ein Lernblock braucht eine Startzeit.", missingStartTimeException.getMessage());
        Assertions.assertEquals("Die Dauer muss zwischen 1 und 1440 Minuten liegen.", invalidDurationException.getMessage());
    }

    /**
     * Prueft, ob nur offene und erledigte Statuswerte erlaubt sind.
     */
    @Test
    void setStatusRejectsInvalidValue() {
        Lernblock lernblock = new Lernblock(UUID.randomUUID(), "Thema", LocalDate.now(), LocalTime.NOON, 60);

        IllegalArgumentException invalidStatusException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> lernblock.setStatus("in arbeit")
        );

        Assertions.assertEquals("Der Status muss offen oder erledigt sein.", invalidStatusException.getMessage());
    }
}
