package studyplanner.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import studyplanner.model.Erinnerung;
import studyplanner.model.Lernblock;
import studyplanner.model.Modul;
import studyplanner.model.Termin;
import studyplanner.service.StudyService;
import studyplanner.service.StudyService.FaelligeErinnerung;
import studyplanner.service.StudyService.KalenderEintrag;

/**
 * Hauptfenster der Study-Planner-Anwendung.
 */
public class StudyPlannerFrame extends JFrame {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final Color SIDEBAR_COLOR = new Color(148, 171, 208);
    private static final Color SIDEBAR_HOVER_COLOR = new Color(176, 195, 224);
    private static final Color SIDEBAR_ACTIVE_COLOR = new Color(205, 218, 238);
    private static final Color SIDEBAR_TEXT_COLOR = new Color(2, 17, 64);

    /**
     * Zentrale Serviceschicht fuer Datenzugriff und Logik.
     */
    private final StudyService service;

    /**
     * Layout fuer den Wechsel zwischen den Hauptseiten.
     */
    private final CardLayout contentLayout = new CardLayout();

    /**
     * Container fuer die Hauptseiten der Anwendung.
     */
    private final JPanel contentPanel = new JPanel(contentLayout);

    /**
     * Navigationsbuttons der Seitenleiste.
     */
    private final List<JButton> navigationButtons = new ArrayList<>();

    /**
     * Name der aktuell aktiven Seite.
     */
    private String activeNavigationCard = "Module";

    /**
     * Container fuer die Modul-Karten.
     */
    private final JPanel modulListPanel = new JPanel();

    /**
     * Suchfeld fuer die Modulansicht.
     */
    private final JTextField modulSearchField = new JTextField();

    /**
     * Semesterfilter fuer die Modulansicht.
     */
    private final JComboBox<String> modulSemesterFilter = new JComboBox<>();

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
     * Modulauswahl fuer den Kalenderfilter.
     */
    private final JComboBox<Object> kalenderModulFilter = new JComboBox<>();

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

        contentPanel.add(createModulePanel(), "Module");
        contentPanel.add(createKalenderPanel(), "Kalender");
        contentPanel.add(createLernblockPanel(), "Lernbloecke");
        contentPanel.add(createErinnerungPanel(), "Erinnerungen");
        contentPanel.add(createEinstellungPanel(), "Einstellungen");
        contentPanel.add(createTerminPanel(), "Termine");

