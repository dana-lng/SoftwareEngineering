package studyplanner.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Testet die Validierung und Normalisierung der Modellklasse {@link Modul}.
 */
class ModulTest {

    /**
     * Prueft, ob ein Modul mit gueltigen Werten korrekt erzeugt wird.
     */
    @Test
    void constructorSetsValidValues() {
        Modul modul = new Modul("Datenbanksysteme I", "#123456", "Relationale Datenbanken", 2);

        Assertions.assertNotNull(modul.getId());
        Assertions.assertEquals("Datenbanksysteme I", modul.getName());
        Assertions.assertEquals("#123456", modul.getFarbe());
        Assertions.assertEquals("Relationale Datenbanken", modul.getNotizen());
        Assertions.assertEquals(2, modul.getSemester());
    }

    /**
     * Prueft, ob Leerzeichen an Name, Farbe und Notizen entfernt werden.
     */
    @Test
    void constructorTrimsNameColorAndNotes() {
        Modul modul = new Modul("  Rechnernetze  ", "  #00AAFF  ", "  Netzwerke  ", 3);

        Assertions.assertEquals("Rechnernetze", modul.getName());
        Assertions.assertEquals("#00AAFF", modul.getFarbe());
        Assertions.assertEquals("Netzwerke", modul.getNotizen());
    }

    /**
     * Prueft, ob bei fehlender Farbe die Standardfarbe gesetzt wird.
     */
    @Test
    void constructorUsesDefaultColorWhenColorIsBlank() {
        Modul modulWithNullColor = new Modul("Mathematik", null, "", 1);
        Modul modulWithBlankColor = new Modul("Programmierung", "   ", "", 1);

        Assertions.assertEquals("#4F7CAC", modulWithNullColor.getFarbe());
        Assertions.assertEquals("#4F7CAC", modulWithBlankColor.getFarbe());
    }

    /**
     * Prueft, ob fehlende Notizen als leerer Text gespeichert werden.
     */
    @Test
    void constructorUsesEmptyTextWhenNotesAreNull() {
        Modul modul = new Modul("Software Engineering", "#FFFFFF", null, 4);

        Assertions.assertEquals("", modul.getNotizen());
    }

    /**
     * Prueft, ob ungueltige Modulnamen im Konstruktor abgelehnt werden.
     */
    @Test
    void constructorRejectsNullBlankAndEmptyName() {
        IllegalArgumentException nullNameException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Modul(null, "#123456", "", 1)
        );
        IllegalArgumentException emptyNameException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Modul("", "#123456", "", 1)
        );
        IllegalArgumentException blankNameException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Modul("   ", "#123456", "", 1)
        );

        Assertions.assertEquals("Der Modulname darf nicht leer sein.", nullNameException.getMessage());
        Assertions.assertEquals("Der Modulname darf nicht leer sein.", emptyNameException.getMessage());
        Assertions.assertEquals("Der Modulname darf nicht leer sein.", blankNameException.getMessage());
    }

    /**
     * Prueft, ob Semester ausserhalb des erlaubten Bereichs im Konstruktor abgelehnt werden.
     */
    @Test
    void constructorRejectsSemesterOutsideValidRange() {
        IllegalArgumentException tooSmallSemesterException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Modul("Mathematik", "#123456", "", 0)
        );
        IllegalArgumentException tooLargeSemesterException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Modul("Mathematik", "#123456", "", 13)
        );

        Assertions.assertEquals("Das Semester muss zwischen 1 und 12 liegen.", tooSmallSemesterException.getMessage());
        Assertions.assertEquals("Das Semester muss zwischen 1 und 12 liegen.", tooLargeSemesterException.getMessage());
    }

    /**
     * Prueft, ob Setter Werte normalisieren und gueltige Werte speichern.
     */
    @Test
    void settersValidateAndNormalizeValues() {
        Modul modul = new Modul("Alt", "#000000", "Alt", 1);

        modul.setName("  Neu  ");
        modul.setFarbe("  #ABCDEF  ");
        modul.setNotizen("  Neue Notiz  ");
        modul.setSemester(12);

        Assertions.assertEquals("Neu", modul.getName());
        Assertions.assertEquals("#ABCDEF", modul.getFarbe());
        Assertions.assertEquals("Neue Notiz", modul.getNotizen());
        Assertions.assertEquals(12, modul.getSemester());
    }

    /**
     * Prueft, ob Setter ungueltige Namen und Semester ablehnen.
     */
    @Test
    void settersRejectInvalidNameAndSemester() {
        Modul modul = new Modul("Gueltig", "#123456", "", 1);

        IllegalArgumentException blankNameException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> modul.setName(" ")
        );
        IllegalArgumentException tooSmallSemesterException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> modul.setSemester(0)
        );
        IllegalArgumentException tooLargeSemesterException = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> modul.setSemester(13)
        );

        Assertions.assertEquals("Der Modulname darf nicht leer sein.", blankNameException.getMessage());
        Assertions.assertEquals("Das Semester muss zwischen 1 und 12 liegen.", tooSmallSemesterException.getMessage());
        Assertions.assertEquals("Das Semester muss zwischen 1 und 12 liegen.", tooLargeSemesterException.getMessage());
    }

    /**
     * Prueft, ob {@link Modul#toString()} den Modulnamen zurueckgibt.
     */
    @Test
    void toStringReturnsModuleName() {
        Modul modul = new Modul("Analysis", "#123456", "", 1);

        Assertions.assertEquals("Analysis", modul.toString());
    }

    /**
     * Prueft, ob zwei Module mit gleichen Inhalten wegen unterschiedlicher IDs nicht gleich sind.
     */
    @Test
    void differentModulesAreNotEqualEvenWithSameValues() {
        Modul first = new Modul("Analysis", "#123456", "", 1);
        Modul second = new Modul("Analysis", "#123456", "", 1);

        Assertions.assertNotEquals(first, second);
    }
}
