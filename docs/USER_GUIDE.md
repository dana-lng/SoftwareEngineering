
# Anwenderdokumentation: Study Planner

Der Study Planner ist eine Desktop-Anwendung für Studenten. Mit der Anwendung können Module, Termine, Lernblöcke und Erinnerungen verwaltet werden. Alle Daten werden lokal auf dem eigenen Computer gespeichert.

## Zweck der Anwendung

Die Anwendung hilft dabei, das Studium übersichtlich zu organisieren. Nutzer können:

- Module verwalten
- Termine eintragen
- Lernzeiten planen
- Kalenderwochen ansehen
- Erinnerungen nutzen
- Grundeinstellungen anpassen

## Start der Anwendung

Die Anwendung wird über Maven gestartet.

1. Öffnen Sie ein Terminal oder die Eingabeaufforderung.
2. Wechseln Sie in den Ordner `java_projekt`.
3. Starten Sie die Anwendung mit:

```bash
mvn exec:java
```

Nach dem Start öffnet sich das Fenster des Study Planners.

## Aufbau der Anwendung

Die Anwendung besteht aus:

- einer oberen Titelleiste
- einer linken Seitenleiste zur Navigation
- einem Hauptbereich für die aktuell ausgewählte Seite

In der Seitenleiste stehen die wichtigsten Bereiche:

- Module
- Kalender
- Termine
- Lernblöcke
- Erinnerungen
- Einstellungen

Wenn Sie mit der Maus über einen Menüpunkt fahren, wird dieser hervorgehoben. Der aktuell geöffnete Bereich ist ebenfalls sichtbar markiert.

## Module

Im Bereich **Module** können Studienmodule verwaltet werden.

### Neues Modul anlegen

1. Öffnen Sie den Bereich **Module**.
2. Klicken Sie auf **+ Neues Modul**.
3. Geben Sie den Namen des Moduls ein.
4. Wählen Sie das Semester aus.
5. Optional können Sie eine Farbe und eine Beschreibung eintragen.
6. Bestätigen Sie die Eingabe.

Das neue Modul erscheint anschließend als Karte in der Modulübersicht.

### Module suchen

Oben in der Modulansicht befindet sich ein Suchfeld. Dort können Sie nach Modulnamen oder Beschreibungen suchen.

Die Liste der Module wird automatisch gefiltert.

### Nach Semester filtern

Neben dem Suchfeld befindet sich ein Semesterfilter. Dort können Sie auswählen:

- alle Semester
- ein bestimmtes Semester

Die Modulübersicht zeigt dann nur die passenden Module an.

### Modul bearbeiten

1. Suchen Sie das gewünschte Modul in der Liste.
2. Klicken Sie auf **Bearbeiten**.
3. Ändern Sie die gewünschten Daten.
4. Bestätigen Sie die Eingabe.

### Modul löschen

1. Suchen Sie das gewünschte Modul in der Liste.
2. Klicken Sie auf **Löschen**.
3. Bestätigen Sie die Rückfrage.

Ein Modul kann nur gelöscht werden, wenn keine Termine oder Lernblöcke mehr damit verbunden sind.

## Kalender

Im Bereich **Kalender** wird eine Wochenansicht angezeigt.

Die Tabelle enthält die Wochentage:

- Montag
- Dienstag
- Mittwoch
- Donnerstag
- Freitag
- Samstag
- Sonntag

### Woche auswählen

Oben im Kalender befindet sich das Feld **Woche**.

Wählen Sie dort ein Datum aus. Die Anwendung zeigt automatisch die Woche an, in der dieses Datum liegt.

### Nach Modul filtern

Neben der Wochenauswahl befindet sich der Modulfilter.

Sie können auswählen:

- alle Module
- ein einzelnes Modul

Wenn ein einzelnes Modul gewählt wird, zeigt der Kalender nur Termine und Lernblöcke dieses Moduls.

### Kalender-Einträge

Im Kalender werden Termine und Lernblöcke am passenden Wochentag angezeigt. Ein Eintrag zeigt:

- Uhrzeit
- Art des Eintrags
- Titel oder Thema
- zugeordnetes Modul
- Dauer bei Lernblöcken

## Termine

Im Bereich **Termine** können feste Termine verwaltet werden.

Beispiele für Termine:

- Vorlesungen
- Klausuren
- Abgaben
- Besprechungen

### Termin anlegen

1. Öffnen Sie den Bereich **Termine**.
2. Klicken Sie auf **Neu**.
3. Wählen Sie ein Modul aus.
4. Geben Sie Titel, Datum und Uhrzeit ein.
5. Optional können Sie Notizen eintragen.
6. Legen Sie fest, wie viele Minuten vorher erinnert werden soll.
7. Bestätigen Sie die Eingabe.

Der Termin erscheint danach in der Terminliste und im Kalender.

### Termin bearbeiten

1. Wählen Sie einen Termin aus der Tabelle.
2. Klicken Sie auf **Bearbeiten**.
3. Ändern Sie die gewünschten Daten.
4. Bestätigen Sie die Eingabe.

### Termin löschen

1. Wählen Sie einen Termin aus der Tabelle.
2. Klicken Sie auf **Löschen**.
3. Bestätigen Sie die Rückfrage.