        JPanel root = new JPanel(new BorderLayout());
        root.add(createTopBar(), BorderLayout.NORTH);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);

        refreshAll();
        startReminderTimer();
    }

    /**
     * Erstellt die obere Titelleiste.
     *
     * @return Panel fuer die Kopfleiste.
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(151, 174, 211));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel title = new JLabel("Study Planner");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));

        JLabel semester = new JLabel("SS 2026");
        semester.setForeground(Color.WHITE);
        semester.setFont(semester.getFont().deriveFont(11f));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(semester, BorderLayout.EAST);
        return topBar;
    }

    /**
     * Erstellt die linke Navigation.
     *
     * @return Navigationspanel.
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(170, 0));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        sidebar.add(createNavButton("•  Module", "Module", true));
        sidebar.add(createNavButton("•  Kalender", "Kalender", false));
        sidebar.add(createNavButton("•  Termine", "Termine", false));
        sidebar.add(createNavButton("•  Lernbloecke", "Lernbloecke", false));
        sidebar.add(createNavButton("•  Erinnerungen", "Erinnerungen", false));
        sidebar.add(createNavButton("•  Einstellungen", "Einstellungen", false));
        sidebar.add(Box.createVerticalGlue());
        updateNavigationStyles();
        return sidebar;
    }

    /**
     * Erstellt einen Navigationsbutton fuer die Seitenleiste.
     *
     * @param label Sichtbarer Text.
     * @param card Name der Zielseite im CardLayout.
     * @param active Gibt an, ob der Button initial hervorgehoben ist.
     * @return Navigationsbutton.
     */
    private JButton createNavButton(String label, String card, boolean active) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        button.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setForeground(SIDEBAR_TEXT_COLOR);
        button.setOpaque(true);
        button.putClientProperty("card", card);
        button.addActionListener(event -> {
            activeNavigationCard = card;
            updateNavigationStyles();
            contentLayout.show(contentPanel, card);
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                if (!card.equals(activeNavigationCard)) {
                    button.setBackground(SIDEBAR_HOVER_COLOR);
                    button.setFont(button.getFont().deriveFont(Font.BOLD, 16f));
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                updateNavigationStyles();
            }
        });
        navigationButtons.add(button);
        if (active) {
            activeNavigationCard = card;
        }
        return button;
    }

    /**
     * Aktualisiert die Darstellung der Navigationsbuttons passend zur aktiven Seite.
     */
    private void updateNavigationStyles() {
        for (JButton button : navigationButtons) {
            Object card = button.getClientProperty("card");
            boolean active = activeNavigationCard.equals(card);
            button.setBackground(active ? SIDEBAR_ACTIVE_COLOR : SIDEBAR_COLOR);
            button.setFont(button.getFont().deriveFont(active ? Font.BOLD : Font.PLAIN, active ? 18f : 16f));
        }
    }

    /**
     * Erstellt den Tab fuer die Modulverwaltung.
     *
     * @return Panel mit Modultabelle und Aktionen.
     */
    private JPanel createModulePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headline = new JPanel();
        headline.setLayout(new BoxLayout(headline, BoxLayout.Y_AXIS));
        headline.setOpaque(false);

        JLabel title = new JLabel("Module");
        title.setForeground(new Color(0, 15, 74));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));

        JLabel subtitle = new JLabel("Verwalten Sie Ihre Module fuer das aktuelle Semester.");
        subtitle.setFont(subtitle.getFont().deriveFont(13f));

        headline.add(title);
        headline.add(Box.createVerticalStrut(8));
        headline.add(subtitle);

        JButton addButton = button("+ Neues Modul", this::addModul);
        addButton.setBackground(new Color(26, 30, 160));
        addButton.setForeground(Color.blue);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        header.add(headline, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        filters.setOpaque(false);
        modulSearchField.setPreferredSize(new Dimension(250, 36));
        modulSearchField.setToolTipText("Modul suchen");
        modulSearchField.getDocument().addDocumentListener(new SimpleDocumentListener(this::refreshModuleCards));

        modulSemesterFilter.setPreferredSize(new Dimension(210, 36));
        modulSemesterFilter.addItem("Alle Semester");
        for (int semester = 1; semester <= 12; semester++) {
            modulSemesterFilter.addItem("Semester " + semester);
        }
        modulSemesterFilter.addActionListener(event -> refreshModuleCards());

        filters.add(modulSearchField);
        filters.add(modulSemesterFilter);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(filters, BorderLayout.SOUTH);

        modulListPanel.setLayout(new BoxLayout(modulListPanel, BoxLayout.Y_AXIS));
        modulListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(modulListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
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
        kalenderTable.setModel(new DefaultTableModel(new Object[]{"Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        kalenderTable.setRowHeight(78);
        kalenderTable.setCellSelectionEnabled(false);
        kalenderTable.setFillsViewportHeight(true);
        kalenderTable.getTableHeader().setReorderingAllowed(false);

        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(kalenderDatum, "yyyy-MM-dd");
        kalenderDatum.setEditor(dateEditor);
        kalenderDatum.addChangeListener(event -> refreshKalender());
        kalenderModulFilter.addActionListener(event -> refreshKalender());

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Woche"));
        filters.add(kalenderDatum);
        filters.add(new JLabel("Modul"));
        filters.add(kalenderModulFilter);

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
     * Erstellt eine einfache Uebersicht der gespeicherten Erinnerungen.
     *
     * @return Panel mit Erinnerungstabelle.
     */
    private JPanel createErinnerungPanel() {
        JPanel panel = basePanel();
        JTable erinnerungTable = new JTable(new DefaultTableModel(new Object[]{"Typ", "Vorlauf", "Aktiv"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        DefaultTableModel model = (DefaultTableModel) erinnerungTable.getModel();
        for (Erinnerung erinnerung : service.getErinnerungen()) {
            model.addRow(new Object[]{erinnerung.getZielTyp(), erinnerung.getVorlaufMinuten() + " Min.", erinnerung.isAktiv() ? "Ja" : "Nein"});
        }
        panel.add(new JScrollPane(erinnerungTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Oeffnet den Dialog zum Anlegen eines neuen Moduls.
     */
    private void addModul() {
        ModulDialog dialog = new ModulDialog(null);
        if (dialog.show()) {
            try {
                service.addModul(dialog.name.getText(), dialog.farbe.getText(), dialog.notizen.getText(), (Integer) dialog.semester.getValue());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Oeffnet den Dialog zum Bearbeiten eines Moduls anhand seiner ID.
     *
     * @param id ID des Moduls.
     */
    private void editModul(UUID id) {
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
                service.updateModul(id, dialog.name.getText(), dialog.farbe.getText(), dialog.notizen.getText(), (Integer) dialog.semester.getValue());
                refreshAll();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                showError(ex.getMessage());
            }
        }
    }

    /**
     * Loescht ein Modul anhand seiner ID.
     *
     * @param id ID des Moduls.
     */
    private void deleteModul(UUID id) {
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
        refreshModuleCards();
        refreshTermin();
        refreshLernblock();
        refreshModuleFilter();
        refreshKalender();
    }

    /**
     * Aktualisiert die Kartenliste der Modulansicht.
     */
    private void refreshModuleCards() {
        modulListPanel.removeAll();

        String search = modulSearchField.getText() == null ? "" : modulSearchField.getText().trim().toLowerCase();
        int semesterFilter = modulSemesterFilter.getSelectedIndex();

        List<Modul> filtered = service.getModule().stream()
                .filter(modul -> search.isBlank()
                        || modul.getName().toLowerCase().contains(search)
                        || modul.getNotizen().toLowerCase().contains(search))
                .filter(modul -> semesterFilter <= 0 || modul.getSemester() == semesterFilter)
                .toList();

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Keine Module gefunden.");
            empty.setBorder(BorderFactory.createEmptyBorder(24, 14, 24, 14));
            empty.setForeground(new Color(80, 80, 80));
            modulListPanel.add(empty);
        } else {
            for (Modul modul : filtered) {
                modulListPanel.add(createModuleCard(modul));
                modulListPanel.add(Box.createVerticalStrut(22));
            }
        }

        modulListPanel.revalidate();
        modulListPanel.repaint();
    }

    /**
     * Erstellt eine Karte fuer ein Modul.
     *
     * @param modul Darzustellendes Modul.
     * @return Kartenpanel.
     */
    private JPanel createModuleCard(Modul modul) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 25, 25)),
                BorderFactory.createEmptyBorder(16, 20, 16, 18)
        ));

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel name = new JLabel(modul.getName());
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 14f));

        JLabel semester = new JLabel("Semester: " + modul.getSemester());
        semester.setFont(semester.getFont().deriveFont(13f));

        JLabel notes = new JLabel(modul.getNotizen().isBlank() ? "Keine Beschreibung vorhanden." : modul.getNotizen());
        notes.setFont(notes.getFont().deriveFont(13f));

        text.add(name);
        text.add(semester);
        text.add(notes);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.add(createLinkButton("Bearbeiten", () -> editModul(modul.getId()), new Color(30, 30, 30)));
        actions.add(Box.createVerticalStrut(8));
        actions.add(createLinkButton("Loeschen", () -> deleteModul(modul.getId()), new Color(140, 20, 20)));

        card.add(text, BorderLayout.CENTER);
        card.add(actions, BorderLayout.EAST);
        return card;
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
            LocalDate weekStart = selectedDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = selectedDate.with(DayOfWeek.SUNDAY);

            UUID modulId = null;
            if (kalenderModulFilter.getSelectedItem() instanceof Modul modul) {
                modulId = modul.getId();
            }

            DefaultTableModel model = (DefaultTableModel) kalenderTable.getModel();
            model.setColumnIdentifiers(createWeekHeaders());
            model.setRowCount(0);

            List<List<String>> entriesByDay = new ArrayList<>();
            for (int day = 0; day < 7; day++) {
                entriesByDay.add(new ArrayList<>());
            }

            for (KalenderEintrag eintrag : service.getKalenderEintraege(weekStart, weekEnd, modulId, null, null)) {
                int dayIndex = eintrag.datum().getDayOfWeek().getValue() - 1;
                entriesByDay.get(dayIndex).add(formatCalendarEntry(eintrag));
            }

            int rowCount = entriesByDay.stream().mapToInt(List::size).max().orElse(1);
            for (int row = 0; row < rowCount; row++) {
                Object[] cells = new Object[7];
                for (int day = 0; day < 7; day++) {
                    List<String> dayEntries = entriesByDay.get(day);
                    cells[day] = row < dayEntries.size() ? dayEntries.get(row) : "";
                }
                model.addRow(cells);
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Erstellt die Spaltenueberschriften fuer eine Kalenderwoche.
     *
     * @return Spaltenueberschriften von Montag bis Sonntag.
     */
    private Object[] createWeekHeaders() {
        return new Object[]{
                "Montag",
                "Dienstag",
                "Mittwoch",
                "Donnerstag",
                "Freitag",
                "Samstag",
                "Sonntag"
        };
    }

    /**
     * Formatiert einen Kalender-Eintrag fuer die Wochen-Tabelle.
     *
     * @param eintrag Kalender-Eintrag.
     * @return Mehrzeiliger Text fuer eine Tabellenzelle.
     */
    private String formatCalendarEntry(KalenderEintrag eintrag) {
        String dauer = eintrag.dauerMinuten() > 0 ? " (" + eintrag.dauerMinuten() + " Min.)" : "";
        return "<html><b>" + eintrag.zeit().format(TIME_FORMAT) + "</b> " + eintrag.typ()
                + "<br>" + eintrag.titel()
                + "<br><span style='color:#4d4d4d'>" + service.getModulName(eintrag.modulId()) + dauer + "</span></html>";
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
     * Erstellt einen Button, der optisch wie ein Textlink wirkt.
     *
     * @param text Sichtbarer Text.
     * @param action Klickaktion.
     * @param color Textfarbe.
     * @return Linkartiger Button.
     */
    private JButton createLinkButton(String text, Runnable action, Color color) {
        JButton button = new JButton("<html><u>" + text + "</u></html>");
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(color);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.RIGHT_ALIGNMENT);
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
        private final JSpinner semester = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
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
                semester.setValue(modul.getSemester());
                notizen.setText(modul.getNotizen());
            }
            addFormRow(panel, 0, "Name", name);
            addFormRow(panel, 1, "Semester", semester);
            addFormRow(panel, 2, "Farbe", farbe);
            addFormRow(panel, 3, "Beschreibung", new JScrollPane(notizen));
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
     * Kleiner DocumentListener, der alle Dokumentaenderungen an dieselbe Aktion weitergibt.
     */
    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        /**
         * Erstellt den Listener.
         *
         * @param action Aktion bei Textaenderung.
         */
        SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void insertUpdate(DocumentEvent event) {
            action.run();
        }

        @Override
        public void removeUpdate(DocumentEvent event) {
            action.run();
        }

        @Override
        public void changedUpdate(DocumentEvent event) {
            action.run();
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
