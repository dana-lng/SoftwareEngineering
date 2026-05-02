## 1)Class-Diagram ist korrigiert.

## 2)Änderungen im Code

Für die Erweiterung der Termin-Funktion wurden folgende Dateien angepasst:

- `studyplanner/model/Termin.java`
  - neue Felder ergänzt: `dauerMinuten` und `terminart`

- `studyplanner/service/StudyService.java`
  - Methoden `addTermin(...)` und `updateTermin(...)` an die neuen Termin-Felder angepasst
  - Kalender-Einträge für Termine geben jetzt auch die Dauer weiter

- `studyplanner/ui/StudyPlannerFrame.java`
  - Termin-Dialog erweitert um:
    - `Dauer Minuten`
    - `Terminart`
  - Termin-Tabelle erweitert um die Spalten:
    - `Dauer`
    - `Art`
  - Aufrufe von `addTermin(...)` und `updateTermin(...)` an die neue Methodensignatur angepasst


Nach den Änderungen wurde das Projekt erfolgreich kompiliert und gestartet. Das Anlegen, Anzeigen und Speichern von Terminen funktioniert aktuell mit den neuen Feldern.