Beim Löschen eines Termins wird auch die zugehörige Erinnerung entfernt.

### Status und Anzeige filtern

In der Terminliste gibt es eine Spalte „Status“.
Über diese Spalte kann zwischen offenen und erledigten Terminen unterschieden werden.

Zusätzlich stehen im unteren Bereich der Ansicht Schaltflächen zur Verfügung, mit denen gefiltert werden kann, ob offene oder erledigte Termine angezeigt werden sollen.

Dadurch kann die Terminliste je nach gewünschtem Status eingeschränkt dargestellt werden.

## Lernblöcke

Im Bereich **Lernblöcke** können geplante Lernzeiten verwaltet werden.

Ein Lernblock beschreibt eine konkrete Zeit, in der für ein Modul gelernt werden soll.

### Lernblock anlegen

1. Öffnen Sie den Bereich **Lernblöcke**.
2. Klicken Sie auf **Neu**.
3. Wählen Sie ein Modul aus.
4. Geben Sie Thema, Datum und Startzeit ein.
5. Geben Sie die Dauer in Minuten ein.
6. Legen Sie fest, wie viele Minuten vorher erinnert werden soll.
7. Bestätigen Sie die Eingabe.

Der Lernblock erscheint danach in der Lernblockliste und im Kalender.

### Lernblock bearbeiten

1. Wählen Sie einen Lernblock aus der Tabelle.
2. Klicken Sie auf **Bearbeiten**.
3. Ändern Sie die gewünschten Daten.
4. Bestätigen Sie die Eingabe.

### Lernblock löschen

1. Wählen Sie einen Lernblock aus der Tabelle.
2. Klicken Sie auf **Löschen**.
3. Bestätigen Sie die Rückfrage.

Beim Löschen eines Lernblocks wird auch die zugehörige Erinnerung entfernt.

### Status und Anzeige filtern

In der Lernblockliste gibt es eine Spalte „Status“.
Über diese Spalte kann zwischen offenen und erledigten Lernblöcken unterschieden werden.

Zusätzlich stehen im unteren Bereich der Ansicht Schaltflächen zur Verfügung, mit denen gefiltert werden kann, ob offene oder erledigte Lernblöcke angezeigt werden sollen.

Dadurch kann die Lernblockliste je nach gewünschtem Status eingeschränkt dargestellt werden.

## Erinnerungen

Erinnerungen informieren vor einem Termin oder Lernblock.

Wenn eine Erinnerung fällig ist, erscheint ein Popup-Fenster mit Informationen zum Eintrag.

Eine Erinnerung enthält:

- Art des Eintrags
- Titel oder Thema
- Modul
- Zeitpunkt

Die Vorlaufzeit wird beim Anlegen oder Bearbeiten von Terminen und Lernblöcken festgelegt.

## Einstellungen

Im Bereich **Einstellungen** können globale Einstellungen angepasst werden.

### Erinnerungen aktivieren oder deaktivieren

Mit der Option **Erinnerungen aktiv** können Popup-Erinnerungen ein- oder ausgeschaltet werden.

Wenn Erinnerungen deaktiviert sind, zeigt die Anwendung keine Erinnerungspopups an.

### Standard-Vorlauf festlegen

Der Standard-Vorlauf bestimmt, wie viele Minuten vor einem neuen Termin oder Lernblock standardmäßig erinnert werden soll.

Dieser Wert kann beim einzelnen Termin oder Lernblock weiterhin angepasst werden.

### Einstellungen speichern

Nach einer Änderung klicken Sie auf **Speichern**.

## Datenspeicherung

Die Daten werden lokal auf dem Computer gespeichert. Es gibt keine Cloud-Synchronisierung und keinen Mehrbenutzerbetrieb.

Beim Start der Anwendung werden gespeicherte Daten automatisch geladen.

Beim Anlegen, Bearbeiten oder Löschen von Einträgen werden die Daten automatisch gespeichert.

## Eingabeformate

### Datum

Datumswerte werden im Format `yyyy-MM-dd` eingegeben.

Beispiel:

```text
2026-05-03
```

### Uhrzeit

Uhrzeiten werden im Format `HH:mm` eingegeben.

Beispiel:

```text
14:30
```

### Dauer

Die Dauer eines Lernblocks wird in Minuten eingegeben.

Beispiel:

```text
90
```

## Fehlermeldungen

Wenn eine Eingabe ungültig ist, zeigt die Anwendung eine Fehlermeldung an.

Beispiele:

- Ein Modulname darf nicht leer sein.
- Ein Termin braucht ein Datum.
- Ein Lernblock braucht eine Startzeit.
- Die Dauer muss zwischen 1 und 1440 Minuten liegen.

Korrigieren Sie in diesem Fall die Eingabe und bestätigen Sie erneut.

## Hinweise zur Nutzung

- Legen Sie zuerst Module an.
- Danach können Termine und Lernblöcke diesen Modulen zugeordnet werden.
- Nutzen Sie den Kalender, um eine Wochenübersicht zu erhalten.
- Verwenden Sie Erinnerungen, um wichtige Termine und Lernzeiten nicht zu verpassen.
- Löschen Sie zuerst Termine und Lernblöcke, bevor Sie ein Modul löschen.


