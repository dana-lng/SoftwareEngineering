package studyplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Testet die Validierung der Modellklasse {@link Erinnerung}.
 */
class ErinnerungTest {

    /**
     * Prueft, ob eine Erinnerung mit gueltigen Werten korrekt erzeugt wird.
     */
    @Test
    void constructorSetsValidValues() {
        UUID zielId = UUID.randomUUID();

        Erinnerung erinnerung = new Erinnerung(Erinnerung.ZielTyp.TERMIN, zielId, 30, true);

        Assertions.assertNotNull(erinnerung.getId());
        Assertions.assertEquals(Erinnerung.ZielTyp.TERMIN, erinnerung.getZielTyp());
        Assertions.assertEquals(zielId, erinnerung.getZielId());
        Assertions.assertEquals(30, erinnerung.getVorlaufMinuten());
        Assertions.assertTrue(erinnerung.isAktiv());
        Assertions.assertNull(erinnerung.getZuletztAngezeigt());
    }

    /**
     * Prueft, ob der Konstruktor fehlende Pflichtwerte ablehnt.
     */
    @Test
    void constructorRejectsMissingRequiredValues() {
        IllegalArgumentException missingTargetTypeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Erinnerung(null, UUID.randomUUID(), 30, true)
        );
        IllegalArgumentException missingTargetIdException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Erinnerung(Erinnerung.ZielTyp.LERNBLOCK, null, 30, true)
        );

        Assertions.assertEquals("Eine Erinnerung braucht einen Zieltyp.", missingTargetTypeException.getMessage());
        Assertions.assertEquals("Eine Erinnerung braucht ein Ziel.", missingTargetIdException.getMessage());
    }

    /**
     * Prueft, ob nur Vorlaufzeiten zwischen 0 Minuten und 7 Tagen erlaubt sind.
     */
    @Test
    void constructorRejectsInvalidLeadTime() {
        IllegalArgumentException negativeLeadTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Erinnerung(Erinnerung.ZielTyp.TERMIN, UUID.randomUUID(), -1, true)
        );
        IllegalArgumentException tooLargeLeadTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Erinnerung(Erinnerung.ZielTyp.TERMIN, UUID.randomUUID(), 10081, true)
        );

        Assertions.assertEquals("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.", negativeLeadTimeException.getMessage());
        Assertions.assertEquals("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.", tooLargeLeadTimeException.getMessage());
    }

    /**
     * Prueft, ob Setter gueltige Werte speichern.
     */
    @Test
    void settersValidateAndStoreValues() {
        Erinnerung erinnerung = new Erinnerung(Erinnerung.ZielTyp.TERMIN, UUID.randomUUID(), 30, true);
        UUID newZielId = UUID.randomUUID();
        LocalDateTime shownAt = LocalDateTime.of(2026, 5, 3, 12, 0);

        erinnerung.setZielTyp(Erinnerung.ZielTyp.LERNBLOCK);
        erinnerung.setZielId(newZielId);
        erinnerung.setVorlaufMinuten(10080);
        erinnerung.setAktiv(false);
        erinnerung.setZuletztAngezeigt(shownAt);

        Assertions.assertEquals(Erinnerung.ZielTyp.LERNBLOCK, erinnerung.getZielTyp());
        Assertions.assertEquals(newZielId, erinnerung.getZielId());
        Assertions.assertEquals(10080, erinnerung.getVorlaufMinuten());
        Assertions.assertFalse(erinnerung.isAktiv());
        Assertions.assertEquals(shownAt, erinnerung.getZuletztAngezeigt());
    }

    /**
     * Prueft, ob Setter ungueltige Werte ablehnen.
     */
    @Test
    void settersRejectInvalidValues() {
        Erinnerung erinnerung = new Erinnerung(Erinnerung.ZielTyp.TERMIN, UUID.randomUUID(), 30, true);

        IllegalArgumentException missingTargetTypeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> erinnerung.setZielTyp(null)
        );
        IllegalArgumentException missingTargetIdException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> erinnerung.setZielId(null)
        );
        IllegalArgumentException invalidLeadTimeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> erinnerung.setVorlaufMinuten(-1)
        );

        Assertions.assertEquals("Eine Erinnerung braucht einen Zieltyp.", missingTargetTypeException.getMessage());
        Assertions.assertEquals("Eine Erinnerung braucht ein Ziel.", missingTargetIdException.getMessage());
        Assertions.assertEquals("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.", invalidLeadTimeException.getMessage());
    }
}
