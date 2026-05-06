package studyplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testet die Standardwerte und Validierung der Modellklasse {@link Einstellung}.
 */
class EinstellungTest {

    /**
     * Prueft, ob neue Einstellungen mit den erwarteten Standardwerten starten.
     */
    @Test
    void constructorUsesDefaultValues() {
        Einstellung einstellung = new Einstellung();

        Assertions.assertEquals(30, einstellung.getStandardVorlaufMinuten());
        Assertions.assertTrue(einstellung.isErinnerungenAktiv());
    }

    /**
     * Prueft, ob gueltige Werte fuer den Standard-Vorlauf gespeichert werden.
     */
    @Test
    void setStandardVorlaufMinutenAcceptsValidValues() {
        Einstellung einstellung = new Einstellung();

        einstellung.setStandardVorlaufMinuten(0);
        Assertions.assertEquals(0, einstellung.getStandardVorlaufMinuten());

        einstellung.setStandardVorlaufMinuten(10080);
        Assertions.assertEquals(10080, einstellung.getStandardVorlaufMinuten());
    }

    /**
     * Prueft, ob ungueltige Werte fuer den Standard-Vorlauf abgelehnt werden.
     */
    @Test
    void setStandardVorlaufMinutenRejectsInvalidValues() {
        Einstellung einstellung = new Einstellung();

        IllegalArgumentException negativeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> einstellung.setStandardVorlaufMinuten(-1)
        );
        IllegalArgumentException tooLargeException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> einstellung.setStandardVorlaufMinuten(10081)
        );

        Assertions.assertEquals("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.", negativeException.getMessage());
        Assertions.assertEquals("Der Vorlauf muss zwischen 0 Minuten und 7 Tagen liegen.", tooLargeException.getMessage());
    }

    /**
     * Prueft, ob Erinnerungen global aktiviert und deaktiviert werden koennen.
     */
    @Test
    void setErinnerungenAktivUpdatesValue() {
        Einstellung einstellung = new Einstellung();

        einstellung.setErinnerungenAktiv(false);
        Assertions.assertFalse(einstellung.isErinnerungenAktiv());

        einstellung.setErinnerungenAktiv(true);
        Assertions.assertTrue(einstellung.isErinnerungenAktiv());
    }
}
