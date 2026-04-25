package studyplanner.ui;

import studyplanner.model.Erinnerung;
import studyplanner.model.Lernblock;
import studyplanner.model.Modul;
import studyplanner.model.Termin;
import studyplanner.service.StudyService;
import studyplanner.service.StudyService.FaelligeErinnerung;
import studyplanner.service.StudyService.KalenderEintrag;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Hauptfenster der Study-Planner-Anwendung.
 */
public class StudyPlannerFrame extends JFrame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Zentrale Serviceschicht fuer Datenzugriff und Logik.
     */
    private final StudyService service;

    /**
     * Tabelle fuer Module.
     */
    private final JTable modulTable = new JTable();

    /**
     * Tabelle fuer Termine.
     */
    private final JTable terminTable = new JTable();

    /**
     * Tabelle fuer Lernbloecke.
     */
    private final JTable lernblockTable = new JTable();

    /**
     * Tabelle fuer Kalender-Eintraege.
     */
    private final JTable kalenderTable = new JTable();

    /**
     * Datumsfilter der Kalenderansicht.
     */
    private final JSpinner kalenderDatum = new JSpinner(new SpinnerDateModel());

    /**
     * Auswahl zwischen Tages- und Wochenansicht.
     */
    private final JComboBox<String> kalenderModus = new JComboBox<>(new String[]{"Tag", "Woche"});

    /**
     * Modulauswahl fuer den Kalenderfilter.
     */
    private final JComboBox<Object> kalenderModulFilter = new JComboBox<>();

    /**
     * Startzeitfilter der Kalenderansicht.
     */
    private final JTextField kalenderStartFilter = new JTextField(5);

    /**
     * Endzeitfilter der Kalenderansicht.
     */
    private final JTextField kalenderEndeFilter = new JTextField(5);

    /**
     * Checkbox fuer globale Erinnerungseinstellung.
     */
    private final JCheckBox erinnerungenAktiv = new JCheckBox("Erinnerungen aktiv");

    /**
     * Eingabefeld fuer den Standard-Vorlauf neuer Erinnerungen.
     */
    private final JSpinner standardVorlauf = new JSpinner(new SpinnerNumberModel(30, 0, 10080, 5));

    /**
     * Erstellt das Hauptfenster und verbindet es mit der Serviceschicht.
     *
     * @param service Zentrale Anwendungslogik.
     */
    public StudyPlannerFrame(StudyService service) {
        super("Study Planner");
        this.service = service;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Module", createModulePanel());
        tabs.addTab("Termine", createTerminPanel());
        tabs.addTab("Lernbloecke", createLernblockPanel());
        tabs.addTab("Kalender", createKalenderPanel());
        tabs.addTab("Einstellungen", createEinstellungPanel());
        add(tabs, BorderLayout.CENTER);

        refreshAll();
        startReminderTimer();
    }

    /**
     * Erstellt den Tab fuer die Modulverwaltung.
     *
     * @return Panel mit Modultabelle und Aktionen.
     */
    private JPanel createModulePanel() {
        JPanel panel = basePanel();
        modulTable.setModel(new DefaultTableModel(new Object[]{"ID", "Name", "Farbe", "Notizen"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        hideIdColumn(modulTable);
        panel.add(new JScrollPane(modulTable), BorderLayout.CENTER);
        panel.add(buttonBar(
                button("Neu", this::addModul),
                button("Bearbeiten", this::editModul),
                button("Loeschen", this::deleteModul)
        ), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Erstellt den Tab fuer die Terminverwaltung.
     *
     * @return Panel mit Termintabelle und Aktionen.
     */
    private JPanel createTerminPanel() {
        JPanel panel = basePanel();
        terminTable.setModel(new DefaultTableModel(new Object[]{"ID", "Titel", "Modul", "Datum", "Uhrzeit", "Erinnerung"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        hideIdColumn(terminTable);
        panel.add(new JScrollPane(terminTable), BorderLayout.CENTER);
        panel.add(buttonBar(
                button("Neu", this::addTermin),
                button("Bearbeiten", this::editTermin),
                button("Loeschen", this::deleteTermin)
        ), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Erstellt den Tab fuer die Lernblockverwaltung.
     *
     * @return Panel mit Lernblocktabelle und Aktionen.
     */
    private JPanel createLernblockPanel() {
        JPanel panel = basePanel();
        lernblockTable.setModel(new DefaultTableModel(new Object[]{"ID", "Thema", "Modul", "Datum", "Start", "Dauer", "Erinnerung"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        hideIdColumn(lernblockTable);
        panel.add(new JScrollPane(lernblockTable), BorderLayout.CENTER);
        panel.add(buttonBar(
                button("Neu", this::addLernblock),
                button("Bearbeiten", this::editLernblock),
                button("Loeschen", this::deleteLernblock)
        ), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Erstellt den Kalender-Tab mit Filtern.
     *
     * @return Panel mit Kalenderfiltern und Ergebnisliste.
     */
    private JPanel createKalenderPanel() {
        JPanel panel = basePanel();
        kalenderTable.setModel(new DefaultTableModel(new Object[]{"Typ", "Titel", "Modul", "Datum", "Zeit", "Dauer"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(kalenderDatum, "yyyy-MM-dd");
        kalenderDatum.setEditor(dateEditor);
        kalenderModus.addActionListener(event -> refreshKalender());
        kalenderModulFilter.addActionListener(event -> refreshKalender());
        kalenderStartFilter.setToolTipText("HH:mm");
        kalenderEndeFilter.setToolTipText("HH:mm");

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Datum"));
        filters.add(kalenderDatum);
        filters.add(new JLabel("Ansicht"));
        filters.add(kalenderModus);
        filters.add(new JLabel("Modul"));
        filters.add(kalenderModulFilter);
        filters.add(new JLabel("Von"));
        filters.add(kalenderStartFilter);
        filters.add(new JLabel("Bis"));
        filters.add(kalenderEndeFilter);
        filters.add(button("Aktualisieren", this::refreshKalender));

        panel.add(filters, BorderLayout.NORTH);
        panel.add(new JScrollPane(kalenderTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Erstellt den Tab fuer globale Einstellungen.
     *
     * @return Panel mit Einstellungselementen.
     */
    private JPanel createEinstellungPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        erinnerungenAktiv.setSelected(service.getEinstellung().isErinnerungenAktiv());
        standardVorlauf.setValue(service.getEinstellung().getStandardVorlaufMinuten());

        addFormRow(panel, 0, "Popup-Erinnerungen", erinnerungenAktiv);
        addFormRow(panel, 1, "Standard-Vorlauf in Minuten", standardVorlauf);
        addFormRow(panel, 2, "Speicherort", new JLabel(System.getProperty("user.home") + "/.studyplanner/studyplanner.dat"));

        JButton save = button("Speichern", () -> {
            try {
                service.updateEinstellung((Integer) standardVorlauf.getValue(), erinnerungenAktiv.isSelected());
                showInfo("Einstellungen wurden gespeichert.");
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        });
        GridBagConstraints c = constraints(3, 1);
        c.anchor = GridBagConstraints.WEST;
        panel.add(save, c);
        return panel;
    }

    /**
     * Oeffnet den Dialog zum Anlegen eines neuen Moduls.
     */
    private void addModul() {
        ModulDialog dialog = new ModulDialog(null);
        if (dialog.show()) {
            try {
                service.addModul(dialog.name.getText(), dialog.farbe.getText(), dialog.notizen.getText());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Oeffnet den Dialog zum Bearbeiten des ausgewaehlten Moduls.
     */
    private void editModul() {
        UUID id = selectedId(modulTable);
        if (id == null) {
            return;
        }
        Optional<Modul> modul = service.findModul(id);
        if (modul.isEmpty()) {
            showError("Modul wurde nicht gefunden.");
            return;
        }
        ModulDialog dialog = new ModulDialog(modul.get());
        if (dialog.show()) {
            try {
                service.updateModul(id, dialog.name.getText(), dialog.farbe.getText(), dialog.notizen.getText());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Loescht das ausgewaehlte Modul nach Bestaetigung.
     */
    private void deleteModul() {
        UUID id = selectedId(modulTable);
        if (id == null || !confirmDelete()) {
            return;
        }
        try {
            service.deleteModul(id);
            refreshAll();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Oeffnet den Dialog zum Anlegen eines neuen Termins.
     */
    private void addTermin() {
        if (service.getModule().isEmpty()) {
            showError("Bitte zuerst ein Modul anlegen.");
            return;
        }
        TerminDialog dialog = new TerminDialog(null);
        if (dialog.show()) {
            try {
                service.addTermin(dialog.selectedModulId(), dialog.titel.getText(), parseDate(dialog.datum.getText()),
                        parseTime(dialog.uhrzeit.getText()), dialog.notizen.getText(), (Integer) dialog.vorlauf.getValue());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Oeffnet den Dialog zum Bearbeiten des ausgewaehlten Termins.
     */
    private void editTermin() {
        UUID id = selectedId(terminTable);
        if (id == null) {
            return;
        }
        Optional<Termin> termin = service.findTermin(id);
        if (termin.isEmpty()) {
            showError("Termin wurde nicht gefunden.");
            return;
        }
        TerminDialog dialog = new TerminDialog(termin.get());
        if (dialog.show()) {
            try {
                service.updateTermin(id, dialog.selectedModulId(), dialog.titel.getText(), parseDate(dialog.datum.getText()),
                        parseTime(dialog.uhrzeit.getText()), dialog.notizen.getText(), (Integer) dialog.vorlauf.getValue(),
                        dialog.erinnerungAktiv.isSelected());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Loescht den ausgewaehlten Termin nach Bestaetigung.
     */
    private void deleteTermin() {
        UUID id = selectedId(terminTable);
        if (id == null || !confirmDelete()) {
            return;
        }
        service.deleteTermin(id);
        refreshAll();
    }

    /**
     * Oeffnet den Dialog zum Anlegen eines neuen Lernblocks.
     */
    private void addLernblock() {
        if (service.getModule().isEmpty()) {
            showError("Bitte zuerst ein Modul anlegen.");
            return;
        }
        LernblockDialog dialog = new LernblockDialog(null);
        if (dialog.show()) {
            try {
                service.addLernblock(dialog.selectedModulId(), dialog.thema.getText(), parseDate(dialog.datum.getText()),
                        parseTime(dialog.startzeit.getText()), (Integer) dialog.dauer.getValue(), (Integer) dialog.vorlauf.getValue());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Oeffnet den Dialog zum Bearbeiten des ausgewaehlten Lernblocks.
     */
    private void editLernblock() {
        UUID id = selectedId(lernblockTable);
        if (id == null) {
            return;
        }
        Optional<Lernblock> lernblock = service.findLernblock(id);
        if (lernblock.isEmpty()) {
            showError("Lernblock wurde nicht gefunden.");
            return;
        }
        LernblockDialog dialog = new LernblockDialog(lernblock.get());
        if (dialog.show()) {
            try {
                service.updateLernblock(id, dialog.selectedModulId(), dialog.thema.getText(), parseDate(dialog.datum.getText()),
                        parseTime(dialog.startzeit.getText()), (Integer) dialog.dauer.getValue(), (Integer) dialog.vorlauf.getValue(),
                        dialog.erinnerungAktiv.isSelected());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Loescht den ausgewaehlten Lernblock nach Bestaetigung.
     */
    private void deleteLernblock() {
        UUID id = selectedId(lernblockTable);
        if (id == null || !confirmDelete()) {
            return;
        }
        service.deleteLernblock(id);
        refreshAll();
    }

    /**
     * Aktualisiert alle Tabellen und Filterlisten.
     */
    private void refreshAll() {
        refreshModule();
        refreshTermin();
        refreshLernblock();
        refreshModuleFilter();
        refreshKalender();
    }

    /**
     * Aktualisiert die Modultabelle.
     */
    private void refreshModule() {
        DefaultTableModel model = (DefaultTableModel) modulTable.getModel();
        model.setRowCount(0);
        for (Modul modul : service.getModule()) {
            model.addRow(new Object[]{modul.getId(), modul.getName(), modul.getFarbe(), modul.getNotizen()});
        }
    }

    /**
     * Aktualisiert die Termintabelle.
     */
    private void refreshTermin() {
        DefaultTableModel model = (DefaultTableModel) terminTable.getModel();
        model.setRowCount(0);
        for (Termin termin : service.getTermine()) {
            String erinnerung = service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId())
                    .map(e -> e.isAktiv() ? e.getVorlaufMinuten() + " Min." : "Aus")
                    .orElse("Aus");
            model.addRow(new Object[]{termin.getId(), termin.getTitel(), service.getModulName(termin.getModulId()),
                    termin.getDatum(), termin.getUhrzeit().format(TIME_FORMAT), erinnerung});
        }
    }

    /**
     * Aktualisiert die Lernblocktabelle.
     */
    private void refreshLernblock() {
        DefaultTableModel model = (DefaultTableModel) lernblockTable.getModel();
        model.setRowCount(0);
        for (Lernblock block : service.getLernbloecke()) {
            String erinnerung = service.findErinnerung(Erinnerung.ZielTyp.LERNBLOCK, block.getId())
                    .map(e -> e.isAktiv() ? e.getVorlaufMinuten() + " Min." : "Aus")
                    .orElse("Aus");
            model.addRow(new Object[]{block.getId(), block.getThema(), service.getModulName(block.getModulId()),
                    block.getDatum(), block.getStartzeit().format(TIME_FORMAT), block.getDauerMinuten() + " Min.", erinnerung});
        }
    }

    /**
     * Aktualisiert die Modulauswahl im Kalenderfilter.
     */
    private void refreshModuleFilter() {
        Object selected = kalenderModulFilter.getSelectedItem();
        kalenderModulFilter.removeAllItems();
        kalenderModulFilter.addItem("Alle");
        for (Modul modul : service.getModule()) {
            kalenderModulFilter.addItem(modul);
        }
        if (selected instanceof Modul selectedModul) {
            for (int i = 0; i < kalenderModulFilter.getItemCount(); i++) {
                Object item = kalenderModulFilter.getItemAt(i);
                if (item instanceof Modul modul && modul.getId().equals(selectedModul.getId())) {
                    kalenderModulFilter.setSelectedIndex(i);
                    return;
                }
            }
        }
        kalenderModulFilter.setSelectedIndex(0);
    }

    /**
     * Aktualisiert die Kalenderansicht anhand der gesetzten Filter.
     */
    private void refreshKalender() {
        try {
            LocalDate selectedDate = ((Date) kalenderDatum.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate von = selectedDate;
            LocalDate bis = selectedDate;
            if ("Woche".equals(kalenderModus.getSelectedItem())) {
                von = selectedDate.with(DayOfWeek.MONDAY);
                bis = selectedDate.with(DayOfWeek.SUNDAY);
            }

            UUID modulId = null;
            if (kalenderModulFilter.getSelectedItem() instanceof Modul modul) {
                modulId = modul.getId();
            }

            LocalTime start = parseOptionalTime(kalenderStartFilter.getText());
            LocalTime ende = parseOptionalTime(kalenderEndeFilter.getText());

            DefaultTableModel model = (DefaultTableModel) kalenderTable.getModel();
            model.setRowCount(0);
            for (KalenderEintrag eintrag : service.getKalenderEintraege(von, bis, modulId, start, ende)) {
                String dauer = eintrag.dauerMinuten() > 0 ? eintrag.dauerMinuten() + " Min." : "";
                model.addRow(new Object[]{eintrag.typ(), eintrag.titel(), service.getModulName(eintrag.modulId()),
                        eintrag.datum(), eintrag.zeit().format(TIME_FORMAT), dauer});
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Startet den Timer, der regelmaessig faellige Erinnerungen prueft.
     */
    private void startReminderTimer() {
        Timer timer = new Timer(30000, event -> showDueReminders());
        timer.setInitialDelay(3000);
        timer.start();
    }

    /**
     * Zeigt alle aktuell faelligen Erinnerungen als Popup an.
     */
    private void showDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        List<FaelligeErinnerung> reminders = service.getFaelligeErinnerungen(now);
        for (FaelligeErinnerung reminder : reminders) {
            JOptionPane.showMessageDialog(this,
                    reminder.typ() + ": " + reminder.titel() + "\nModul: " + reminder.modulName()
                            + "\nZeit: " + reminder.zeitpunkt().format(DATE_TIME_FORMAT),
                    "Erinnerung",
                    JOptionPane.INFORMATION_MESSAGE);
            service.markErinnerungAngezeigt(reminder.erinnerungId(), now);
        }
    }

    /**
     * Erstellt ein Standard-Panel mit BorderLayout und Rand.
     *
     * @return Neues Basis-Panel.
     */
    private JPanel basePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return panel;
    }

    /**
     * Erstellt eine rechtsbuendige Buttonleiste.
     *
     * @param buttons Buttons, die angezeigt werden sollen.
     * @return Panel mit Buttons.
     */
    private JPanel buttonBar(JButton... buttons) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        for (JButton button : buttons) {
            panel.add(button);
        }
        return panel;
    }

    /**
     * Erstellt einen Button mit Aktion.
     *
     * @param text Beschriftung des Buttons.
     * @param action Aktion, die beim Klick ausgefuehrt wird.
     * @return Neuer Button.
     */
    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    /**
     * Blendet die ID-Spalte einer Tabelle aus.
     *
     * @param table Tabelle mit ID-Spalte an erster Position.
     */
    private void hideIdColumn(JTable table) {
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
    }

    /**
     * Liest die ID der aktuell ausgewaehlten Tabellenzeile aus.
     *
     * @param table Tabelle mit ID-Spalte an erster Position.
     * @return Ausgewaehlte ID oder {@code null}, wenn nichts ausgewaehlt ist.
     */
    private UUID selectedId(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Bitte zuerst einen Eintrag auswaehlen.");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(row);
        return (UUID) table.getModel().getValueAt(modelRow, 0);
    }

    /**
     * Fragt den Benutzer, ob ein Eintrag geloescht werden soll.
     *
     * @return {@code true}, wenn der Benutzer das Loeschen bestaetigt.
     */
    private boolean confirmDelete() {
        return JOptionPane.showConfirmDialog(this, "Eintrag wirklich loeschen?", "Loeschen",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /**
     * Wandelt einen Datumstext in ein {@link LocalDate} um.
     *
     * @param value Datum im Format yyyy-MM-dd.
     * @return Geparstes Datum.
     */
    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Datum bitte im Format yyyy-MM-dd eingeben.");
        }
    }

    /**
     * Wandelt einen Uhrzeittext in ein {@link LocalTime} um.
     *
     * @param value Uhrzeit im Format HH:mm.
     * @return Geparste Uhrzeit.
     */
    private LocalTime parseTime(String value) {
        try {
            return LocalTime.parse(value.trim(), TIME_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Uhrzeit bitte im Format HH:mm eingeben.");
        }
    }

    /**
     * Wandelt einen optionalen Uhrzeittext um.
     *
     * @param value Uhrzeit im Format HH:mm oder leer.
     * @return Geparste Uhrzeit oder {@code null}.
     */
    private LocalTime parseOptionalTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return parseTime(value);
    }

    /**
     * Zeigt eine Fehlermeldung an.
     *
     * @param message Text der Fehlermeldung.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Zeigt eine Hinweismeldung an.
     *
     * @param message Text der Hinweismeldung.
     */
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Hinweis", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Fuegt eine beschriftete Formularzeile in ein GridBagLayout ein.
     *
     * @param panel Ziel-Panel.
     * @param row Zeilennummer.
     * @param label Beschriftung.
     * @param component Eingabekomponente.
     */
    private void addFormRow(JPanel panel, int row, String label, java.awt.Component component) {
        GridBagConstraints left = constraints(row, 0);
        left.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel(label), left);
        GridBagConstraints right = constraints(row, 1);
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, right);
    }

    /**
     * Erstellt GridBagConstraints fuer eine Formularposition.
     *
     * @param row Zeilennummer.
     * @param column Spaltennummer.
     * @return Vorbereitete Constraints.
     */
    private GridBagConstraints constraints(int row, int column) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = column;
        c.gridy = row;
        c.insets = new Insets(6, 6, 6, 6);
        return c;
    }

    /**
     * Dialog zum Anlegen und Bearbeiten eines Moduls.
     */
    private class ModulDialog {
        private final JTextField name = new JTextField(24);
        private final JTextField farbe = new JTextField(10);
        private final JTextArea notizen = new JTextArea(4, 24);
        private final JPanel panel = new JPanel(new GridBagLayout());

        /**
         * Erstellt den Moduldialog.
         *
         * @param modul Vorhandenes Modul oder {@code null} fuer ein neues Modul.
         */
        ModulDialog(Modul modul) {
            if (modul == null) {
                farbe.setText("#4F7CAC");
            } else {
                name.setText(modul.getName());
                farbe.setText(modul.getFarbe());
                notizen.setText(modul.getNotizen());
            }
            addFormRow(panel, 0, "Name", name);
            addFormRow(panel, 1, "Farbe", farbe);
            addFormRow(panel, 2, "Notizen", new JScrollPane(notizen));
        }

        /**
         * Zeigt den Dialog an.
         *
         * @return {@code true}, wenn der Benutzer bestaetigt hat.
         */
        boolean show() {
            return JOptionPane.showConfirmDialog(StudyPlannerFrame.this, panel, "Modul",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
        }
    }

    /**
     * Dialog zum Anlegen und Bearbeiten eines Termins.
     */
    private class TerminDialog {
        private final JComboBox<Modul> modul = new JComboBox<>();
        private final JTextField titel = new JTextField(24);
        private final JTextField datum = new JTextField(10);
        private final JTextField uhrzeit = new JTextField(5);
        private final JTextArea notizen = new JTextArea(4, 24);
        private final JSpinner vorlauf = new JSpinner(new SpinnerNumberModel(service.getEinstellung().getStandardVorlaufMinuten(), 0, 10080, 5));
        private final JCheckBox erinnerungAktiv = new JCheckBox("Aktiv", true);
        private final JPanel panel = new JPanel(new GridBagLayout());

        /**
         * Erstellt den Termindialog.
         *
         * @param termin Vorhandener Termin oder {@code null} fuer einen neuen Termin.
         */
        TerminDialog(Termin termin) {
            service.getModule().forEach(modul::addItem);
            datum.setText(LocalDate.now().format(DATE_FORMAT));
            uhrzeit.setText("09:00");
            if (termin != null) {
                selectModul(termin.getModulId(), modul);
                titel.setText(termin.getTitel());
                datum.setText(termin.getDatum().format(DATE_FORMAT));
                uhrzeit.setText(termin.getUhrzeit().format(TIME_FORMAT));
                notizen.setText(termin.getNotizen());
                service.findErinnerung(Erinnerung.ZielTyp.TERMIN, termin.getId()).ifPresent(erinnerung -> {
                    vorlauf.setValue(erinnerung.getVorlaufMinuten());
                    erinnerungAktiv.setSelected(erinnerung.isAktiv());
                });
            }
            addFormRow(panel, 0, "Modul", modul);
            addFormRow(panel, 1, "Titel", titel);
            addFormRow(panel, 2, "Datum yyyy-MM-dd", datum);
            addFormRow(panel, 3, "Uhrzeit HH:mm", uhrzeit);
            addFormRow(panel, 4, "Notizen", new JScrollPane(notizen));
            addFormRow(panel, 5, "Vorlauf Minuten", vorlauf);
            addFormRow(panel, 6, "Erinnerung", erinnerungAktiv);
        }

        /**
         * Gibt die aktuell ausgewaehlte Modul-ID zurueck.
         *
         * @return Modul-ID oder {@code null}.
         */
        UUID selectedModulId() {
            Modul selected = (Modul) modul.getSelectedItem();
            return selected == null ? null : selected.getId();
        }

        /**
         * Zeigt den Dialog an.
         *
         * @return {@code true}, wenn der Benutzer bestaetigt hat.
         */
        boolean show() {
            return JOptionPane.showConfirmDialog(StudyPlannerFrame.this, panel, "Termin",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
        }
    }

    /**
     * Dialog zum Anlegen und Bearbeiten eines Lernblocks.
     */
    private class LernblockDialog {
        private final JComboBox<Modul> modul = new JComboBox<>();
        private final JTextField thema = new JTextField(24);
        private final JTextField datum = new JTextField(10);
        private final JTextField startzeit = new JTextField(5);
        private final JSpinner dauer = new JSpinner(new SpinnerNumberModel(60, 1, 1440, 15));
        private final JSpinner vorlauf = new JSpinner(new SpinnerNumberModel(service.getEinstellung().getStandardVorlaufMinuten(), 0, 10080, 5));
        private final JCheckBox erinnerungAktiv = new JCheckBox("Aktiv", true);
        private final JPanel panel = new JPanel(new GridBagLayout());

        /**
         * Erstellt den Lernblockdialog.
         *
         * @param lernblock Vorhandener Lernblock oder {@code null} fuer einen neuen Lernblock.
         */
        LernblockDialog(Lernblock lernblock) {
            service.getModule().forEach(modul::addItem);
            datum.setText(LocalDate.now().format(DATE_FORMAT));
            startzeit.setText("09:00");
            if (lernblock != null) {
                selectModul(lernblock.getModulId(), modul);
                thema.setText(lernblock.getThema());
                datum.setText(lernblock.getDatum().format(DATE_FORMAT));
                startzeit.setText(lernblock.getStartzeit().format(TIME_FORMAT));
                dauer.setValue(lernblock.getDauerMinuten());
                service.findErinnerung(Erinnerung.ZielTyp.LERNBLOCK, lernblock.getId()).ifPresent(erinnerung -> {
                    vorlauf.setValue(erinnerung.getVorlaufMinuten());
                    erinnerungAktiv.setSelected(erinnerung.isAktiv());
                });
            }
            addFormRow(panel, 0, "Modul", modul);
            addFormRow(panel, 1, "Thema", thema);
            addFormRow(panel, 2, "Datum yyyy-MM-dd", datum);
            addFormRow(panel, 3, "Start HH:mm", startzeit);
            addFormRow(panel, 4, "Dauer Minuten", dauer);
            addFormRow(panel, 5, "Vorlauf Minuten", vorlauf);
            addFormRow(panel, 6, "Erinnerung", erinnerungAktiv);
        }

        /**
         * Gibt die aktuell ausgewaehlte Modul-ID zurueck.
         *
         * @return Modul-ID oder {@code null}.
         */
        UUID selectedModulId() {
            Modul selected = (Modul) modul.getSelectedItem();
            return selected == null ? null : selected.getId();
        }

        /**
         * Zeigt den Dialog an.
         *
         * @return {@code true}, wenn der Benutzer bestaetigt hat.
         */
        boolean show() {
            return JOptionPane.showConfirmDialog(StudyPlannerFrame.this, panel, "Lernblock",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
        }
    }

    /**
     * Waehlt ein Modul in einer ComboBox anhand der Modul-ID aus.
     *
     * @param modulId Gesuchte Modul-ID.
     * @param comboBox ComboBox mit Modulen.
     */
    private void selectModul(UUID modulId, JComboBox<Modul> comboBox) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).getId().equals(modulId)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }
}
