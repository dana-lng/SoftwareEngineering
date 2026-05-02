# Study Planner

Eine einfache Java-Desktop-Anwendung fuer Studenten zum Verwalten von Modulen, Terminen, Lernbloecken und Erinnerungen.

## Voraussetzungen

- Java 17 oder neuer
- Maven

## Starten mit Maven

Unter Windows PowerShell oder CMD im Ordner `java_projekt`:

```powershell
mvn compile
mvn exec:java
```

Alternativ kann ein ausfuehrbares JAR gebaut werden:

```powershell
mvn package
java -jar target/study-planner-1.0.0.jar
```

Die Javadoc-Dokumentation kann mit folgendem Befehl erzeugt werden:

```powershell
mvn javadoc:javadoc
```

Danach liegt die HTML-Dokumentation unter `target/site/apidocs/index.html`.

Falls `mvn` nicht erkannt wird, ist Maven noch nicht im `PATH`. Dann entweder das Terminal neu starten oder den `bin`-Ordner deiner Maven-Installation zum `PATH` hinzufuegen.

Die Daten werden lokal im Benutzerordner unter `.studyplanner/studyplanner.dat` gespeichert.

## Projektstruktur

```text
java_projekt/
├── pom.xml
├── README.md
├── Agend.md
└── src/
    └── main/
        └── java/
            └── studyplanner/
                ├── Main.java
                ├── model/
                ├── service/
                ├── persistence/
                └── ui/
```

### Wichtige Dateien und Ordner

- `pom.xml`: Maven-Konfiguration des Projekts. Hier stehen Projektname, Java-Version, Build-Plugins und die Startklasse `studyplanner.Main`.
- `README.md`: Diese Projektbeschreibung mit Startanleitung und Strukturuebersicht.
- `Agend.md`: Beschreibung der Anforderungen an die Study-Planner-Anwendung.
- `src/main/java`: Standardordner fuer Java-Quellcode in Maven-Projekten.
- `studyplanner`: Java-Package der Anwendung. Der Ordnername passt zur Package-Deklaration `package studyplanner;`.
- `Main.java`: Startpunkt der Anwendung. Diese Klasse erzeugt den Service und oeffnet das Hauptfenster.

### Anwendungsschichten

- `model`: Datenklassen der Anwendung.
  - `Einstellung`
  - `Modul`
  - `Lernblock`
  - `Termin`
  - `Erinnerung`
- `service`: Anwendungslogik, Validierung und Kalender-/Erinnerungsfunktionen.
  - `StudyService`
  - `StudyPlan`
- `persistence`: Lokales Speichern und Laden der Daten.
  - `Storage`
- `ui`: Swing-Oberflaeche.
  - `StudyPlannerFrame`

### Automatisch erzeugte Ordner

- `target`: Wird von Maven beim Kompilieren und Paketieren erzeugt. Der Ordner enthaelt Build-Dateien und die fertige `.jar`-Datei.
- `out`: Kann entstehen, wenn man manuell mit `javac -d out` kompiliert. Mit Maven wird dieser Ordner nicht benoetigt.

Beide Ordner enthalten keine Quellcode-Dateien und koennen geloescht werden. Sie werden bei Bedarf automatisch neu erzeugt.
