import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClinicGUI extends JFrame {
    private Clinic clinic;
    private ArrayList<Clinic> clinics;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private static final String CLINICS_DATA_FILE = "data/clinics.dat";
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(46, 204, 113);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BG_COLOR = new Color(236, 240, 241);
    private final Color CARD_BG = Color.WHITE;
    
    public ClinicGUI() {
        setTitle("Healthcare Management System");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize data folder
        FileManager.initializeDataFolder();
        
        // Load existing clinics or initialize new list
        clinics = loadClinics();
        if (clinics == null) {
            clinics = new ArrayList<>();
        }
        
        // Initialize with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Add window closing listener to save data before exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveClinics();
                System.out.println("All clinic data saved. Goodbye!");
                System.exit(0);
            }
        });
        
        // Show clinic selection menu
        showClinicSelectionMenu();
        
        add(mainPanel);
        setVisible(true);
    }
    
    private void setupKeyboardShortcuts() {
        // Add keyboard shortcuts for quick navigation
        JRootPane rootPane = getRootPane();
        
        // F1 - Help/Statistics
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("F1"), "showStats");
        rootPane.getActionMap().put("showStats", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) showStatistics();
            }
        });
        
        // Ctrl+D - Doctors
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control D"), "showDoctors");
        rootPane.getActionMap().put("showDoctors", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) showDoctorManagement();
            }
        });
        
        // Ctrl+P - Patients
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control P"), "showPatients");
        rootPane.getActionMap().put("showPatients", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) showPatientManagement();
            }
        });
        
        // Ctrl+A - Appointments
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control A"), "showAppointments");
        rootPane.getActionMap().put("showAppointments", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) showAppointmentManagement();
            }
        });
        
        // Ctrl+H - Home/Dashboard
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control H"), "showHome");
        rootPane.getActionMap().put("showHome", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) showMainDashboard();
            }
        });

        // Ctrl+S - Save Current Clinic
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke("control S"), "saveClinic");
        rootPane.getActionMap().put("saveClinic", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clinic != null) saveCurrentClinic();
            }
        });
    }
    
    private void showClinicSetup() {
        JPanel setupPanel = new JPanel(new GridBagLayout());
        setupPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Create card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(40, 50, 40, 50)
        ));
        
        // Logo with enhanced styling
        try {
            java.io.File logoFile = new java.io.File("Project12th/src/logo.png");
            if (logoFile.exists()) {
                ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
                Image logoImage = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                
                // Create a panel for logo with background
                JPanel logoPanel = new JPanel();
                logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
                logoPanel.setBackground(CARD_BG);
                logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel logoLabel = new JLabel(new ImageIcon(logoImage));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                logoLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230), 2, true),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
                
                logoPanel.add(logoLabel);
                card.add(logoPanel);
                card.add(Box.createRigidArea(new Dimension(0, 25)));
            }
        } catch (Exception ex) {
            // Logo not found, continue without it
        }
        
        // Title
        JLabel titleLabel = new JLabel("Healthcare Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel subtitleLabel = new JLabel("Create Your Clinic");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Form fields
        JTextField nameField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        
        // City dropdown
        String[] cities = {"Cairo", "Giza", "Alexandria"};
        JComboBox<String> cityCombo = new JComboBox<>(cities);
        cityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cityCombo.setPreferredSize(new Dimension(350, 40));
        cityCombo.setMaximumSize(new Dimension(350, 40));
        
        // State/Area dropdown (will be populated based on city)
        JComboBox<String> stateCombo = new JComboBox<>();
        stateCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        stateCombo.setPreferredSize(new Dimension(350, 40));
        stateCombo.setMaximumSize(new Dimension(350, 40));
        
        // Populate states based on selected city
        cityCombo.addActionListener(e -> {
            String selectedCity = (String) cityCombo.getSelectedItem();
            stateCombo.removeAllItems();
            if ("Cairo".equals(selectedCity)) {
                String[] cairoAreas = {
                    "Nasr City", "Heliopolis", "Maadi", "Zamalek", "Downtown Cairo",
                    "New Cairo", "Mokattam", "Helwan", "Shubra", "Ain Shams",
                    "Abbasia", "Garden City", "Dokki", "Mohandessin", "Agouza"
                };
                for (String area : cairoAreas) stateCombo.addItem(area);
            } else if ("Giza".equals(selectedCity)) {
                String[] gizaAreas = {
                    "6th of October City", "Sheikh Zayed", "Mohandessin", "Dokki", "Agouza",
                    "Haram", "Faisal", "Imbaba", "Kerdasa", "El Wahat",
                    "October Gardens", "Beverly Hills", "Palm Hills", "Pyramids", "Smart Village"
                };
                for (String area : gizaAreas) stateCombo.addItem(area);
            } else if ("Alexandria".equals(selectedCity)) {
                String[] alexAreas = {
                    "Smouha", "Sidi Gaber", "Sporting", "Cleopatra",
                    "Stanley", "San Stefano", "Gleem", "Roushdy", "Louran",
                    "Mandara", "Montaza", "Sidi Bishr", "Asafra", "Borg El Arab"
                };
                for (String area : alexAreas) stateCombo.addItem(area);
            }
        });
        
        // Initialize with Cairo areas
        cityCombo.setSelectedIndex(0);
        
        JPanel namePanel = createFormField("Clinic Name:", nameField);
        namePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(namePanel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel cityPanel = createFormField("City:", cityCombo);
        cityPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cityPanel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel statePanel = createFormField("Area:", stateCombo);
        statePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statePanel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JPanel phonePanel = createFormField("Phone (11 digits):", phoneField);
        phonePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(phonePanel);
        
        card.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Create button
        JButton createBtn = createStyledButton("Create Clinic", PRIMARY_COLOR);
        createBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String city = (String) cityCombo.getSelectedItem();
            String area = (String) stateCombo.getSelectedItem();
            String address = area + ", " + city + ", Egypt";
            String phone = phoneField.getText().trim();
            
            if (name.isEmpty() || phone.isEmpty()) {
                showError("Please fill in all fields");
                return;
            }
            
            if (!phone.matches("\\d{11}")) {
                showError("Phone must be exactly 11 digits");
                return;
            }
            
            clinic = new Clinic(name, address, phone);
            clinics.add(clinic);
            saveClinics();
            showSuccess("Clinic created successfully!\nID: " + clinic.getClinicId());
            showMainDashboard();
        });
        
        // Enable Enter key to submit
        addEnterKeyListeners(createBtn, nameField, phoneField);
        
        card.add(createBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        setupPanel.add(card, gbc);
        
        mainPanel.add(setupPanel, "SETUP");
        cardLayout.show(mainPanel, "SETUP");
    }
    
    private void showClinicSelectionMenu() {
        JPanel selectionPanel = new JPanel(new GridBagLayout());
        selectionPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Content panel to hold everything
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Top panel with logo and title
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);
        topPanel.setOpaque(false);
        
        // Logo with enhanced transparent background
        try {
            java.io.File logoFile = new java.io.File("Project12th/src/logo.png");
            if (logoFile.exists()) {
                ImageIcon logoIcon = new ImageIcon(logoFile.getAbsolutePath());
                Image logoImage = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                
                JPanel logoPanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        // Don't paint background - fully transparent
                    }
                };
                logoPanel.setOpaque(false);
                logoPanel.setBackground(new Color(0, 0, 0, 0));
                logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel logoLabel = new JLabel(new ImageIcon(logoImage)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        super.paintComponent(g2d);
                        g2d.dispose();
                    }
                };
                logoLabel.setOpaque(false);
                logoLabel.setBackground(new Color(0, 0, 0, 0));
                logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 25, 10));
                
                logoPanel.add(logoLabel);
                topPanel.add(logoPanel);
            }
        } catch (Exception ex) {
            // Logo not found, continue without it
        }
        
        // Title
        JLabel titleLabel = new JLabel("Select or Create a Clinic");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel with clinic list and buttons
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(BG_COLOR);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(BG_COLOR);
        
        JButton createNewBtn = createStyledButton("Create New Clinic", ACCENT_COLOR);
        createNewBtn.setPreferredSize(new Dimension(250, 50));
        createNewBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        createNewBtn.addActionListener(e -> showClinicSetup());
        buttonPanel.add(createNewBtn);
        
        if (!clinics.isEmpty()) {
            JButton searchBtn = createStyledButton("Search by ID", SECONDARY_COLOR);
            searchBtn.setPreferredSize(new Dimension(200, 50));
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            searchBtn.addActionListener(e -> showSearchClinicDialog());
            buttonPanel.add(searchBtn);
        }
        
        if (clinic != null) {
            JButton continueBtn = createStyledButton("Continue with Current", PRIMARY_COLOR);
            continueBtn.setPreferredSize(new Dimension(250, 50));
            continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            continueBtn.addActionListener(e -> showMainDashboard());
            buttonPanel.add(continueBtn);
        }
        
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Clinics table
        if (!clinics.isEmpty()) {
            String[] columns = {"ID", "Clinic Name", "Address", "Phone", "Doctors", "Patients", "Appointments"};
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (int i = 0; i < clinics.size(); i++) {
                Clinic c = clinics.get(i);
                tableModel.addRow(new Object[]{
                    c.getClinicId(),
                    c.getName(),
                    c.getAddress(),
                    c.getPhone(),
                    c.getDoctors().size(),
                    c.getPatients().size(),
                    c.getAppointments().size()
                });
            }
            
        }
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        selectionPanel.add(contentPanel, gbc);
        
        mainPanel.removeAll();
        mainPanel.add(selectionPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void showMainDashboard() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(BG_COLOR);
        
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(PRIMARY_COLOR);
        
        JLabel clinicNameLabel = new JLabel(clinic.getName() + " (ID: " + clinic.getClinicId() + ")");
        clinicNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        clinicNameLabel.setForeground(Color.WHITE);
        leftPanel.add(clinicNameLabel);
        
        // Live status indicator
        JLabel statusLabel = new JLabel("● ACTIVE");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(ACCENT_COLOR);
        leftPanel.add(statusLabel);
        
        // Animate status indicator with blinking effect
        Timer blinkTimer = new Timer(1000, new ActionListener() {
            boolean visible = true;
            @Override
            public void actionPerformed(ActionEvent e) {
                visible = !visible;
                statusLabel.setForeground(visible ? ACCENT_COLOR : PRIMARY_COLOR);
            }
        });
        blinkTimer.start();
        
        JButton switchBtn = createStyledButton("Switch Clinic", new Color(230, 126, 34));
        switchBtn.addActionListener(e -> showClinicSelectionMenu());
        leftPanel.add(switchBtn);

        JButton saveBtn = createStyledButton("Save Clinic", ACCENT_COLOR);
        saveBtn.addActionListener(e -> saveCurrentClinic());
        leftPanel.add(saveBtn);
        
        topBar.add(leftPanel, BorderLayout.WEST);
        
        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        backBtn.addActionListener(e -> showClinicSelectionMenu());
        topBar.add(backBtn, BorderLayout.EAST);
        
        dashboardPanel.add(topBar, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        
        // Dashboard cards
        gbc.gridx = 0; gbc.gridy = 0;
        centerPanel.add(createDashboardCard("Doctors", "Manage doctors", PRIMARY_COLOR, e -> showDoctorManagement()), gbc);
        
        gbc.gridx = 1;
        centerPanel.add(createDashboardCard("Patients", "Manage patients", SECONDARY_COLOR, e -> showPatientManagement()), gbc);
        
        gbc.gridx = 2;
        centerPanel.add(createDashboardCard("Appointments", "Schedule & manage", ACCENT_COLOR, e -> showAppointmentManagement()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        centerPanel.add(createDashboardCard("Medical Reports", "Doctor reports", new Color(231, 76, 60), e -> showMedicalReports()), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        centerPanel.add(createDashboardCard("Financial", "Payment processing", new Color(155, 89, 182), e -> {
            System.out.println("DEBUG: Financial card clicked!");
            showFinancialManagement();
        }), gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        centerPanel.add(createDashboardCard("Health Reports", "Patient vital signs", new Color(142, 68, 173), e -> showHealthReports()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        centerPanel.add(createDashboardCard("Statistics", "Clinic overview", new Color(26, 188, 156), e -> showStatistics()), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        centerPanel.add(createDashboardCard("Reports", "View statistics", new Color(230, 126, 34), e -> showReports()), gbc);
        
        dashboardPanel.add(centerPanel, BorderLayout.CENTER);
        
        mainPanel.add(dashboardPanel, "DASHBOARD");
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    private JPanel createDashboardCard(String title, String description, Color color, ActionListener action) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(40, 30, 40, 30)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(250, 180));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(Color.GRAY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(descLabel);
        card.add(Box.createVerticalGlue());
        
        // Combined mouse listener for both hover effect and click action
        card.addMouseListener(new MouseAdapter() {
            private Timer scaleTimer;
            private float scale = 1.0f;
            
            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, null));
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 249, 249));
                if (scaleTimer != null) scaleTimer.stop();
                scaleTimer = new Timer(10, evt -> {
                    scale += 0.02f;
                    if (scale >= 1.05f) {
                        scale = 1.05f;
                        scaleTimer.stop();
                    }
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 2),
                        BorderFactory.createEmptyBorder(40, 30, 40, 30)
                    ));
                    card.revalidate();
                });
                scaleTimer.start();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
                if (scaleTimer != null) scaleTimer.stop();
                scaleTimer = new Timer(10, evt -> {
                    scale -= 0.02f;
                    if (scale <= 1.0f) {
                        scale = 1.0f;
                        scaleTimer.stop();
                    }
                    card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(40, 30, 40, 30)
                    ));
                    card.revalidate();
                });
                scaleTimer.start();
            }
        });
        
        return card;
    }
    
    private void showDoctorManagement() {
        JPanel panel = createManagementPanel("Doctor Management");
        JPanel contentPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        
        // Add live search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(CARD_BG);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        
        JTextField liveSearchField = new JTextField(30);
        liveSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        liveSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add placeholder and interactive effects
        liveSearchField.setForeground(Color.GRAY);
        liveSearchField.setText("Type to search...");
        liveSearchField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (liveSearchField.getText().equals("Type to search...")) {
                    liveSearchField.setText("");
                    liveSearchField.setForeground(Color.BLACK);
                }
                liveSearchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(FocusEvent e) {
                if (liveSearchField.getText().isEmpty()) {
                    liveSearchField.setForeground(Color.GRAY);
                    liveSearchField.setText("Type to search...");
                }
                liveSearchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        searchPanel.add(liveSearchField);
        
        // Sort panel for doctors
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortPanel.setBackground(CARD_BG);
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sortPanel.add(sortLabel);
        
        String[] doctorSortOptions = {"Name", "Specialization", "Experience", "License", "ID"};
        JComboBox<String> doctorSortCombo = new JComboBox<>(doctorSortOptions);
        doctorSortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        doctorSortCombo.setPreferredSize(new Dimension(150, 35));
        sortPanel.add(doctorSortCombo);
        
        JCheckBox doctorAscendingCheckbox = new JCheckBox("Ascending", true);
        doctorAscendingCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorAscendingCheckbox.setBackground(CARD_BG);
        sortPanel.add(doctorAscendingCheckbox);
        
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(sortPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(CARD_BG);
        
        JButton addBtn = createStyledButton("Add Doctor", ACCENT_COLOR);
        addBtn.setToolTipText("Register a new doctor to the clinic");
        JButton viewBtn = createStyledButton("View All", PRIMARY_COLOR);
        viewBtn.setToolTipText("Show all registered doctors");
        JButton searchBtn = createStyledButton("Search", SECONDARY_COLOR);
        searchBtn.setToolTipText("Find a specific doctor by ID");
        JButton editBtn = createStyledButton("Edit", new Color(230, 126, 34));
        editBtn.setToolTipText("Edit doctor information");
        JButton manageAvailabilityBtn = createStyledButton("Manage Availability", new Color(155, 89, 182));
        manageAvailabilityBtn.setToolTipText("Manage doctor's available days");
        JButton reportBtn = createStyledButton("Report", new Color(26, 188, 156));
        reportBtn.setToolTipText("View doctor report");
        JButton removeBtn = createStyledButton("Remove", DANGER_COLOR);
        removeBtn.setToolTipText("Remove a doctor from the system");
        
        addBtn.addActionListener(e -> showAddDoctorDialog());
        viewBtn.addActionListener(e -> showAllDoctors());
        searchBtn.addActionListener(e -> showSearchDoctorDialog());
        editBtn.addActionListener(e -> showEditDoctorDialog());
        manageAvailabilityBtn.addActionListener(e -> showManageDoctorAvailabilityDialog());
        reportBtn.addActionListener(e -> showDoctorReportDialog());
        removeBtn.addActionListener(e -> showRemoveDoctorDialog());
        
        buttonsPanel.add(addBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(searchBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(manageAvailabilityBtn);
        buttonsPanel.add(reportBtn);
        buttonsPanel.add(removeBtn);
        
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Display doctors table
        String[] columns = {"ID", "Name", "Specialization", "License", "Experience (Years)", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Doctor doctor : clinic.getDoctors()) {
            model.addRow(new Object[]{
                doctor.getId(),
                doctor.getName(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getYearsOfExperience(),
                doctor.getPhone()
            });
        }
        
        JTable table = createStyledTable(model);
        
        // Add real-time search filtering and sorting
        liveSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            
            private void filterAndSortTable() {
                String searchText = liveSearchField.getText().toLowerCase();
                ArrayList<Doctor> filteredDoctors = new ArrayList<>();
                
                for (Doctor doctor : clinic.getDoctors()) {
                    String doctorInfo = (doctor.getId() + doctor.getName() + doctor.getSpecialization() + 
                                        doctor.getLicenseNumber() + doctor.getPhone()).toLowerCase();
                    if (doctorInfo.contains(searchText)) {
                        filteredDoctors.add(doctor);
                    }
                }
                
                // Sort doctors
                String sortOption = (String) doctorSortCombo.getSelectedItem();
                boolean ascending = doctorAscendingCheckbox.isSelected();
                
                filteredDoctors.sort((d1, d2) -> {
                    int result = 0;
                    switch(sortOption) {
                        case "Name":
                            result = d1.getName().compareTo(d2.getName());
                            break;
                        case "Specialization":
                            result = d1.getSpecialization().compareTo(d2.getSpecialization());
                            break;
                        case "Experience":
                            result = Integer.compare(d1.getYearsOfExperience(), d2.getYearsOfExperience());
                            break;
                        case "License":
                            result = d1.getLicenseNumber().compareTo(d2.getLicenseNumber());
                            break;
                        case "ID":
                            result = d1.getId().compareTo(d2.getId());
                            break;
                    }
                    return ascending ? result : -result;
                });
                
                // Update table
                model.setRowCount(0);
                for (Doctor doctor : filteredDoctors) {
                    model.addRow(new Object[]{
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecialization(),
                        doctor.getLicenseNumber(),
                        doctor.getYearsOfExperience(),
                        doctor.getPhone()
                    });
                }
            }
        });
        
        // Add sorting listener
        doctorSortCombo.addActionListener(e -> {
            String searchText = liveSearchField.getText().toLowerCase();
            ArrayList<Doctor> filteredDoctors = new ArrayList<>();
            
            for (Doctor doctor : clinic.getDoctors()) {
                String doctorInfo = (doctor.getId() + doctor.getName() + doctor.getSpecialization() + 
                                    doctor.getLicenseNumber() + doctor.getPhone()).toLowerCase();
                if (doctorInfo.contains(searchText)) {
                    filteredDoctors.add(doctor);
                }
            }
            
            // Sort doctors
            String sortOption = (String) doctorSortCombo.getSelectedItem();
            boolean ascending = doctorAscendingCheckbox.isSelected();
            
            filteredDoctors.sort((d1, d2) -> {
                int result = 0;
                switch(sortOption) {
                    case "Name":
                        result = d1.getName().compareTo(d2.getName());
                        break;
                    case "Specialization":
                        result = d1.getSpecialization().compareTo(d2.getSpecialization());
                        break;
                    case "Experience":
                        result = Integer.compare(d1.getYearsOfExperience(), d2.getYearsOfExperience());
                        break;
                    case "License":
                        result = d1.getLicenseNumber().compareTo(d2.getLicenseNumber());
                        break;
                    case "ID":
                        result = d1.getId().compareTo(d2.getId());
                        break;
                }
                return ascending ? result : -result;
            });
            
            // Update table
            model.setRowCount(0);
            for (Doctor doctor : filteredDoctors) {
                model.addRow(new Object[]{
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getLicenseNumber(),
                    doctor.getYearsOfExperience(),
                    doctor.getPhone()
                });
            }
        });
        
        doctorAscendingCheckbox.addActionListener(e -> {
            String searchText = liveSearchField.getText().toLowerCase();
            ArrayList<Doctor> filteredDoctors = new ArrayList<>();
            
            for (Doctor doctor : clinic.getDoctors()) {
                String doctorInfo = (doctor.getId() + doctor.getName() + doctor.getSpecialization() + 
                                    doctor.getLicenseNumber() + doctor.getPhone()).toLowerCase();
                if (doctorInfo.contains(searchText)) {
                    filteredDoctors.add(doctor);
                }
            }
            
            // Sort doctors
            String sortOption = (String) doctorSortCombo.getSelectedItem();
            boolean ascending = doctorAscendingCheckbox.isSelected();
            
            filteredDoctors.sort((d1, d2) -> {
                int result = 0;
                switch(sortOption) {
                    case "Name":
                        result = d1.getName().compareTo(d2.getName());
                        break;
                    case "Specialization":
                        result = d1.getSpecialization().compareTo(d2.getSpecialization());
                        break;
                    case "Experience":
                        result = Integer.compare(d1.getYearsOfExperience(), d2.getYearsOfExperience());
                        break;
                    case "License":
                        result = d1.getLicenseNumber().compareTo(d2.getLicenseNumber());
                        break;
                    case "ID":
                        result = d1.getId().compareTo(d2.getId());
                        break;
                }
                return ascending ? result : -result;
            });
            
            // Update table
            model.setRowCount(0);
            for (Doctor doctor : filteredDoctors) {
                model.addRow(new Object[]{
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getSpecialization(),
                    doctor.getLicenseNumber(),
                    doctor.getYearsOfExperience(),
                    doctor.getPhone()
                });
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 400));
        contentPanel.add(scrollPane);
        
        mainPanel.add(panel, "DOCTORS");
        cardLayout.show(mainPanel, "DOCTORS");
    }
    
    private void showPatientManagement() {
        JPanel panel = createManagementPanel("Patient Management");
        JPanel contentPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        
        // Add live search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(CARD_BG);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchPanel.add(searchLabel);
        
        JTextField liveSearchField = new JTextField(30);
        liveSearchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        liveSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add placeholder and interactive effects
        liveSearchField.setForeground(Color.GRAY);
        liveSearchField.setText("Type to search...");
        liveSearchField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (liveSearchField.getText().equals("Type to search...")) {
                    liveSearchField.setText("");
                    liveSearchField.setForeground(Color.BLACK);
                }
                liveSearchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            public void focusLost(FocusEvent e) {
                if (liveSearchField.getText().isEmpty()) {
                    liveSearchField.setForeground(Color.GRAY);
                    liveSearchField.setText("Type to search...");
                }
                liveSearchField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
        
        searchPanel.add(liveSearchField);
        
        // Sort panel for patients
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sortPanel.setBackground(CARD_BG);
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sortPanel.add(sortLabel);
        
        String[] patientSortOptions = {"Name", "Age", "Gender", "Blood Type", "ID"};
        JComboBox<String> patientSortCombo = new JComboBox<>(patientSortOptions);
        patientSortCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientSortCombo.setPreferredSize(new Dimension(150, 35));
        sortPanel.add(patientSortCombo);
        
        JCheckBox patientAscendingCheckbox = new JCheckBox("Ascending", true);
        patientAscendingCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientAscendingCheckbox.setBackground(CARD_BG);
        sortPanel.add(patientAscendingCheckbox);
        
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(sortPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(CARD_BG);
        
        JButton addBtn = createStyledButton("Register Patient", ACCENT_COLOR);
        addBtn.setToolTipText("Register a new patient");
        JButton viewBtn = createStyledButton("View All", PRIMARY_COLOR);
        viewBtn.setToolTipText("View all registered patients");
        JButton searchBtn = createStyledButton("Search", SECONDARY_COLOR);
        searchBtn.setToolTipText("Find a patient by ID");
        JButton editBtn = createStyledButton("Edit", new Color(230, 126, 34));
        editBtn.setToolTipText("Edit patient information");
        JButton recordsBtn = createStyledButton("Manage Records", new Color(155, 89, 182));
        recordsBtn.setToolTipText("Manage patient medical records");
        JButton detailsBtn = createStyledButton("View Details", new Color(26, 188, 156));
        detailsBtn.setToolTipText("View patient details and contact info");
        JButton removeBtn = createStyledButton("Remove", DANGER_COLOR);
        removeBtn.setToolTipText("Remove a patient from the system");
        
        addBtn.addActionListener(e -> showAddPatientDialog());
        viewBtn.addActionListener(e -> showAllPatients());
        searchBtn.addActionListener(e -> showSearchPatientDialog());
        editBtn.addActionListener(e -> showEditPatientDialog());
        recordsBtn.addActionListener(e -> showManagePatientRecordsDialog());
        detailsBtn.addActionListener(e -> showPatientDetailsDialog());
        removeBtn.addActionListener(e -> showRemovePatientDialog());
        
        buttonsPanel.add(addBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(searchBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(recordsBtn);
        buttonsPanel.add(detailsBtn);
        buttonsPanel.add(removeBtn);
        
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Display patients table
        String[] columns = {"ID", "Name", "Age", "Gender", "Blood Type", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Patient patient : clinic.getPatients()) {
            model.addRow(new Object[]{
                patient.getId(),
                patient.getName(),
                patient.getAge(),
                patient.getGender(),
                patient.getBloodType(),
                patient.getPhone()
            });
        }
        
        JTable table = createStyledTable(model);
        
        // Add real-time search filtering and sorting
        liveSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterAndSortTable(); }
            
            private void filterAndSortTable() {
                String searchText = liveSearchField.getText().toLowerCase();
                ArrayList<Patient> filteredPatients = new ArrayList<>();
                
                for (Patient patient : clinic.getPatients()) {
                    String patientInfo = (patient.getId() + patient.getName() + patient.getGender() + 
                                         patient.getBloodType() + patient.getPhone()).toLowerCase();
                    if (patientInfo.contains(searchText)) {
                        filteredPatients.add(patient);
                    }
                }
                
                // Sort patients
                String sortOption = (String) patientSortCombo.getSelectedItem();
                boolean ascending = patientAscendingCheckbox.isSelected();
                
                filteredPatients.sort((p1, p2) -> {
                    int result = 0;
                    switch(sortOption) {
                        case "Name":
                            result = p1.getName().compareTo(p2.getName());
                            break;
                        case "Age":
                            result = Integer.compare(p1.getAge(), p2.getAge());
                            break;
                        case "Gender":
                            result = p1.getGender().compareTo(p2.getGender());
                            break;
                        case "Blood Type":
                            result = p1.getBloodType().compareTo(p2.getBloodType());
                            break;
                        case "ID":
                            result = p1.getId().compareTo(p2.getId());
                            break;
                    }
                    return ascending ? result : -result;
                });
                
                // Update table
                model.setRowCount(0);
                for (Patient patient : filteredPatients) {
                    model.addRow(new Object[]{
                        patient.getId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getGender(),
                        patient.getBloodType(),
                        patient.getPhone()
                    });
                }
            }
        });
        
        // Add sorting listener
        patientSortCombo.addActionListener(e -> {
            String searchText = liveSearchField.getText().toLowerCase();
            ArrayList<Patient> filteredPatients = new ArrayList<>();
            
            for (Patient patient : clinic.getPatients()) {
                String patientInfo = (patient.getId() + patient.getName() + patient.getGender() + 
                                     patient.getBloodType() + patient.getPhone()).toLowerCase();
                if (patientInfo.contains(searchText)) {
                    filteredPatients.add(patient);
                }
            }
            
            // Sort patients
            String sortOption = (String) patientSortCombo.getSelectedItem();
            boolean ascending = patientAscendingCheckbox.isSelected();
            
            filteredPatients.sort((p1, p2) -> {
                int result = 0;
                switch(sortOption) {
                    case "Name":
                        result = p1.getName().compareTo(p2.getName());
                        break;
                    case "Age":
                        result = Integer.compare(p1.getAge(), p2.getAge());
                        break;
                    case "Gender":
                        result = p1.getGender().compareTo(p2.getGender());
                        break;
                    case "Blood Type":
                        result = p1.getBloodType().compareTo(p2.getBloodType());
                        break;
                    case "ID":
                        result = p1.getId().compareTo(p2.getId());
                        break;
                }
                return ascending ? result : -result;
            });
            
            // Update table
            model.setRowCount(0);
            for (Patient patient : filteredPatients) {
                model.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getBloodType(),
                    patient.getPhone()
                });
            }
        });
        
        patientAscendingCheckbox.addActionListener(e -> {
            String searchText = liveSearchField.getText().toLowerCase();
            ArrayList<Patient> filteredPatients = new ArrayList<>();
            
            for (Patient patient : clinic.getPatients()) {
                String patientInfo = (patient.getId() + patient.getName() + patient.getGender() + 
                                     patient.getBloodType() + patient.getPhone()).toLowerCase();
                if (patientInfo.contains(searchText)) {
                    filteredPatients.add(patient);
                }
            }
            
            // Sort patients
            String sortOption = (String) patientSortCombo.getSelectedItem();
            boolean ascending = patientAscendingCheckbox.isSelected();
            
            filteredPatients.sort((p1, p2) -> {
                int result = 0;
                switch(sortOption) {
                    case "Name":
                        result = p1.getName().compareTo(p2.getName());
                        break;
                    case "Age":
                        result = Integer.compare(p1.getAge(), p2.getAge());
                        break;
                    case "Gender":
                        result = p1.getGender().compareTo(p2.getGender());
                        break;
                    case "Blood Type":
                        result = p1.getBloodType().compareTo(p2.getBloodType());
                        break;
                    case "ID":
                        result = p1.getId().compareTo(p2.getId());
                        break;
                }
                return ascending ? result : -result;
            });
            
            // Update table
            model.setRowCount(0);
            for (Patient patient : filteredPatients) {
                model.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getBloodType(),
                    patient.getPhone()
                });
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 400));
        contentPanel.add(scrollPane);
        
        mainPanel.add(panel, "PATIENTS");
        cardLayout.show(mainPanel, "PATIENTS");
    }
    
    private void showAppointmentManagement() {
        JPanel panel = createManagementPanel("Appointment Management");
        JPanel contentPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsPanel.setBackground(CARD_BG);
        
        JButton scheduleBtn = createStyledButton("Schedule Appointment", ACCENT_COLOR);
        scheduleBtn.setToolTipText("Book a new appointment with a doctor");
        JButton viewBtn = createStyledButton("View All", PRIMARY_COLOR);
        viewBtn.setToolTipText("View all scheduled appointments");
        JButton statusBtn = createStyledButton("Change Status", new Color(230, 126, 34));
        statusBtn.setToolTipText("Update appointment status");
            JButton rescheduleBtn = createStyledButton("Reschedule", new Color(155, 89, 182));
            rescheduleBtn.setToolTipText("Reschedule an appointment");
            JButton completeBtn = createStyledButton("Complete", new Color(26, 188, 156));
            completeBtn.setToolTipText("Mark appointment as completed");
            JButton detailsBtn = createStyledButton("Details", SECONDARY_COLOR);
            detailsBtn.setToolTipText("View appointment details");
            JButton cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
            cancelBtn.setToolTipText("Cancel an appointment");
        
            scheduleBtn.addActionListener(e -> showScheduleAppointmentDialog());
            viewBtn.addActionListener(e -> showAllAppointments());
            statusBtn.addActionListener(e -> showChangeStatusDialog());
            rescheduleBtn.addActionListener(e -> showRescheduleAppointmentDialog());
            completeBtn.addActionListener(e -> showCompleteAppointmentDialog());
            detailsBtn.addActionListener(e -> showAppointmentDetailsDialog());
            cancelBtn.addActionListener(e -> showCancelAppointmentDialog());
        
            buttonsPanel.add(scheduleBtn);
            buttonsPanel.add(viewBtn);
            buttonsPanel.add(statusBtn);
            buttonsPanel.add(rescheduleBtn);
            buttonsPanel.add(completeBtn);
            buttonsPanel.add(detailsBtn);
            buttonsPanel.add(cancelBtn);
        
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Display appointments table
        String[] columns = {"ID", "Patient", "Doctor", "Date & Time", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        for (Appointment apt : clinic.getAppointments()) {
            model.addRow(new Object[]{
                apt.getAppointmentId(),
                apt.getPatient().getName(),
                "Dr. " + apt.getDoctor().getName(),
                sdf.format(apt.getDate()),
                apt.getStatus(),
                apt.getReason()
            });
        }
        
        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 400));
        contentPanel.add(scrollPane);
        
        mainPanel.add(panel, "APPOINTMENTS");
        cardLayout.show(mainPanel, "APPOINTMENTS");
    }
    
    private void showFinancialManagement() {
        System.out.println("DEBUG: showFinancialManagement() called");
        try {
            JPanel panel = createManagementPanel("Financial Management");
            JPanel contentPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
            
            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonsPanel.setBackground(CARD_BG);
        
        JButton processBtn = createStyledButton("Process Payment", ACCENT_COLOR);
        JButton viewBtn = createStyledButton("View Summary", PRIMARY_COLOR);
        JButton historyBtn = createStyledButton("Payment History", SECONDARY_COLOR);
        JButton editBtn = createStyledButton("Edit Payment", new Color(230, 126, 34));
        JButton addItemBtn = createStyledButton("Add Item", new Color(155, 89, 182));
        JButton quickBillBtn = createStyledButton("Quick Bill", new Color(26, 188, 156));
        JButton reportBtn = createStyledButton("Report", new Color(46, 204, 113));
        
        processBtn.addActionListener(e -> showProcessPaymentDialog());
        viewBtn.addActionListener(e -> showFinancialManagement());
        historyBtn.addActionListener(e -> showPaymentHistoryDialog());
        editBtn.addActionListener(e -> showEditPaymentDialog());
        addItemBtn.addActionListener(e -> showAddInvoiceItemDialog());
        quickBillBtn.addActionListener(e -> showQuickBillDialog());
        reportBtn.addActionListener(e -> showFinancialReportDialog());
        
        buttonsPanel.add(processBtn);
        buttonsPanel.add(viewBtn);
        buttonsPanel.add(historyBtn);
        buttonsPanel.add(editBtn);
        buttonsPanel.add(addItemBtn);
        buttonsPanel.add(quickBillBtn);
        buttonsPanel.add(reportBtn);
        
        contentPanel.add(buttonsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Financial Summary
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(CARD_BG);
        
        double totalRevenue = 0;
        int totalTransactions = clinic.getFinancialServices().size();
        int completedPayments = 0;
        
        for (FinancialService service : clinic.getFinancialServices()) {
            totalRevenue += service.getTotalAmount();
            if (service.getStatus().equals("Paid") || service.getStatus().equals("Completed")) {
                completedPayments++;
            }
        }
        
        summaryPanel.add(createStatCard("Total Revenue", "$" + String.format("%.2f", totalRevenue), ACCENT_COLOR));
        summaryPanel.add(createStatCard("Total Payments", String.valueOf(totalTransactions), PRIMARY_COLOR));
        summaryPanel.add(createStatCard("Completed", String.valueOf(completedPayments), new Color(46, 204, 113)));
        
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Financial services table
        String[] columns = {"Invoice ID", "Patient", "Amount", "Total", "Method", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        for (FinancialService service : clinic.getFinancialServices()) {
            model.addRow(new Object[]{
                service.getInvoiceId(),
                service.getPatient().getName(),
                "$" + String.format("%.2f", service.getAmount()),
                "$" + String.format("%.2f", service.getTotalAmount()),
                service.getPaymentMethod(),
                sdf.format(service.getInvoiceDate()),
                service.getStatus()
            });
        }
        
        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1100, 300));
        contentPanel.add(scrollPane);
        
        mainPanel.add(panel, "FINANCIAL");
        cardLayout.show(mainPanel, "FINANCIAL");
        System.out.println("DEBUG: Financial panel displayed successfully");
        } catch (Exception ex) {
            System.err.println("ERROR in showFinancialManagement: " + ex.getMessage());
            ex.printStackTrace();
            showError("Error loading Financial Management: " + ex.getMessage());
        }
    }

    private void showPaymentHistoryDialog() {
        JDialog dialog = new JDialog(this, "Payment History", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        // Filters row
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filters.setBackground(CARD_BG);

        JLabel patientLbl = new JLabel("Patient:");
        patientLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filters.add(patientLbl);

        JComboBox<String> patientCombo = new JComboBox<>();
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        patientCombo.setPreferredSize(new Dimension(220, 32));
        patientCombo.addItem("All Patients");
        for (Patient p : clinic.getPatients()) {
            patientCombo.addItem(p.getName() + " (" + p.getId() + ")");
        }
        filters.add(patientCombo);

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        filters.add(statusLbl);

        String[] statuses = new String[] {"All", "Paid", "Partial Payment", "Payment Rejected - Invalid Payment Method", "Completed"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(260, 32));
        filters.add(statusCombo);

        panel.add(filters);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Table
        String[] columns = {"Invoice ID", "Patient", "Amount", "Total", "Method", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = createStyledTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(740, 380));
        panel.add(scroll);

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(CARD_BG);
        JButton detailsBtn = createStyledButton("View Details", PRIMARY_COLOR);
        JButton closeBtn = createStyledButton("Close", DANGER_COLOR);
        btns.add(detailsBtn);
        btns.add(closeBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(btns);

        Runnable refreshTable = () -> {
            String patientSel = (String) patientCombo.getSelectedItem();
            String statusSel = (String) statusCombo.getSelectedItem();
            model.setRowCount(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            for (FinancialService s : clinic.getFinancialServices()) {
                boolean patientMatch = true;
                if (patientSel != null && !patientSel.equals("All Patients")) {
                    String idInSel = patientSel.substring(patientSel.lastIndexOf('(') + 1, patientSel.lastIndexOf(')'));
                    patientMatch = s.getPatient() != null && idInSel.equals(s.getPatient().getId());
                }
                boolean statusMatch = statusSel == null || statusSel.equals("All") || statusSel.equals(s.getStatus());
                if (patientMatch && statusMatch) {
                    model.addRow(new Object[]{
                        s.getInvoiceId(),
                        s.getPatient() != null ? s.getPatient().getName() : "-",
                        "$" + String.format("%.2f", s.getAmount()),
                        "$" + String.format("%.2f", s.getTotalAmount()),
                        s.getPaymentMethod(),
                        sdf.format(s.getInvoiceDate()),
                        s.getStatus()
                    });
                }
            }
        };

        patientCombo.addActionListener(e -> refreshTable.run());
        statusCombo.addActionListener(e -> refreshTable.run());

        // Initial load
        refreshTable.run();

        detailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showError("Please select a record.");
                return;
            }
            String invoiceId = (String) model.getValueAt(row, 0);
            FinancialService found = null;
            for (FinancialService s : clinic.getFinancialServices()) {
                if (s.getInvoiceId().equals(invoiceId)) { found = s; break; }
            }
            if (found == null) { showError("Record not found."); return; }

            JDialog d2 = new JDialog(dialog, "Payment Details", true);
            d2.setSize(600, 500);
            d2.setLocationRelativeTo(dialog);
            JPanel p2 = new JPanel();
            p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
            p2.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            p2.setBackground(CARD_BG);

            JTextArea ta = new JTextArea();
            ta.setEditable(false);
            ta.setFont(new Font("Courier New", Font.PLAIN, 12));
            ta.setText(found.generateInvoice() + "\n\n" + found.trackPayment());
            JScrollPane sp = new JScrollPane(ta);
            sp.setPreferredSize(new Dimension(560, 380));
            p2.add(sp);

            JPanel pBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            pBtns.setBackground(CARD_BG);
            JButton close = createStyledButton("Close", SECONDARY_COLOR);
            close.addActionListener(ev -> d2.dispose());
            pBtns.add(close);
            p2.add(Box.createRigidArea(new Dimension(0, 10)));
            p2.add(pBtns);

            d2.add(p2);
            d2.setVisible(true);
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditPaymentDialog() {
        if (clinic.getFinancialServices().isEmpty()) {
            showInfo("No payments to edit.");
            return;
        }
        JDialog dialog = new JDialog(this, "Edit Payment", true);
        dialog.setSize(520, 460);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        JComboBox<String> invoiceCombo = new JComboBox<>();
        for (FinancialService s : clinic.getFinancialServices()) {
            String patientName = (s.getPatient() != null ? s.getPatient().getName() : "-");
            invoiceCombo.addItem(s.getInvoiceId() + " - " + patientName);
        }
        JTextField amountField = createStyledTextField();
        JTextField totalField = createStyledTextField();
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"CASH", "VISA"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Pending", "Paid", "Partial Payment", "Completed", "Payment Rejected - Invalid Payment Method"
        });

        panel.add(createFormField("Invoice:", invoiceCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Amount:", amountField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Total Amount:", totalField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Method:", methodCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Status:", statusCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton loadBtn = createStyledButton("Load Current", SECONDARY_COLOR);
        JButton saveBtn = createStyledButton("Save Changes", ACCENT_COLOR);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btns.setBackground(CARD_BG);
        btns.add(loadBtn);
        btns.add(saveBtn);
        panel.add(btns);

        loadBtn.addActionListener(e -> {
            int idx = invoiceCombo.getSelectedIndex();
            if (idx < 0) return;
            FinancialService s = clinic.getFinancialServices().get(idx);
            amountField.setText(String.valueOf(s.getAmount()));
            totalField.setText(String.valueOf(s.getTotalAmount()));
            methodCombo.setSelectedItem(s.getPaymentMethod());
            statusCombo.setSelectedItem(s.getStatus());
        });

        saveBtn.addActionListener(e -> {
            try {
                int idx = invoiceCombo.getSelectedIndex();
                if (idx < 0) { showError("Select an invoice."); return; }
                FinancialService s = clinic.getFinancialServices().get(idx);
                double amount = Double.parseDouble(amountField.getText().trim());
                double total = Double.parseDouble(totalField.getText().trim());
                String method = (String) methodCombo.getSelectedItem();
                String status = (String) statusCombo.getSelectedItem();

                s.setAmount(amount);
                s.setTotalAmount(total);
                s.setPaymentMethod(method);
                s.setStatus(status);
                autoSave();
                showSuccess("Payment updated for invoice: " + s.getInvoiceId());
                dialog.dispose();
                showFinancialManagement();
            } catch (NumberFormatException ex) {
                showError("Invalid number format.");
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddInvoiceItemDialog() {
        if (clinic.getFinancialServices().isEmpty()) {
            showInfo("No invoices available.");
            return;
        }
        JDialog dialog = new JDialog(this, "Add Invoice Item", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        JComboBox<String> invoiceCombo = new JComboBox<>();
        for (FinancialService s : clinic.getFinancialServices()) {
            String patientName = (s.getPatient() != null ? s.getPatient().getName() : "-");
            invoiceCombo.addItem(s.getInvoiceId() + " - " + patientName);
        }
        JTextField itemField = createStyledTextField();

        panel.add(createFormField("Invoice:", invoiceCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Item Description:", itemField));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton addBtn = createStyledButton("Add Item", ACCENT_COLOR);
        addBtn.addActionListener(e -> {
            int idx = invoiceCombo.getSelectedIndex();
            if (idx < 0) { showError("Select an invoice."); return; }
            String item = itemField.getText().trim();
            if (item.isEmpty()) { showError("Enter an item description."); return; }
            FinancialService s = clinic.getFinancialServices().get(idx);
            s.addItem(item);
            autoSave();
            showSuccess("Item added to invoice: " + s.getInvoiceId());
            dialog.dispose();
            showFinancialManagement();
        });
        addEnterKeyListener(itemField, addBtn);
        panel.add(addBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showQuickBillDialog() {
        if (clinic.getFinancialServices().isEmpty()) {
            showInfo("No invoices available.");
            return;
        }
        JDialog dialog = new JDialog(this, "Quick Bill Calculator", true);
        dialog.setSize(520, 340);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        JComboBox<String> invoiceCombo = new JComboBox<>();
        for (FinancialService s : clinic.getFinancialServices()) {
            String patientName = (s.getPatient() != null ? s.getPatient().getName() : "-");
            invoiceCombo.addItem(s.getInvoiceId() + " - " + patientName);
        }
        JTextField extrasField = createStyledTextField();
        JLabel resultLabel = new JLabel("Total = ");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(createFormField("Invoice:", invoiceCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Extra Amounts (comma-separated):", extrasField));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton calcBtn = createStyledButton("Calculate", PRIMARY_COLOR);
        calcBtn.addActionListener(e -> {
            int idx = invoiceCombo.getSelectedIndex();
            if (idx < 0) { showError("Select an invoice."); return; }
            FinancialService s = clinic.getFinancialServices().get(idx);
            String text = extrasField.getText().trim();
            if (text.isEmpty()) {
                double total = s.calculateBill();
                resultLabel.setText("Total = $" + String.format("%.2f", total));
                return;
            }
            try {
                String[] parts = text.split(",");
                java.util.List<Double> list = new java.util.ArrayList<>();
                for (String p : parts) {
                    String v = p.trim();
                    if (!v.isEmpty()) list.add(Double.parseDouble(v));
                }
                double[] arr = new double[list.size()];
                for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
                double total = s.calcTotal(arr);
                resultLabel.setText("Total = $" + String.format("%.2f", total));
            } catch (NumberFormatException ex) {
                showError("Invalid extras format.");
            }
        });
        panel.add(calcBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showFinancialReportDialog() {
        if (clinic.getFinancialServices().isEmpty()) {
            showInfo("No invoices available.");
            return;
        }
        JDialog dialog = new JDialog(this, "Financial Report", true);
        dialog.setSize(620, 520);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);

        JComboBox<String> invoiceCombo = new JComboBox<>();
        for (FinancialService s : clinic.getFinancialServices()) {
            String patientName = (s.getPatient() != null ? s.getPatient().getName() : "-");
            invoiceCombo.addItem(s.getInvoiceId() + " - " + patientName);
        }
        panel.add(createFormField("Invoice:", invoiceCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JTextArea area = new JTextArea(18, 40);
        area.setEditable(false);
        area.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(area);
        panel.add(sp);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JButton generateBtn = createStyledButton("Generate", PRIMARY_COLOR);
        JButton closeBtn = createStyledButton("Close", SECONDARY_COLOR);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(CARD_BG);
        btns.add(generateBtn);
        btns.add(closeBtn);
        panel.add(btns);

        generateBtn.addActionListener(e -> {
            int idx = invoiceCombo.getSelectedIndex();
            if (idx < 0) { showError("Select an invoice."); return; }
            FinancialService s = clinic.getFinancialServices().get(idx);
            area.setText(s.generateFinancialReport());
        });
        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showReports() {
        JPanel reportPanel = new JPanel(new BorderLayout(20, 20));
        reportPanel.setBackground(BG_COLOR);
        reportPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Top bar with back button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton backBtn = createStyledButton("← Back to Dashboard", DANGER_COLOR);
        backBtn.addActionListener(e -> showMainDashboard());
        topBar.add(backBtn, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Clinic Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);
        
        reportPanel.add(topBar, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_COLOR);
        
        // Clinic Info Card
        JPanel infoCard = createReportCard("Clinic Information", PRIMARY_COLOR);
        addReportRow(infoCard, "Clinic ID:", clinic.getClinicId());
        addReportRow(infoCard, "Name:", clinic.getName());
        addReportRow(infoCard, "Address:", clinic.getAddress());
        addReportRow(infoCard, "Phone:", clinic.getPhone());
        contentPanel.add(infoCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Staff & Patients Card
        JPanel staffCard = createReportCard("Staff & Patients", SECONDARY_COLOR);
        addReportRow(staffCard, "Total Doctors:", String.valueOf(clinic.getDoctors().size()));
        addReportRow(staffCard, "Total Patients:", String.valueOf(clinic.getPatients().size()));
        contentPanel.add(staffCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Appointments Card
        int scheduled = 0, completed = 0, cancelled = 0;
        for (Appointment apt : clinic.getAppointments()) {
            String status = apt.getStatus().toUpperCase();
            if (status.equals("SCHEDULED")) scheduled++;
            else if (status.equals("COMPLETED")) completed++;
            else if (status.equals("CANCELLED")) cancelled++;
        }
        
        JPanel appointmentsCard = createReportCard("Appointments", ACCENT_COLOR);
        addReportRow(appointmentsCard, "Total Appointments:", String.valueOf(clinic.getAppointments().size()));
        addReportRow(appointmentsCard, "Scheduled:", String.valueOf(scheduled));
        addReportRow(appointmentsCard, "Completed:", String.valueOf(completed));
        addReportRow(appointmentsCard, "Cancelled:", String.valueOf(cancelled));
        contentPanel.add(appointmentsCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Financial Card
        double totalRevenue = 0;
        for (FinancialService service : clinic.getFinancialServices()) {
            totalRevenue += service.getTotalAmount();
        }
        
        JPanel financialCard = createReportCard("Financial Summary", new Color(230, 126, 34));
        addReportRow(financialCard, "Total Revenue:", "$" + String.format("%.2f", totalRevenue));
        addReportRow(financialCard, "Total Transactions:", String.valueOf(clinic.getFinancialServices().size()));
        contentPanel.add(financialCard);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Medical Records Card
        JPanel medicalCard = createReportCard("Medical Records", new Color(142, 68, 173));
        addReportRow(medicalCard, "Health Reports:", String.valueOf(clinic.getHealthReports().size()));
        addReportRow(medicalCard, "Medical Documents:", String.valueOf(clinic.getMedicalDocuments().size()));
        contentPanel.add(medicalCard);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.removeAll();
        mainPanel.add(reportPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private JPanel createReportCard(String title, Color headerColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(0, 0, 20, 0)
        ));
        card.setMaximumSize(new Dimension(1300, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(headerColor);
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel);
        
        card.add(header);
        
        return card;
    }
    
    private void addReportRow(JPanel card, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelText.setForeground(new Color(100, 100, 100));
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueText.setForeground(new Color(50, 50, 50));
        
        row.add(labelText, BorderLayout.WEST);
        row.add(valueText, BorderLayout.EAST);
        
        card.add(row);
    }
    
    private void showStatistics() {
        JPanel panel = createManagementPanel("Clinic Statistics & Report");
        JPanel contentPanel = (JPanel) ((JScrollPane) panel.getComponent(1)).getViewport().getView();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Clinic Details Section
        JPanel clinicDetailsPanel = new JPanel();
        clinicDetailsPanel.setLayout(new BoxLayout(clinicDetailsPanel, BoxLayout.Y_AXIS));
        clinicDetailsPanel.setBackground(CARD_BG);
        clinicDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        clinicDetailsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        JLabel clinicTitleLabel = new JLabel("Clinic Details: " + clinic.getName());
        clinicTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        clinicTitleLabel.setForeground(PRIMARY_COLOR);
        clinicDetailsPanel.add(clinicTitleLabel);
        clinicDetailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        String clinicDetails = clinic.getClinicDetails();
        JLabel detailsLabel = new JLabel(clinicDetails.substring(clinicDetails.indexOf('{') + 1, clinicDetails.lastIndexOf('}')));
        detailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        detailsLabel.setForeground(Color.GRAY);
        clinicDetailsPanel.add(detailsLabel);
        
        contentPanel.add(clinicDetailsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Statistics cards
        JPanel statsGrid = new JPanel(new GridLayout(2, 3, 25, 25));
        statsGrid.setBackground(BG_COLOR);
        statsGrid.setPreferredSize(new Dimension(1000, 400));
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        
        statsGrid.add(createStatCard("Total Doctors", String.valueOf(clinic.getDoctors().size()), PRIMARY_COLOR));
        statsGrid.add(createStatCard("Total Patients", String.valueOf(clinic.getPatients().size()), SECONDARY_COLOR));
        statsGrid.add(createStatCard("Total Appointments", String.valueOf(clinic.getAppointments().size()), ACCENT_COLOR));
        
        int scheduled = 0, completed = 0, cancelled = 0;
        for (Appointment apt : clinic.getAppointments()) {
            String status = apt.getStatus();
            if (status.equals("Scheduled") || status.equals("SCHEDULED")) scheduled++;
            else if (status.equals("Completed") || status.equals("COMPLETED")) completed++;
            else if (status.equals("Cancelled") || status.equals("CANCELLED")) cancelled++;
        }
        
        statsGrid.add(createStatCard("Scheduled", String.valueOf(scheduled), new Color(52, 152, 219)));
        statsGrid.add(createStatCard("Completed", String.valueOf(completed), new Color(46, 204, 113)));
        statsGrid.add(createStatCard("Cancelled", String.valueOf(cancelled), DANGER_COLOR));
        
        contentPanel.add(statsGrid);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Report Section
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBackground(CARD_BG);
        reportPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        reportPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        
        JLabel reportTitle = new JLabel("Clinic Report Summary");
        reportTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        reportTitle.setForeground(PRIMARY_COLOR);
        reportPanel.add(reportTitle);
        reportPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        String reportText = clinic.generateClinicReport();
        JTextArea reportArea = new JTextArea(reportText);
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 11));
        reportArea.setEditable(false);
        reportArea.setOpaque(false);
        reportArea.setLineWrap(true);
        reportArea.setWrapStyleWord(true);
        reportArea.setForeground(Color.DARK_GRAY);
        reportPanel.add(reportArea);
        
        contentPanel.add(reportPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Medical Records Section
        JPanel recordsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        recordsPanel.setBackground(BG_COLOR);
        recordsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        
        JPanel healthReportsCard = new JPanel();
        healthReportsCard.setLayout(new BoxLayout(healthReportsCard, BoxLayout.Y_AXIS));
        healthReportsCard.setBackground(CARD_BG);
        healthReportsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel healthLabel = new JLabel("Health Reports: " + clinic.getHealthReports().size());
        healthLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        healthLabel.setForeground(new Color(231, 76, 60));
        healthReportsCard.add(healthLabel);
        recordsPanel.add(healthReportsCard);
        
        JPanel medDocsCard = new JPanel();
        medDocsCard.setLayout(new BoxLayout(medDocsCard, BoxLayout.Y_AXIS));
        medDocsCard.setBackground(CARD_BG);
        medDocsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel docsLabel = new JLabel("Medical Documents: " + clinic.getMedicalDocuments().size());
        docsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        docsLabel.setForeground(new Color(155, 89, 182));
        medDocsCard.add(docsLabel);
        recordsPanel.add(medDocsCard);
        
        contentPanel.add(recordsPanel);
        
        mainPanel.add(panel, "STATISTICS");
        cardLayout.show(mainPanel, "STATISTICS");
    }
    
    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        card.setPreferredSize(new Dimension(200, 150));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel valueLabel = new JLabel("0");
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelLabel.setForeground(Color.WHITE);
        labelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(labelLabel);
        card.add(Box.createVerticalGlue());
        
        // Add animated counting effect - handle both numeric and currency values
        try {
            // Check if value contains currency symbol or decimal point
            boolean isCurrency = value.contains("$");
            String numericValue = value.replaceAll("[^0-9.]", ""); // Remove non-numeric characters except decimal point
            
            if (isCurrency || value.contains(".")) {
                // For currency or decimal values, just display directly without animation
                valueLabel.setText(value);
            } else {
                // For integer values, animate the counting
                int targetValue = Integer.parseInt(numericValue);
                Timer countTimer = new Timer(20, new ActionListener() {
                    int currentValue = 0;
                    int increment = Math.max(1, targetValue / 30);
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        currentValue += increment;
                        if (currentValue >= targetValue) {
                            currentValue = targetValue;
                            ((Timer) e.getSource()).stop();
                        }
                        valueLabel.setText(String.valueOf(currentValue));
                    }
                });
                countTimer.start();
            }
        } catch (NumberFormatException ex) {
            // If parsing fails, just display the value as-is
            valueLabel.setText(value);
        }
        
        // Add hover effect with scale animation
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(color);
            }
        });
        
        return card;
    }
    
    private JPanel createManagementPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        // Top bar with back button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton backBtn = createStyledButton("← Back to Dashboard", DANGER_COLOR);
        backBtn.addActionListener(e -> showMainDashboard());
        topBar.add(backBtn, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(topBar, BorderLayout.NORTH);
        
        // Content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Dialog methods
    private void showAddDoctorDialog() {
        JDialog dialog = new JDialog(this, "Add Doctor", true);
        dialog.setSize(550, 750);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(CARD_BG);
        
        JTextField nameField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        
        // City and Area dropdowns
        String[] cities = {"Cairo", "Giza", "Alexandria"};
        JComboBox<String> cityCombo = new JComboBox<>(cities);
        cityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cityCombo.setPreferredSize(new Dimension(350, 40));
        cityCombo.setMaximumSize(new Dimension(350, 40));
        
        JComboBox<String> areaCombo = new JComboBox<>();
        areaCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        areaCombo.setPreferredSize(new Dimension(350, 40));
        areaCombo.setMaximumSize(new Dimension(350, 40));
        
        cityCombo.addActionListener(e -> {
            String selectedCity = (String) cityCombo.getSelectedItem();
            areaCombo.removeAllItems();
            if ("Cairo".equals(selectedCity)) {
                String[] cairoAreas = {"Nasr City", "Heliopolis", "Maadi", "Zamalek", "Downtown Cairo", "New Cairo", "Mokattam", "Helwan", "Shubra", "Ain Shams", "Abbasia", "Garden City", "Dokki", "Mohandessin", "Agouza"};
                for (String area : cairoAreas) areaCombo.addItem(area);
            } else if ("Giza".equals(selectedCity)) {
                String[] gizaAreas = {"6th of October City", "Sheikh Zayed", "Mohandessin", "Dokki", "Agouza", "Haram", "Faisal", "Imbaba", "Kerdasa", "El Wahat", "October Gardens", "Beverly Hills", "Palm Hills", "Pyramids", "Smart Village"};
                for (String area : gizaAreas) areaCombo.addItem(area);
            } else if ("Alexandria".equals(selectedCity)) {
                String[] alexAreas = {"Smouha", "Sidi Gaber", "Sporting", "Cleopatra", "Stanley", "San Stefano", "Gleem", "Roushdy", "Louran", "Mandara", "Montaza", "Sidi Bishr", "Asafra", "Borg El Arab"};
                for (String area : alexAreas) areaCombo.addItem(area);
            }
        });
        cityCombo.setSelectedIndex(0);
        
        // Specialization dropdown
        String[] specializations = {
            "General Practitioner", "Cardiologist", "Dermatologist", "Pediatrician",
            "Orthopedist", "Neurologist", "Psychiatrist", "Ophthalmologist",
            "ENT Specialist", "Gynecologist", "Urologist", "Oncologist",
            "Radiologist", "Anesthesiologist", "Surgeon", "Dentist"
        };
        JComboBox<String> specializationCombo = new JComboBox<>(specializations);
        specializationCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        specializationCombo.setPreferredSize(new Dimension(350, 40));
        specializationCombo.setMaximumSize(new Dimension(350, 40));
        
        JTextField licenseField = createStyledTextField();
        JTextField experienceField = createStyledTextField();
        
        // Available days selection
        JLabel daysLabel = new JLabel("Select Available Days:");
        daysLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        daysLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel daysPanel = new JPanel();
        daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS));
        daysPanel.setBackground(CARD_BG);
        daysPanel.setMaximumSize(new Dimension(400, 200));
        daysPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Days of week
        String[] daysOfWeek = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
        
        ArrayList<JCheckBox> dayCheckboxes = new ArrayList<>();
        for (String day : daysOfWeek) {
            JCheckBox checkbox = new JCheckBox(day);
            checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            checkbox.setBackground(CARD_BG);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            dayCheckboxes.add(checkbox);
            daysPanel.add(checkbox);
        }
        
        JScrollPane daysScrollPane = new JScrollPane(daysPanel);
        daysScrollPane.setPreferredSize(new Dimension(350, 150));
        daysScrollPane.setMaximumSize(new Dimension(350, 150));
        daysScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(createFormField("Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("City:", cityCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Area:", areaCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Phone (11 digits):", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Specialization:", specializationCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("License Number:", licenseField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Years of Experience:", experienceField));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(daysLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(daysScrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton addBtn = createStyledButton("Add Doctor", ACCENT_COLOR);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String city = (String) cityCombo.getSelectedItem();
                String area = (String) areaCombo.getSelectedItem();
                String address = area + ", " + city + ", Egypt";
                String phone = phoneField.getText().trim();
                String specialization = (String) specializationCombo.getSelectedItem();
                String license = licenseField.getText().trim();
                int experience = Integer.parseInt(experienceField.getText().trim());
                
                if (name.isEmpty() || phone.isEmpty() || license.isEmpty()) {
                    showError("Please fill in all fields");
                    return;
                }
                
                if (!phone.matches("\\d{11}")) {
                    showError("Phone must be exactly 11 digits");
                    return;
                }
                
                // Collect selected days
                ArrayList<String> selectedDays = new ArrayList<>();
                for (JCheckBox checkbox : dayCheckboxes) {
                    if (checkbox.isSelected()) {
                        selectedDays.add(checkbox.getText());
                    }
                }
                
                if (selectedDays.isEmpty()) {
                    showError("Please select at least one available day");
                    return;
                }
                
                Doctor doctor = new Doctor(name, address, phone, specialization, license, experience);
                
                // Add selected days
                for (String day : selectedDays) {
                    doctor.addAvailableDay(day);
                }
                
                clinic.addDoctor(doctor);
                autoSave();
                showSuccess("Doctor added successfully!\nID: " + doctor.getId() + "\nAvailable on " + selectedDays.size() + " days");
                dialog.dispose();
                showDoctorManagement();
            } catch (NumberFormatException ex) {
                showError("Invalid experience value");
            }
        });
        
        // Enable Enter key to submit
        addEnterKeyListeners(addBtn, nameField, phoneField, licenseField, experienceField);
        
        panel.add(addBtn);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddPatientDialog() {
        JDialog dialog = new JDialog(this, "Register Patient", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);
        
        JTextField nameField = createStyledTextField();
        JTextField ageField = createStyledTextField();
        JTextField phoneField = createStyledTextField();
        
        // City and Area dropdowns
        String[] cities = {"Cairo", "Giza"};
        JComboBox<String> cityCombo = new JComboBox<>(cities);
        cityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cityCombo.setPreferredSize(new Dimension(350, 40));
        cityCombo.setMaximumSize(new Dimension(350, 40));
        
        JComboBox<String> areaCombo = new JComboBox<>();
        areaCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        areaCombo.setPreferredSize(new Dimension(350, 40));
        areaCombo.setMaximumSize(new Dimension(350, 40));
        
        cityCombo.addActionListener(e -> {
            String selectedCity = (String) cityCombo.getSelectedItem();
            areaCombo.removeAllItems();
            if ("Cairo".equals(selectedCity)) {
                String[] cairoAreas = {"Nasr City", "Heliopolis", "Maadi", "Zamalek", "Downtown Cairo", "New Cairo", "Mokattam", "Helwan", "Shubra", "Ain Shams", "Abbasia", "Garden City", "Dokki", "Mohandessin", "Agouza"};
                for (String area : cairoAreas) areaCombo.addItem(area);
            } else if ("Giza".equals(selectedCity)) {
                String[] gizaAreas = {"6th of October City", "Sheikh Zayed", "Mohandessin", "Dokki", "Agouza", "Haram", "Faisal", "Imbaba", "Kerdasa", "El Wahat", "October Gardens", "Beverly Hills", "Palm Hills", "Pyramids", "Smart Village"};
                for (String area : gizaAreas) areaCombo.addItem(area);
            }
        });
        cityCombo.setSelectedIndex(0);
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"M", "F"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        genderCombo.setPreferredSize(new Dimension(350, 40));
        genderCombo.setMaximumSize(new Dimension(350, 40));
        
        JComboBox<String> bloodTypeCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        bloodTypeCombo.setPreferredSize(new Dimension(350, 40));
        bloodTypeCombo.setMaximumSize(new Dimension(350, 40));
        
        panel.add(createFormField("Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Age:", ageField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("City:", cityCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Area:", areaCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Phone (11 digits):", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Gender:", genderCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Blood Type:", bloodTypeCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton addBtn = createStyledButton("Register Patient", ACCENT_COLOR);
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String city = (String) cityCombo.getSelectedItem();
                String area = (String) areaCombo.getSelectedItem();
                String address = area + ", " + city + ", Egypt";
                String phone = phoneField.getText().trim();
                String gender = (String) genderCombo.getSelectedItem();
                String bloodType = (String) bloodTypeCombo.getSelectedItem();
                
                if (name.isEmpty() || phone.isEmpty()) {
                    showError("Please fill in all fields");
                    return;
                }
                
                if (!phone.matches("\\d{11}")) {
                    showError("Phone must be exactly 11 digits");
                    return;
                }
                
                Patient patient = new Patient(age, address, phone, name, bloodType, 
                                             phone, gender, new ArrayList<>());
                clinic.registerPatient(patient);
                autoSave();
                showSuccess("Patient registered successfully!\nID: " + patient.getId());
                dialog.dispose();
                showPatientManagement();
            } catch (NumberFormatException ex) {
                showError("Invalid age value");
            }
        });
        
        // Enable Enter key to submit
        addEnterKeyListeners(addBtn, nameField, ageField, phoneField);
        
        panel.add(addBtn);
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }
    
    private void showScheduleAppointmentDialog() {
        JDialog dialog = new JDialog(this, "Schedule Appointment", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);
        
        // Create patient dropdown
        JComboBox<String> patientCombo = new JComboBox<>();
        patientCombo.addItem("-- Select Patient --");
        for (Patient patient : clinic.getPatients()) {
            patientCombo.addItem(patient.getId() + " - " + patient.getName());
        }
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        patientCombo.setPreferredSize(new Dimension(350, 40));
        patientCombo.setMaximumSize(new Dimension(350, 40));
        
        // Create doctor dropdown
        JComboBox<String> doctorCombo = new JComboBox<>();
        doctorCombo.addItem("-- Select Doctor --");
        for (Doctor doctor : clinic.getDoctors()) {
            doctorCombo.addItem(doctor.getId() + " - Dr. " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        }
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        doctorCombo.setPreferredSize(new Dimension(350, 40));
        doctorCombo.setMaximumSize(new Dimension(350, 40));
        
        // Create available dates dropdown (will be populated when doctor is selected)
        JComboBox<String> dateCombo = new JComboBox<>();
        dateCombo.addItem("-- Select Date --");
        dateCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dateCombo.setPreferredSize(new Dimension(350, 40));
        dateCombo.setMaximumSize(new Dimension(350, 40));
        dateCombo.setEnabled(false);
        
        // Create time combo (will be populated when date is selected)
        JComboBox<String> timeCombo = new JComboBox<>();
        timeCombo.addItem("-- Select Time --");
        timeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        timeCombo.setPreferredSize(new Dimension(350, 40));
        timeCombo.setMaximumSize(new Dimension(350, 40));
        timeCombo.setEnabled(false);
        
        // Update dates when doctor is selected
        doctorCombo.addActionListener(e -> {
            String doctorSelection = (String) doctorCombo.getSelectedItem();
            dateCombo.removeAllItems();
            dateCombo.addItem("-- Select Date --");
            timeCombo.removeAllItems();
            timeCombo.addItem("-- Select Time --");
            timeCombo.setEnabled(false);
            
            if (!doctorSelection.startsWith("--")) {
                String doctorId = doctorSelection.split(" - ")[0];
                Doctor selectedDoctor = clinic.searchDoctor(doctorId);
                if (selectedDoctor != null) {
                    for (String date : selectedDoctor.getAvailableDates()) {
                        dateCombo.addItem(date);
                    }
                    dateCombo.setEnabled(true);
                }
            } else {
                dateCombo.setEnabled(false);
            }
        });
        
        // Update available times when date is selected
        dateCombo.addActionListener(e -> {
            String dateSelection = (String) dateCombo.getSelectedItem();
            String doctorSelection = (String) doctorCombo.getSelectedItem();
            timeCombo.removeAllItems();
            timeCombo.addItem("-- Select Time --");
            
            if (dateSelection != null && !dateSelection.startsWith("--") && 
                doctorSelection != null && !doctorSelection.startsWith("--")) {
                String doctorId = doctorSelection.split(" - ")[0];
                Doctor selectedDoctor = clinic.searchDoctor(doctorId);
                if (selectedDoctor != null) {
                    // Extract date from "Day - dd/MM/yyyy" format
                    String dateOnly = dateSelection.contains(" - ") ? dateSelection.split(" - ")[1] : dateSelection;
                    ArrayList<String> availableTimes = selectedDoctor.getAvailableTimesForDate(dateOnly);
                    for (String time : availableTimes) {
                        timeCombo.addItem(time);
                    }
                    if (!availableTimes.isEmpty()) {
                        timeCombo.setEnabled(true);
                    } else {
                        timeCombo.setEnabled(false);
                        showInfo("No available time slots for this date");
                    }
                }
            } else {
                timeCombo.setEnabled(false);
            }
        });
        
        // Reason dropdown
        String[] reasons = {
            "Regular Checkup",
            "Follow-up Visit",
            "New Symptoms",
            "Chronic Disease Management",
            "Vaccination",
            "Medical Certificate",
            "Lab Results Review",
            "Prescription Refill",
            "Emergency",
            "Consultation",
            "Physical Examination",
            "Second Opinion",
            "Other"
        };
        JComboBox<String> reasonCombo = new JComboBox<>(reasons);
        reasonCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        reasonCombo.setPreferredSize(new Dimension(350, 40));
        reasonCombo.setMaximumSize(new Dimension(350, 40));
        
        panel.add(createFormField("Patient:", patientCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Doctor:", doctorCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Available Date:", dateCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Time:", timeCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Reason:", reasonCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton scheduleBtn = createStyledButton("Schedule", ACCENT_COLOR);
        scheduleBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        scheduleBtn.addActionListener(e -> {
            try {
                String patientSelection = (String) patientCombo.getSelectedItem();
                String doctorSelection = (String) doctorCombo.getSelectedItem();
                String dateSelection = (String) dateCombo.getSelectedItem();
                String timeSelection = (String) timeCombo.getSelectedItem();
                
                if (patientSelection.startsWith("--") || doctorSelection.startsWith("--") || dateSelection.startsWith("--")) {
                    showError("Please select patient, doctor, and date");
                    return;
                }
                
                String patientId = patientSelection.split(" - ")[0];
                String doctorId = doctorSelection.split(" - ")[0];
                String reason = (String) reasonCombo.getSelectedItem();
                
                if (timeSelection.startsWith("--")) {
                    showError("Please select a time slot");
                    return;
                }
                
                Patient patient = clinic.searchPatient(patientId);
                Doctor doctor = clinic.searchDoctor(doctorId);
                
                if (patient == null || doctor == null) {
                    showError("Invalid Patient ID or Doctor ID");
                    return;
                }
                
                // Extract date from "Day - dd/MM/yyyy" format
                String dateOnly = dateSelection.contains(" - ") ? dateSelection.split(" - ")[1] : dateSelection;
                
                // Check if time slot is still available
                if (!doctor.isTimeSlotAvailable(dateOnly, timeSelection)) {
                    showError("This time slot has just been booked. Please select another time.");
                    return;
                }
                
                // Convert 12-hour format to 24-hour format
                String dateStr = dateOnly + " " + timeSelection;
                SimpleDateFormat sdf12 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                SimpleDateFormat sdf24 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = sdf12.parse(dateStr);
                String formattedDate = sdf24.format(date);
                date = sdf24.parse(formattedDate);
                
                Appointment appointment = new Appointment(patient, doctor, date, "SCHEDULED", reason);
                clinic.scheduleAppointment(appointment);
                
                // Book the time slot and remove date if fully booked
                doctor.bookTimeSlot(dateOnly, timeSelection);
                
                autoSave();
                showSuccess("Appointment scheduled successfully!\nID: " + appointment.getAppointmentId() + 
                           "\nDate: " + dateSelection + "\nTime: " + timeSelection);
                dialog.dispose();
                showAppointmentManagement();
            } catch (Exception ex) {
                showError("Invalid date/time format or other error: " + ex.getMessage());
            }
        });
        
        panel.add(scheduleBtn);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showRemoveDoctorDialog() {
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID to remove:");
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove this doctor?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clinic.removeDoctor(doctorId.trim());
                showDoctorManagement();
            }
        }
    }
    
    private void showRemovePatientDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to remove:");
        if (patientId != null && !patientId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to remove this patient?", 
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clinic.removePatient(patientId.trim());
                showPatientManagement();
            }
        }
    }
    
    private void showCancelAppointmentDialog() {
        String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to cancel:");
        if (appointmentId != null && !appointmentId.trim().isEmpty()) {
            for (Appointment apt : clinic.getAppointments()) {
                if (apt.getAppointmentId().equals(appointmentId.trim())) {
                    apt.setStatus("CANCELLED");
                    showSuccess("Appointment cancelled successfully!");
                    showAppointmentManagement();
                    return;
                }
            }
            showError("Appointment not found!");
        }
    }
    
    private void showSearchDoctorDialog() {
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID to search:");
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            Doctor doctor = clinic.searchDoctor(doctorId.trim());
            if (doctor != null) {
                JOptionPane.showMessageDialog(this, doctor.getDoctorDetails(), 
                    "Doctor Details", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showEditDoctorDialog() {
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID to edit:");
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            Doctor doctor = clinic.searchDoctor(doctorId.trim());
            if (doctor != null) {
                JDialog dialog = new JDialog(this, "Edit Doctor", true);
                dialog.setSize(500, 350);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                JTextField specializationField = createStyledTextField();
                specializationField.setText(doctor.getSpecialization());
                
                JTextField licenseField = createStyledTextField();
                licenseField.setText(doctor.getLicenseNumber());
                
                JTextField experienceField = createStyledTextField();
                experienceField.setText(String.valueOf(doctor.getYearsOfExperience()));
                
                panel.add(createFormField("Specialization:", specializationField));
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                panel.add(createFormField("License Number:", licenseField));
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
                panel.add(createFormField("Years of Experience:", experienceField));
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                JButton saveBtn = createStyledButton("Save Changes", ACCENT_COLOR);
                saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                saveBtn.addActionListener(e -> {
                    try {
                        doctor.setSpecialization(specializationField.getText().trim());
                        doctor.setLicenseNumber(licenseField.getText().trim());
                        doctor.setYearsOfExperience(Integer.parseInt(experienceField.getText().trim()));
                        autoSave();
                        showSuccess("Doctor information updated successfully!");
                        dialog.dispose();
                        showDoctorManagement();
                    } catch (NumberFormatException ex) {
                        showError("Invalid experience value");
                    }
                });
                
                panel.add(saveBtn);
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Doctor not found!");
            }
        }
    }
    
    private void showManageDoctorAvailabilityDialog() {
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID to manage availability:");
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            Doctor doctor = clinic.searchDoctor(doctorId.trim());
            if (doctor != null) {
                JDialog dialog = new JDialog(this, "Manage Doctor Availability", true);
                dialog.setSize(500, 450);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                JLabel titleLabel = new JLabel("Available Days for Dr. " + doctor.getName());
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titleLabel.setForeground(PRIMARY_COLOR);
                panel.add(titleLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                String[] daysOfWeek = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};
                ArrayList<JCheckBox> dayCheckboxes = new ArrayList<>();
                
                for (String day : daysOfWeek) {
                    JCheckBox checkbox = new JCheckBox(day);
                    checkbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    checkbox.setBackground(CARD_BG);
                    checkbox.setSelected(doctor.getAvailableDays().contains(day));
                    dayCheckboxes.add(checkbox);
                    panel.add(checkbox);
                }
                
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                JButton saveBtn = createStyledButton("Save Availability", ACCENT_COLOR);
                saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                saveBtn.addActionListener(e -> {
                    doctor.clearAvailableDays();
                    for (JCheckBox checkbox : dayCheckboxes) {
                        if (checkbox.isSelected()) {
                            doctor.addAvailableDay(checkbox.getText());
                        }
                    }
                    autoSave();
                    showSuccess("Doctor availability updated successfully!");
                    dialog.dispose();
                    showDoctorManagement();
                });
                
                panel.add(saveBtn);
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Doctor not found!");
            }
        }
    }
    
    private void showDoctorReportDialog() {
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID to view report:");
        if (doctorId != null && !doctorId.trim().isEmpty()) {
            Doctor doctor = clinic.searchDoctor(doctorId.trim());
            if (doctor != null) {
                JDialog dialog = new JDialog(this, "Doctor Report", true);
                dialog.setSize(600, 450);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                // Report section
                JTextArea reportArea = new JTextArea(doctor.generateDoctorReport());
                reportArea.setFont(new Font("Courier New", Font.PLAIN, 12));
                reportArea.setEditable(false);
                reportArea.setLineWrap(true);
                reportArea.setWrapStyleWord(true);
                reportArea.setBackground(new Color(245, 245, 245));
                
                JScrollPane scrollPane = new JScrollPane(reportArea);
                scrollPane.setPreferredSize(new Dimension(550, 200));
                panel.add(scrollPane);
                
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Additional info
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(CARD_BG);
                infoPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                JLabel experiencedLabel = new JLabel("Experience Status: " + (doctor.isExperienced() ? "✓ Experienced (5+ years)" : "● Junior (< 5 years)"));
                experiencedLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                experiencedLabel.setForeground(doctor.isExperienced() ? ACCENT_COLOR : new Color(230, 126, 34));
                infoPanel.add(experiencedLabel);
                
                infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                JLabel availabilityLabel = new JLabel("Available Days: " + doctor.getAvailableDays().toString());
                availabilityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                infoPanel.add(availabilityLabel);
                
                panel.add(infoPanel);
                
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Doctor not found!");
            }
        }
    }
    
    private void showSearchPatientDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to search:");
        if (patientId != null && !patientId.trim().isEmpty()) {
            Patient patient = clinic.searchPatient(patientId.trim());
            if (patient != null) {
                String info = "ID: " + patient.getId() + "\n" +
                             "Name: " + patient.getName() + "\n" +
                             "Age: " + patient.getAge() + "\n" +
                             "Gender: " + patient.getGender() + "\n" +
                             "Blood Type: " + patient.getBloodType() + "\n" +
                             "Phone: " + patient.getPhone();
                JOptionPane.showMessageDialog(this, info, "Patient Details", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showEditPatientDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to edit:");
        if (patientId != null && !patientId.trim().isEmpty()) {
            Patient patient = clinic.searchPatient(patientId.trim());
            if (patient != null) {
                JDialog dialog = new JDialog(this, "Edit Patient", true);
                dialog.setSize(500, 450);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                // Age field
                JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(patient.getAge(), 1, 120, 1));
                ageSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                ageSpinner.setPreferredSize(new Dimension(350, 40));
                ageSpinner.setMaximumSize(new Dimension(350, 40));
                panel.add(createFormField("Age:", ageSpinner));
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Gender combo
                JComboBox<String> genderCombo = new JComboBox<>(new String[]{"M", "F"});
                genderCombo.setSelectedItem(patient.getGender());
                genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                genderCombo.setPreferredSize(new Dimension(350, 40));
                genderCombo.setMaximumSize(new Dimension(350, 40));
                panel.add(createFormField("Gender:", genderCombo));
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Blood type combo
                JComboBox<String> bloodTypeCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
                bloodTypeCombo.setSelectedItem(patient.getBloodType());
                bloodTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                bloodTypeCombo.setPreferredSize(new Dimension(350, 40));
                bloodTypeCombo.setMaximumSize(new Dimension(350, 40));
                panel.add(createFormField("Blood Type:", bloodTypeCombo));
                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                // Buttons
                JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                buttonsPanel.setBackground(CARD_BG);
                
                JButton saveBtn = createStyledButton("Save Changes", ACCENT_COLOR);
                JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
                
                saveBtn.addActionListener(e -> {
                    try {
                        int newAge = (Integer) ageSpinner.getValue();
                        String newGender = (String) genderCombo.getSelectedItem();
                        String newBloodType = (String) bloodTypeCombo.getSelectedItem();
                        
                        patient.setAge(newAge);
                        patient.setGender(newGender);
                        patient.setBloodType(newBloodType);
                        
                        autoSave();
                        showSuccess("Patient updated successfully!");
                        dialog.dispose();
                        showPatientManagement();
                    } catch (Exception ex) {
                        showError("Error updating patient: " + ex.getMessage());
                    }
                });
                
                cancelBtn.addActionListener(e -> dialog.dispose());
                
                buttonsPanel.add(saveBtn);
                buttonsPanel.add(cancelBtn);
                panel.add(buttonsPanel);
                
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Patient not found!");
            }
        }
    }
    
    private void showManagePatientRecordsDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to manage records:");
        if (patientId != null && !patientId.trim().isEmpty()) {
            Patient patient = clinic.searchPatient(patientId.trim());
            if (patient != null) {
                JDialog dialog = new JDialog(this, "Manage Medical Records", true);
                dialog.setSize(600, 500);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                // Patient info label
                JLabel infoLabel = new JLabel("Patient: " + patient.getName() + " (ID: " + patient.getId() + ")");
                infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
                panel.add(infoLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Display existing records
                JPanel recordsPanel = new JPanel();
                recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.Y_AXIS));
                recordsPanel.setBackground(CARD_BG);
                recordsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Medical Records"),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                ArrayList<String> records = patient.getMedicalRecords();
                if (records.isEmpty()) {
                    JLabel noRecordsLabel = new JLabel("No medical records yet");
                    noRecordsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    noRecordsLabel.setForeground(Color.GRAY);
                    recordsPanel.add(noRecordsLabel);
                } else {
                    for (int i = 0; i < records.size(); i++) {
                        JLabel recordLabel = new JLabel((i + 1) + ". " + records.get(i));
                        recordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        recordsPanel.add(recordLabel);
                        if (i < records.size() - 1) {
                            recordsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                        }
                    }
                }
                recordsPanel.add(Box.createVerticalGlue());
                
                JScrollPane scrollPane = new JScrollPane(recordsPanel);
                scrollPane.setPreferredSize(new Dimension(550, 200));
                panel.add(scrollPane);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Add new record section
                JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                addPanel.setBackground(CARD_BG);
                
                JLabel addLabel = new JLabel("Add Record:");
                addLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                JTextField recordField = new JTextField(40);
                recordField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                JButton addRecordBtn = createStyledButton("Add", ACCENT_COLOR);
                
                addRecordBtn.addActionListener(e -> {
                    String newRecord = recordField.getText().trim();
                    if (!newRecord.isEmpty()) {
                        patient.addMedicalRecord(newRecord);
                        autoSave();
                        showSuccess("Record added successfully!");
                        dialog.dispose();
                        showManagePatientRecordsDialog();
                    } else {
                        showError("Please enter a record");
                    }
                });
                
                addPanel.add(addLabel);
                addPanel.add(recordField);
                addPanel.add(addRecordBtn);
                panel.add(addPanel);
                
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Patient not found!");
            }
        }
    }
    
    private void showPatientDetailsDialog() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to view details:");
        if (patientId != null && !patientId.trim().isEmpty()) {
            Patient patient = clinic.searchPatient(patientId.trim());
            if (patient != null) {
                JDialog dialog = new JDialog(this, "Patient Details", true);
                dialog.setSize(600, 500);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                panel.setBackground(CARD_BG);
                
                // Personal info section
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(CARD_BG);
                infoPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Personal Information"),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                addDetailRow(infoPanel, "ID:", patient.getId());
                addDetailRow(infoPanel, "Name:", patient.getName());
                addDetailRow(infoPanel, "Age:", String.valueOf(patient.getAge()));
                addDetailRow(infoPanel, "Gender:", patient.getGender());
                addDetailRow(infoPanel, "Blood Type:", patient.getBloodType());
                addDetailRow(infoPanel, "Phone:", patient.getPhone());
                addDetailRow(infoPanel, "Contact Info:", patient.getContactInfo());
                addDetailRow(infoPanel, "Address:", patient.getAddress());
                
                panel.add(infoPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Medical records section
                JPanel recordsPanel = new JPanel();
                recordsPanel.setLayout(new BoxLayout(recordsPanel, BoxLayout.Y_AXIS));
                recordsPanel.setBackground(CARD_BG);
                recordsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Medical Records (" + patient.getMedicalRecords().size() + ")"),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                
                ArrayList<String> records = patient.getMedicalRecords();
                if (records.isEmpty()) {
                    JLabel noRecordsLabel = new JLabel("No medical records on file");
                    noRecordsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    noRecordsLabel.setForeground(Color.GRAY);
                    recordsPanel.add(noRecordsLabel);
                } else {
                    for (int i = 0; i < records.size(); i++) {
                        JLabel recordLabel = new JLabel((i + 1) + ". " + records.get(i));
                        recordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        recordsPanel.add(recordLabel);
                        if (i < records.size() - 1) {
                            recordsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                        }
                    }
                }
                recordsPanel.add(Box.createVerticalGlue());
                
                JScrollPane recordsScroll = new JScrollPane(recordsPanel);
                recordsScroll.setPreferredSize(new Dimension(550, 200));
                panel.add(recordsScroll);
                
                // Close button
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                closePanel.setBackground(CARD_BG);
                JButton closeBtn = createStyledButton("Close", SECONDARY_COLOR);
                closeBtn.addActionListener(e -> dialog.dispose());
                closePanel.add(closeBtn);
                panel.add(closePanel);
                
                dialog.add(new JScrollPane(panel));
                dialog.setVisible(true);
            } else {
                showError("Patient not found!");
            }
        }
    }
    
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        rowPanel.setBackground(panel.getBackground());
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelComp.setPreferredSize(new Dimension(120, 25));
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rowPanel.add(labelComp);
        rowPanel.add(valueComp);
        panel.add(rowPanel);
    }
    
    private void showProcessPaymentDialog() {
        JDialog dialog = new JDialog(this, "Process Payment", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BG);
        
        // Create patient dropdown
        JComboBox<String> patientCombo = new JComboBox<>();
        patientCombo.addItem("-- Select Patient --");
        for (Patient patient : clinic.getPatients()) {
            patientCombo.addItem(patient.getId() + " - " + patient.getName());
        }
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        patientCombo.setPreferredSize(new Dimension(350, 40));
        patientCombo.setMaximumSize(new Dimension(350, 40));
        
        JTextField amountField = createStyledTextField();
        JTextField totalField = createStyledTextField();
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"CASH", "VISA"});
        methodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        methodCombo.setPreferredSize(new Dimension(350, 40));
        methodCombo.setMaximumSize(new Dimension(350, 40));
        
        panel.add(createFormField("Patient:", patientCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Amount:", amountField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Total Amount:", totalField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormField("Payment Method:", methodCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton processBtn = createStyledButton("Process Payment", ACCENT_COLOR);
        processBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        processBtn.addActionListener(e -> {
            try {
                String patientSelection = (String) patientCombo.getSelectedItem();
                
                if (patientSelection.startsWith("--")) {
                    showError("Please select a patient");
                    return;
                }
                
                String patientId = patientSelection.split(" - ")[0];
                double amount = Double.parseDouble(amountField.getText().trim());
                double total = Double.parseDouble(totalField.getText().trim());
                String method = (String) methodCombo.getSelectedItem();
                
                Patient patient = clinic.searchPatient(patientId);
                if (patient == null) {
                    showError("Patient not found!");
                    return;
                }
                
                // Create and save financial service
                FinancialService service = new FinancialService(patient, amount, total, "Pending", method);
                service.processPayment(total);
                clinic.addFinancialService(service);
                
                autoSave();
                showSuccess("Payment processed successfully!\nInvoice ID: " + service.getInvoiceId() + "\nStatus: " + service.getStatus());
                dialog.dispose();
                showFinancialManagement();
            } catch (NumberFormatException ex) {
                showError("Invalid amount values");
            }
        });
        
        // Enable Enter key to submit
        addEnterKeyListeners(processBtn, amountField, totalField);
        
        panel.add(processBtn);
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAllDoctors() {
        if (clinic.getDoctors().isEmpty()) {
            showInfo("No doctors in the clinic");
        } else {
            showDoctorManagement();
        }
    }
    
    private void showAllPatients() {
        if (clinic.getPatients().isEmpty()) {
            showInfo("No patients registered");
        } else {
            showPatientManagement();
        }
    }
    
    private void showAllAppointments() {
        if (clinic.getAppointments().isEmpty()) {
            showInfo("No appointments scheduled");
        } else {
            showAppointmentManagement();
        }
    }
    
    // Helper methods
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(350, 40));
        field.setMaximumSize(new Dimension(350, 40));
        
        // Add focus listener for interactive visual feedback
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * Add Enter key listener to trigger button action
     */
    private void addEnterKeyListener(JComponent component, JButton button) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    button.doClick();
                }
            }
        });
    }
    
    /**
     * Add Enter key listener to multiple components
     */
    private void addEnterKeyListeners(JButton button, JComponent... components) {
        for (JComponent component : components) {
            addEnterKeyListener(component, button);
        }
    }
    
    private JPanel createFormField(String label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setMaximumSize(new Dimension(400, 80));
        
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (component instanceof JTextField || component instanceof JComboBox) {
            component.setMaximumSize(new Dimension(350, 40));
        }
        
        panel.add(jLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 150));
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 42));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.repaint();
            }
        });
        
        return button;
    }
    
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            private int hoveredRow = -1;
            
            {
                // Add mouse motion listener for row hover effect
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        if (row != hoveredRow) {
                            hoveredRow = row;
                            repaint();
                        }
                    }
                });
                
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredRow = -1;
                        repaint();
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int row = rowAtPoint(e.getPoint());
                        int col = columnAtPoint(e.getPoint());
                        // If clicked on ID column (first column), copy to clipboard
                        if (col == 0 && row >= 0) {
                            Object id = getValueAt(row, col);
                            if (id != null) {
                                java.awt.datatransfer.StringSelection stringSelection = 
                                    new java.awt.datatransfer.StringSelection(id.toString());
                                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                                    .setContents(stringSelection, null);
                                
                                // Show brief notification
                                JLabel notification = new JLabel(" \u2713 ID Copied! ");
                                notification.setFont(new Font("Segoe UI", Font.BOLD, 12));
                                notification.setForeground(Color.WHITE);
                                notification.setBackground(ACCENT_COLOR);
                                notification.setOpaque(true);
                                notification.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                                
                                JWindow window = new JWindow();
                                window.add(notification);
                                window.pack();
                                Point mousePos = e.getLocationOnScreen();
                                window.setLocation(mousePos.x + 10, mousePos.y - 30);
                                window.setVisible(true);
                                
                                Timer timer = new Timer(1000, evt -> window.dispose());
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                    }
                });
            }
            
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Always set colors, regardless of selection
                if (isRowSelected(row)) {
                    c.setForeground(Color.WHITE);
                    c.setBackground(new Color(52, 152, 219));
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (row == hoveredRow) {
                    // Hover effect
                    c.setBackground(new Color(189, 224, 254));
                    if (column == 0 || column == 1) {
                        c.setForeground(new Color(30, 100, 180));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        c.setForeground(new Color(50, 50, 50));
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                } else {
                    // Alternating row colors
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(240, 248, 255));
                    }
                    
                    // Color specific columns (ID and Name columns) in blue
                    if (column == 0 || column == 1) {
                        c.setForeground(new Color(30, 100, 180));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        c.setForeground(new Color(50, 50, 50));
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                }
                
                return c;
            }
        };
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Custom header renderer for colored columns
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            private int hoveredColumn = -1;
            
            {
                // Add mouse motion listener to detect hover
                header.addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        int col = header.columnAtPoint(e.getPoint());
                        if (col != hoveredColumn) {
                            hoveredColumn = col;
                            header.repaint();
                        }
                    }
                });
                
                header.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredColumn = -1;
                        header.repaint();
                    }
                });
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
                label.setOpaque(true);
                
                Color baseColor;
                // Color different header columns
                if (column == 0) {
                    baseColor = new Color(41, 128, 185); // Blue for ID
                } else if (column == 1) {
                    baseColor = new Color(52, 152, 219); // Lighter blue for Name
                } else if (column == 2) {
                    baseColor = new Color(46, 204, 113); // Green
                } else if (column == 3) {
                    baseColor = new Color(155, 89, 182); // Purple
                } else if (column == 4) {
                    baseColor = new Color(230, 126, 34); // Orange
                } else {
                    baseColor = new Color(26, 188, 156); // Teal
                }
                
                // Apply hover effect
                if (column == hoveredColumn) {
                    label.setBackground(baseColor.brighter());
                    label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.WHITE, 2),
                        BorderFactory.createEmptyBorder(8, 3, 8, 3)
                    ));
                } else {
                    label.setBackground(baseColor);
                }
                
                label.setForeground(Color.WHITE);
                
                return label;
            }
        });
        
        table.setSelectionBackground(new Color(52, 152, 219));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        
        return table;
    }
    
    private void showHealthReports() {
        JPanel healthPanel = new JPanel(new BorderLayout(20, 20));
        healthPanel.setBackground(Color.WHITE);
        healthPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Health Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        healthPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton createReportBtn = createStyledButton("Create Health Report", ACCENT_COLOR);
        JButton viewAllBtn = createStyledButton("View All Reports", PRIMARY_COLOR);
        JButton detailsBtn = createStyledButton("View Details", SECONDARY_COLOR);
        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        
        buttonPanel.add(createReportBtn);
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(detailsBtn);
        buttonPanel.add(backBtn);
        
        // Table (expanded)
        String[] columnNames = {"Report ID", "Patient Name", "Date", "Height (cm)", "Weight (kg)", "BMI", "Blood Pressure", "Heart Rate", "Temperature", "Blood Sugar", "Condition"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable reportsTable = createStyledTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.setPreferredSize(new Dimension(1300, 600));
        
        // Center panel to hold button panel and table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        healthPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Load initial data
        loadHealthReports(tableModel);
        
        // Button actions
        createReportBtn.addActionListener(e -> showCreateHealthReportDialog(tableModel));
        viewAllBtn.addActionListener(e -> loadHealthReports(tableModel));
        detailsBtn.addActionListener(e -> {
            int row = reportsTable.getSelectedRow();
            if (row == -1) { showError("Select a report to view details."); return; }
            String reportId = (String) reportsTable.getValueAt(row, 0);
            Health_Report target = null;
            for (Health_Report r : clinic.getHealthReports()) {
                if (r.getReportId().equals(reportId)) { target = r; break; }
            }
            if (target == null) { showError("Report not found."); return; }
            showHealthReportDetailsDialog(target, tableModel);
        });
        backBtn.addActionListener(e -> showMainDashboard());
        
        mainPanel.removeAll();
        mainPanel.add(healthPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void loadHealthReports(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        
        for (Health_Report report : clinic.getHealthReports()) {
            String patientName = report.getPatient() != null ? report.getPatient().getName() : "Unknown";
            
            int cols = tableModel.getColumnCount();
            if (cols >= 11) {
                tableModel.addRow(new Object[]{
                    report.getReportId(),
                    patientName,
                    dateFormat.format(report.getReportDate()),
                    report.getHeight(),
                    report.getWeight(),
                    String.format("%.2f", report.getBmi()),
                    report.getBloodPressure(),
                    report.getHeartRate() + " bpm",
                    report.getTemperature() + "°C",
                    report.getBloodSugar(),
                    report.getGeneralCondition()
                });
            } else if (cols >= 9) {
                tableModel.addRow(new Object[]{
                    report.getReportId(),
                    patientName,
                    dateFormat.format(report.getReportDate()),
                    report.getHeight(),
                    report.getWeight(),
                    String.format("%.2f", report.getBmi()),
                    report.getBloodPressure(),
                    report.getHeartRate() + " bpm",
                    report.getTemperature() + "°C"
                });
            } else {
                tableModel.addRow(new Object[]{
                    report.getReportId(),
                    patientName,
                    dateFormat.format(report.getReportDate()),
                    String.format("%.2f", report.getBmi()),
                    report.getBloodPressure(),
                    report.getHeartRate() + " bpm",
                    report.getTemperature() + "°C"
                });
            }
        }
    }
    
    private void showCreateHealthReportDialog(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Create Health Report", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Create Health Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Patient selection
        JLabel patientLabel = new JLabel("Select Patient:");
        patientLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(patientLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JComboBox<String> patientCombo = new JComboBox<>();
        patientCombo.setMaximumSize(new Dimension(500, 40));
        patientCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        for (Patient p : clinic.getPatients()) {
            patientCombo.addItem(p.getId() + " - " + p.getName());
        }
        
        contentPanel.add(patientCombo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Height
        JLabel heightLabel = new JLabel("Height (cm):");
        heightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        heightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(heightLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField heightField = createStyledTextField();
        heightField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(heightField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Weight
        JLabel weightLabel = new JLabel("Weight (kg):");
        weightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        weightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(weightLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField weightField = createStyledTextField();
        weightField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(weightField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Blood Pressure
        JLabel bpLabel = new JLabel("Blood Pressure (e.g., 120/80):");
        bpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(bpLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField bpField = createStyledTextField();
        bpField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(bpField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Heart Rate
        JLabel hrLabel = new JLabel("Heart Rate (bpm):");
        hrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(hrLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField hrField = createStyledTextField();
        hrField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(hrField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Temperature
        JLabel tempLabel = new JLabel("Temperature (°C):");
        tempLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(tempLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField tempField = createStyledTextField();
        tempField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(tempField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Blood Sugar
        JLabel bsLabel = new JLabel("Blood Sugar:");
        bsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(bsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextField bsField = createStyledTextField();
        bsField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(bsField);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Buttons
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel2.setBackground(Color.WHITE);
        buttonPanel2.setMaximumSize(new Dimension(500, 50));
        
        JButton saveBtn = createStyledButton("Save Report", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
        
        buttonPanel2.add(saveBtn);
        buttonPanel2.add(cancelBtn);
        contentPanel.add(buttonPanel2);
        
        // Save action
        saveBtn.addActionListener(e -> {
            if (patientCombo.getSelectedIndex() == -1) {
                showError("Please select a patient");
                return;
            }
            
            try {
                String patientSelection = (String) patientCombo.getSelectedItem();
                String patientId = patientSelection.split(" - ")[0];
                Patient patient = clinic.searchPatient(patientId);
                
                double height = Double.parseDouble(heightField.getText().trim());
                double weight = Double.parseDouble(weightField.getText().trim());
                String bloodPressure = bpField.getText().trim();
                int heartRate = Integer.parseInt(hrField.getText().trim());
                double temperature = Double.parseDouble(tempField.getText().trim());
                String bloodSugar = bsField.getText().trim();
                
                // Calculate BMI
                double heightInMeters = height / 100.0;
                double bmi = weight / (heightInMeters * heightInMeters);
                
                // Create health report
                Health_Report report = new Health_Report(patient, height, weight, bloodPressure, bmi, heartRate, temperature, bloodSugar);
                clinic.addHealthReport(report);
                
                loadHealthReports(tableModel);
                showSuccess("Health report created successfully!");
                dialog.dispose();
            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric values");
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JScrollPane scrollPane2 = new JScrollPane(contentPanel);
        scrollPane2.setBorder(null);
        scrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane2);
        dialog.setVisible(true);
    }
    
    private void showMedicalReports() {
        JPanel medicalPanel = new JPanel(new BorderLayout(20, 20));
        medicalPanel.setBackground(Color.WHITE);
        medicalPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Medical Reports & Documents");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_COLOR);
        medicalPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton createReportBtn = createStyledButton("Create Health Report", ACCENT_COLOR);
        JButton createDocBtn = createStyledButton("Create Medical Document", SECONDARY_COLOR);
        JButton viewAllReportsBtn = createStyledButton("View All Health Reports", PRIMARY_COLOR);
        JButton viewAllDocsBtn = createStyledButton("View All Documents", new Color(155, 89, 182));
        JButton backBtn = createStyledButton("← Back", DANGER_COLOR);
        
        buttonPanel.add(createReportBtn);
        buttonPanel.add(createDocBtn);
        buttonPanel.add(viewAllReportsBtn);
        buttonPanel.add(viewAllDocsBtn);
        buttonPanel.add(backBtn);
        
        // Tabbed pane for switching between reports and documents
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Tab 1: Health Reports
        String[] healthReportColumns = {"Report ID", "Patient Name", "Date", "Height (cm)", "Weight (kg)", "BMI", "BP", "HR (bpm)", "Temp (°C)"};
        DefaultTableModel healthReportsModel = new DefaultTableModel(healthReportColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable healthReportsTable = createStyledTable(healthReportsModel);
        JScrollPane healthScrollPane = new JScrollPane(healthReportsTable);
        healthScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tabbedPane.addTab("Health Reports", healthScrollPane);
        loadHealthReports(healthReportsModel);
        
        // Tab 2: Medical Documents
        String[] documentColumns = {"Doc ID", "Patient Name", "Doctor Name", "Date", "Diagnosis", "Treatment"};
        DefaultTableModel documentsModel = new DefaultTableModel(documentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable docsTable = createStyledTable(documentsModel);
        JScrollPane docsScrollPane = new JScrollPane(docsTable);
        docsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        tabbedPane.addTab("Medical Documents", docsScrollPane);
        loadMedicalReports(documentsModel);
        
        // Center panel to hold button panel and table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        
        medicalPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button actions
        createReportBtn.addActionListener(e -> showCreateHealthReportDialog(healthReportsModel));
        createDocBtn.addActionListener(e -> showCreateReportDialog(documentsModel));
        viewAllReportsBtn.addActionListener(e -> loadHealthReports(healthReportsModel));
        viewAllDocsBtn.addActionListener(e -> loadMedicalReports(documentsModel));
        backBtn.addActionListener(e -> showMainDashboard());
        
        mainPanel.removeAll();
        mainPanel.add(medicalPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void loadMedicalReports(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        
        for (Medical_Document doc : clinic.getMedicalDocuments()) {
            String patientName = "Unknown";
            String doctorName = "Unknown";
            
            // Find patient name
            for (Patient patient : clinic.getPatients()) {
                if (patient.getId().equals(String.valueOf(doc.getPatientId()))) {
                    patientName = patient.getName();
                    break;
                }
            }
            
            // Find doctor name
            for (Doctor doctor : clinic.getDoctors()) {
                if (doctor.getId().equals(String.valueOf(doc.getDoctorId()))) {
                    doctorName = doctor.getName();
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{
                doc.getDocId(),
                patientName,
                doctorName,
                dateFormat.format(doc.getDate()),
                doc.getDiagnosis(),
                doc.getTreatment()
            });
        }
    }
    
    private void showCreateReportDialog(DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Create Medical Report", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Create Medical Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Appointment selection
        JLabel aptLabel = new JLabel("Select Appointment:");
        aptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(aptLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JComboBox<String> appointmentCombo = new JComboBox<>();
        appointmentCombo.setMaximumSize(new Dimension(500, 40));
        appointmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Populate appointments
        for (Appointment apt : clinic.getAppointments()) {
            String patientName = apt.getPatient().getName();
            String doctorName = apt.getDoctor().getName();
            
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
            String displayText = String.format("Appt #%s - Dr. %s with %s on %s", 
                apt.getAppointmentId(), doctorName, patientName, df.format(apt.getDate()));
            appointmentCombo.addItem(displayText);
        }
        
        contentPanel.add(appointmentCombo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Symptoms
        JLabel symptomsLabel = new JLabel("Symptoms:");
        symptomsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        symptomsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(symptomsLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextArea symptomsArea = new JTextArea(4, 30);
        symptomsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        symptomsArea.setLineWrap(true);
        symptomsArea.setWrapStyleWord(true);
        symptomsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane symptomsScroll = new JScrollPane(symptomsArea);
        symptomsScroll.setMaximumSize(new Dimension(500, 100));
        symptomsScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(symptomsScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Diagnosis
        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        diagnosisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(diagnosisLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JTextArea diagnosisArea = new JTextArea(4, 30);
        diagnosisArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        diagnosisArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane diagnosisScroll = new JScrollPane(diagnosisArea);
        diagnosisScroll.setMaximumSize(new Dimension(500, 100));
        diagnosisScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(diagnosisScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Treatment
        JLabel treatmentLabel = new JLabel("Treatment:");
        treatmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        treatmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(treatmentLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextArea treatmentArea = new JTextArea(4, 30);
        treatmentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        treatmentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane treatmentScroll = new JScrollPane(treatmentArea);
        treatmentScroll.setMaximumSize(new Dimension(500, 100));
        treatmentScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(treatmentScroll);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(500, 50));
        
        JButton saveBtn = createStyledButton("Save Report", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel);
        
        // Save action
        saveBtn.addActionListener(e -> {
            if (appointmentCombo.getSelectedIndex() == -1) {
                showError("Please select an appointment");
                return;
            }
            
            String symptoms = symptomsArea.getText().trim();
            String diagnosis = diagnosisArea.getText().trim();
            String treatment = treatmentArea.getText().trim();
            
            if (symptoms.isEmpty() || diagnosis.isEmpty() || treatment.isEmpty()) {
                showError("Please fill in all fields");
                return;
            }
            
            // Get selected appointment
            Appointment selectedApt = clinic.getAppointments().get(appointmentCombo.getSelectedIndex());
            
            // Create medical document
            Medical_Document document = new Medical_Document(
                selectedApt.getPatient().getId(),
                new Date(),
                diagnosis,
                treatment,
                symptoms,
                selectedApt.getDoctor().getId(),
                selectedApt.getAppointmentId()
            );
            
            clinic.addMedicalDocument(document);
            selectedApt.setHasCheckup(true);
            loadMedicalReports(tableModel);
            showSuccess("Medical report created successfully!");
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
    private void showChangeStatusDialog() {
        JDialog dialog = new JDialog(this, "Change Appointment Status", true);
        dialog.setLayout(new BorderLayout(20, 20));
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Change Appointment Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Appointment selection
        JLabel aptLabel = new JLabel("Select Appointment:");
        aptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        aptLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(aptLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JComboBox<String> appointmentCombo = new JComboBox<>();
        appointmentCombo.setMaximumSize(new Dimension(450, 40));
        appointmentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        appointmentCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Populate appointments
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        for (Appointment apt : clinic.getAppointments()) {
            String displayText = String.format("#%s - %s with Dr. %s on %s [%s]", 
                apt.getAppointmentId(), 
                apt.getPatient().getName(),
                apt.getDoctor().getName(), 
                df.format(apt.getDate()),
                apt.getStatus());
            appointmentCombo.addItem(displayText);
        }
        
        contentPanel.add(appointmentCombo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Status selection
        JLabel statusLabel = new JLabel("New Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});
        statusCombo.setMaximumSize(new Dimension(450, 40));
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(statusCombo);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(450, 50));
        
        JButton updateBtn = createStyledButton("Update Status", ACCENT_COLOR);
        JButton cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        contentPanel.add(buttonPanel);
        
        // Update action
        updateBtn.addActionListener(e -> {
            if (appointmentCombo.getSelectedIndex() == -1) {
                showError("Please select an appointment");
                return;
            }
            
            Appointment selectedApt = clinic.getAppointments().get(appointmentCombo.getSelectedIndex());
            String newStatus = (String) statusCombo.getSelectedItem();
            
            // Check if trying to mark as completed
            if (newStatus.equals("COMPLETED")) {
                if (!selectedApt.hasCheckup()) {
                    showError("Cannot mark as COMPLETED!\nMedical checkup/report required.\nPlease create a medical report first.");
                    return;
                }
                boolean success = selectedApt.completeAppointment();
                if (success) {
                    showSuccess("Appointment status updated to COMPLETED");
                    dialog.dispose();
                    showAppointmentManagement();
                }
            } else {
                selectedApt.setStatus(newStatus);
                showSuccess("Appointment status updated to " + newStatus);
                dialog.dispose();
                showAppointmentManagement();
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }
    
        private void showRescheduleAppointmentDialog() {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to reschedule:");
            if (appointmentId != null && !appointmentId.trim().isEmpty()) {
                Appointment appointment = null;
                for (Appointment apt : clinic.getAppointments()) {
                    if (apt.getAppointmentId().equals(appointmentId.trim())) {
                        appointment = apt;
                        break;
                    }
                }
            
                if (appointment != null) {
                    final Appointment selectedAppointment = appointment;
                    JDialog dialog = new JDialog(this, "Reschedule Appointment", true);
                    dialog.setSize(500, 400);
                    dialog.setLocationRelativeTo(this);
                
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.setBackground(CARD_BG);
                
                    // Current appointment info
                    JLabel currentLabel = new JLabel("Current: " + selectedAppointment.getDoctor().getName() + " on " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(selectedAppointment.getDate()));
                    currentLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                    panel.add(currentLabel);
                    panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                    // New date selection
                    JComboBox<String> dateCombo = new JComboBox<>();
                    dateCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    dateCombo.setPreferredSize(new Dimension(350, 40));
                    dateCombo.setMaximumSize(new Dimension(350, 40));
                
                    for (String date : selectedAppointment.getDoctor().getAvailableDates()) {
                        dateCombo.addItem(date);
                    }
                
                    if (dateCombo.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(this, "Doctor has no available dates for rescheduling", "No Availability", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                
                    panel.add(createFormField("New Date:", dateCombo));
                    panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                    // New time selection
                    JComboBox<String> timeCombo = new JComboBox<>();
                    timeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    timeCombo.setPreferredSize(new Dimension(350, 40));
                    timeCombo.setMaximumSize(new Dimension(350, 40));
                
                    dateCombo.addActionListener(e -> {
                        timeCombo.removeAllItems();
                        String selectedDate = (String) dateCombo.getSelectedItem();
                        String dateKey = (selectedDate != null && selectedDate.contains(" - ")) ? selectedDate.split(" - ")[1] : selectedDate;
                        ArrayList<String> times = selectedAppointment.getDoctor().getAvailableTimesForDate(dateKey);
                        for (String time : times) {
                            timeCombo.addItem(time);
                        }
                    });
                
                    // Trigger initial population
                    dateCombo.setSelectedIndex(0);
                    // Populate times once for initial selection
                    {
                        String selectedDate = (String) dateCombo.getSelectedItem();
                        String dateKey = (selectedDate != null && selectedDate.contains(" - ")) ? selectedDate.split(" - ")[1] : selectedDate;
                        timeCombo.removeAllItems();
                        ArrayList<String> times = selectedAppointment.getDoctor().getAvailableTimesForDate(dateKey);
                        for (String time : times) {
                            timeCombo.addItem(time);
                        }
                    }
                
                    panel.add(createFormField("New Time:", timeCombo));
                    panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                    // Buttons
                    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                    buttonsPanel.setBackground(CARD_BG);
                
                    JButton rescheduleBtn = createStyledButton("Reschedule", ACCENT_COLOR);
                    JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
                
                    rescheduleBtn.addActionListener(e -> {
                        try {
                            String dateSelection = (String) dateCombo.getSelectedItem();
                            String dateKey = (dateSelection != null && dateSelection.contains(" - ")) ? dateSelection.split(" - ")[1] : dateSelection;
                            String timeSelection = (String) timeCombo.getSelectedItem();
                        
                            if (dateSelection == null || timeSelection == null) {
                                showError("Please select both date and time");
                                return;
                            }
                        
                            // Check if time slot is still available
                            if (!selectedAppointment.getDoctor().isTimeSlotAvailable(dateKey, timeSelection)) {
                                showError("This time slot has just been booked. Please select another time.");
                                return;
                            }
                        
                            String dateStr = dateKey + " " + timeSelection;
                            SimpleDateFormat sdf12 = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                            SimpleDateFormat sdf24 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            Date newDate = sdf12.parse(dateStr);
                            String formattedDate = sdf24.format(newDate);
                            newDate = sdf24.parse(formattedDate);
                        
                            selectedAppointment.setDate(newDate);
                            selectedAppointment.rescheduleAppointment(newDate);
                            selectedAppointment.getDoctor().bookTimeSlot(dateKey, timeSelection);
                        
                            autoSave();
                            showSuccess("Appointment rescheduled successfully!\nNew Date: " + (dateKey == null ? "" : dateKey) + "\nNew Time: " + timeSelection);
                            dialog.dispose();
                            showAppointmentManagement();
                        } catch (Exception ex) {
                            showError("Error rescheduling: " + ex.getMessage());
                        }
                    });
                
                    cancelBtn.addActionListener(e -> dialog.dispose());
                
                    buttonsPanel.add(rescheduleBtn);
                    buttonsPanel.add(cancelBtn);
                    panel.add(buttonsPanel);
                
                    dialog.add(new JScrollPane(panel));
                    dialog.setVisible(true);
                } else {
                    showError("Appointment not found!");
                }
            }
        }
    
        private void showCompleteAppointmentDialog() {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to complete:");
            if (appointmentId != null && !appointmentId.trim().isEmpty()) {
                Appointment appointment = null;
                for (Appointment apt : clinic.getAppointments()) {
                    if (apt.getAppointmentId().equals(appointmentId.trim())) {
                        appointment = apt;
                        break;
                    }
                }
            
                if (appointment != null) {
                    final Appointment selectedAppointment = appointment;
                    JDialog dialog = new JDialog(this, "Complete Appointment", true);
                    dialog.setSize(500, 350);
                    dialog.setLocationRelativeTo(this);
                
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.setBackground(CARD_BG);
                
                    // Appointment info
                    JLabel infoLabel = new JLabel("Appointment: " + selectedAppointment.getPatient().getName() + " with Dr. " + selectedAppointment.getDoctor().getName());
                    infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel.add(infoLabel);
                    panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                    // Checkup checkbox
                    JCheckBox checkupCheckbox = new JCheckBox("Medical Checkup Completed");
                    checkupCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    checkupCheckbox.setBackground(CARD_BG);
                    panel.add(checkupCheckbox);
                    panel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                    // Notes field
                    JLabel notesLabel = new JLabel("Notes:");
                    notesLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    panel.add(notesLabel);
                
                    JTextArea notesArea = new JTextArea(5, 30);
                    notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    notesArea.setLineWrap(true);
                    notesArea.setWrapStyleWord(true);
                    JScrollPane scrollPane = new JScrollPane(notesArea);
                    panel.add(scrollPane);
                    panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                    // Buttons
                    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                    buttonsPanel.setBackground(CARD_BG);
                
                    JButton completeBtn = createStyledButton("Complete", ACCENT_COLOR);
                    JButton cancelBtn = createStyledButton("Cancel", SECONDARY_COLOR);
                
                    completeBtn.addActionListener(e -> {
                        if (!checkupCheckbox.isSelected()) {
                            showError("Medical checkup must be marked as completed!");
                            return;
                        }
                    
                        selectedAppointment.setHasCheckup(true);
                        if (selectedAppointment.completeAppointment()) {
                            String notes = notesArea.getText().trim();
                            if (!notes.isEmpty()) {
                                selectedAppointment.getPatient().addMedicalRecord(notes);
                            }
                            autoSave();
                            showSuccess("Appointment completed successfully!");
                            dialog.dispose();
                            showAppointmentManagement();
                        } else {
                            showError("Error completing appointment");
                        }
                    });
                
                    cancelBtn.addActionListener(e -> dialog.dispose());
                
                    buttonsPanel.add(completeBtn);
                    buttonsPanel.add(cancelBtn);
                    panel.add(buttonsPanel);
                
                    dialog.add(panel);
                    dialog.setVisible(true);
                } else {
                    showError("Appointment not found!");
                }
            }
        }
    
        private void showAppointmentDetailsDialog() {
            String appointmentId = JOptionPane.showInputDialog(this, "Enter Appointment ID to view details:");
            if (appointmentId != null && !appointmentId.trim().isEmpty()) {
                Appointment appointment = null;
                for (Appointment apt : clinic.getAppointments()) {
                    if (apt.getAppointmentId().equals(appointmentId.trim())) {
                        appointment = apt;
                        break;
                    }
                }
            
                if (appointment != null) {
                    JDialog dialog = new JDialog(this, "Appointment Details", true);
                    dialog.setSize(600, 450);
                    dialog.setLocationRelativeTo(this);
                
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    panel.setBackground(CARD_BG);
                
                    // Details section
                    JPanel detailsPanel = new JPanel();
                    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
                    detailsPanel.setBackground(CARD_BG);
                    detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Appointment Information"),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
                    addDetailRow(detailsPanel, "Appointment ID:", appointment.getAppointmentId());
                    addDetailRow(detailsPanel, "Patient:", appointment.getPatient().getName() + " (ID: " + appointment.getPatient().getId() + ")");
                    addDetailRow(detailsPanel, "Doctor:", "Dr. " + appointment.getDoctor().getName() + " (ID: " + appointment.getDoctor().getId() + ")");
                    addDetailRow(detailsPanel, "Specialization:", appointment.getDoctor().getSpecialization());
                    addDetailRow(detailsPanel, "Date & Time:", df.format(appointment.getDate()));
                    addDetailRow(detailsPanel, "Reason:", appointment.getReason());
                    addDetailRow(detailsPanel, "Status:", appointment.getStatus());
                    addDetailRow(detailsPanel, "Checkup Completed:", appointment.hasCheckup() ? "Yes" : "No");
                
                    panel.add(detailsPanel);
                    panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                    // Full details text area
                    JTextArea detailsArea = new JTextArea(appointment.getAppointmentDetails());
                    detailsArea.setFont(new Font("Courier New", Font.PLAIN, 11));
                    detailsArea.setEditable(false);
                    detailsArea.setLineWrap(true);
                    detailsArea.setWrapStyleWord(true);
                    detailsArea.setBackground(new Color(245, 245, 245));
                
                    JScrollPane scrollPane = new JScrollPane(detailsArea);
                    scrollPane.setPreferredSize(new Dimension(550, 120));
                    panel.add(scrollPane);
                    panel.add(Box.createRigidArea(new Dimension(0, 20)));
                
                    // Close button
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                    buttonPanel.setBackground(CARD_BG);
                    JButton closeBtn = createStyledButton("Close", SECONDARY_COLOR);
                    closeBtn.addActionListener(e -> dialog.dispose());
                    buttonPanel.add(closeBtn);
                    panel.add(buttonPanel);
                
                    dialog.add(new JScrollPane(panel));
                    dialog.setVisible(true);
                } else {
                    showError("Appointment not found!");
                }
            }
        }
    
    private void showHealthReportDetailsDialog(Health_Report report, DefaultTableModel tableModel) {
        JDialog dialog = new JDialog(this, "Health Report Details", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Report #" + report.getReportId() + " - " + (report.getPatient() != null ? report.getPatient().getName() : "Unknown"));
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 8));
        grid.setBackground(CARD_BG);
        grid.add(new JLabel("Date:")); grid.add(new JLabel(new java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a").format(report.getReportDate())));
        grid.add(new JLabel("Height (cm):")); grid.add(new JLabel(String.valueOf(report.getHeight())));
        grid.add(new JLabel("Weight (kg):")); grid.add(new JLabel(String.valueOf(report.getWeight())));
        grid.add(new JLabel("BMI:")); grid.add(new JLabel(String.format("%.2f", report.getBmi())));
        grid.add(new JLabel("Blood Pressure:")); grid.add(new JLabel(report.getBloodPressure()));
        grid.add(new JLabel("Heart Rate:")); grid.add(new JLabel(report.getHeartRate() + " bpm"));
        grid.add(new JLabel("Temperature:")); grid.add(new JLabel(report.getTemperature() + "°C"));
        grid.add(new JLabel("Blood Sugar:")); grid.add(new JLabel(report.getBloodSugar()));
        grid.add(new JLabel("Condition:"));
        JTextField conditionField = createStyledTextField();
        conditionField.setText(report.getGeneralCondition());
        grid.add(conditionField);
        panel.add(grid);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel notesLbl = new JLabel("Add Note:");
        notesLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(notesLbl);
        JTextArea noteArea = new JTextArea(4, 40);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane noteSp = new JScrollPane(noteArea);
        noteSp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(noteSp);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel trendsLbl = new JLabel("Trends/Analysis:");
        trendsLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(trendsLbl);
        JTextArea trendsArea = new JTextArea(8, 40);
        trendsArea.setEditable(false);
        trendsArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane trendsSp = new JScrollPane(trendsArea);
        panel.add(trendsSp);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setBackground(CARD_BG);
        JButton analyzeBtn = createStyledButton("Analyze", PRIMARY_COLOR);
        JButton saveNoteBtn = createStyledButton("Add Note", ACCENT_COLOR);
        JButton saveCondBtn = createStyledButton("Save Condition", new Color(26, 188, 156));
        JButton closeBtn = createStyledButton("Close", SECONDARY_COLOR);
        btns.add(analyzeBtn);
        btns.add(saveNoteBtn);
        btns.add(saveCondBtn);
        btns.add(closeBtn);
        panel.add(btns);

        analyzeBtn.addActionListener(e -> {
            trendsArea.setText(buildTrendAnalysis(report));
        });

        saveNoteBtn.addActionListener(e -> {
            String note = noteArea.getText().trim();
            if (note.isEmpty()) { showError("Enter a note."); return; }
            report.addHealthNote(note);
            autoSave();
            showSuccess("Note added.");
            noteArea.setText("");
        });

        saveCondBtn.addActionListener(e -> {
            String cond = conditionField.getText().trim();
            if (cond.isEmpty()) { showError("Enter a condition."); return; }
            report.setGeneralCondition(cond);
            autoSave();
            loadHealthReports(tableModel);
            showSuccess("Condition updated.");
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private String buildTrendAnalysis(Health_Report r) {
        StringBuilder sb = new StringBuilder();
        double bmi = r.getBmi();
        if (bmi < 18.5) sb.append("BMI: Underweight (" + String.format("%.2f", bmi) + ")\n");
        else if (bmi < 25) sb.append("BMI: Normal (" + String.format("%.2f", bmi) + ")\n");
        else if (bmi < 30) sb.append("BMI: Overweight (" + String.format("%.2f", bmi) + ")\n");
        else sb.append("BMI: Obese (" + String.format("%.2f", bmi) + ")\n");

        int hr = r.getHeartRate();
        if (hr < 60) sb.append("Heart Rate: Low (" + hr + " bpm)\n");
        else if (hr <= 100) sb.append("Heart Rate: Normal (" + hr + " bpm)\n");
        else sb.append("Heart Rate: High (" + hr + " bpm)\n");

        double t = r.getTemperature();
        if (t < 36.5) sb.append("Temp: Low (" + t + "°C)\n");
        else if (t <= 37.5) sb.append("Temp: Normal (" + t + "°C)\n");
        else if (t <= 38.5) sb.append("Temp: Slight Fever (" + t + "°C)\n");
        else sb.append("Temp: High Fever (" + t + "°C)\n");

        sb.append("Blood Pressure: ").append(r.getBloodPressure()).append('\n');
        sb.append("Blood Sugar: ").append(r.getBloodSugar()).append('\n');
        sb.append("Condition: ").append(r.getGeneralCondition()).append('\n');
        return sb.toString();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        // Create custom success dialog with animation
        JDialog dialog = new JDialog(this, "Success", true);
        dialog.setLayout(new BorderLayout());
        dialog.setUndecorated(true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ACCENT_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Create a custom loading spinner panel
        JPanel spinnerPanel = new JPanel() {
            private int angle = 0;
            private Timer timer;
            
            {
                setPreferredSize(new Dimension(60, 60));
                setMaximumSize(new Dimension(60, 60));
                setOpaque(false);
                timer = new Timer(50, e -> {
                    angle = (angle + 15) % 360;
                    repaint();
                });
                timer.start();
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int radius = 20;
                
                // Draw rotating arc (loading arrow)
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.rotate(Math.toRadians(angle), centerX, centerY);
                g2d.drawArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, 300);
                
                // Draw arrow head
                int arrowX = (int) (centerX + radius * Math.cos(Math.toRadians(300)));
                int arrowY = (int) (centerY + radius * Math.sin(Math.toRadians(300)));
                int[] xPoints = {arrowX, arrowX - 8, arrowX - 5};
                int[] yPoints = {arrowY, arrowY - 3, arrowY + 5};
                g2d.fillPolygon(xPoints, yPoints, 3);
                
                g2d.dispose();
            }
        };
        spinnerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(spinnerPanel);
        
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        JLabel messageLabel = new JLabel("<html><center>" + message.replace("\n", "<br>") + "</center></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        
        // Auto-close after 2 seconds
        Timer closeTimer = new Timer(2000, ev -> {
            dialog.dispose();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
        
        dialog.setVisible(true);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSearchClinicDialog() {
        JDialog dialog = new JDialog(this, "Search Clinic by ID", true);
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(CARD_BG);
        
        JLabel titleLabel = new JLabel("Enter Clinic ID");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JTextField idField = createStyledTextField();
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(idField);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(CARD_BG);
        
        JButton searchBtn = createStyledButton("Login to Clinic", ACCENT_COLOR);
        searchBtn.addActionListener(e -> {
            String searchId = idField.getText().trim();
            if (searchId.isEmpty()) {
                showError("Please enter a Clinic ID");
                return;
            }
            
            // Search for clinic by ID
            boolean found = false;
            for (int i = 0; i < clinics.size(); i++) {
                if (clinics.get(i).getClinicId().equalsIgnoreCase(searchId)) {
                    clinic = clinics.get(i);
                    found = true;
                    dialog.dispose();
                    showSuccess("Logged in to: " + clinic.getName());
                    showMainDashboard();
                    break;
                }
            }
            
            if (!found) {
                showError("Clinic ID not found: " + searchId);
            }
        });
        
        JButton cancelBtn = createStyledButton("Cancel", DANGER_COLOR);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        // Enable Enter key to submit
        addEnterKeyListener(idField, searchBtn);
        
        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    // ==================== DATA PERSISTENCE METHODS ====================
    
    /**
     * Save all clinics to file
     */
    private void saveClinics() {
        try {
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                    new java.io.FileOutputStream(CLINICS_DATA_FILE))) {
                oos.writeObject(clinics);
                System.out.println("All clinics saved successfully! (" + clinics.size() + " clinics)");
            }
        } catch (java.io.IOException e) {
            System.err.println("Error saving clinics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load clinics from file
     */
    @SuppressWarnings("unchecked")
    private ArrayList<Clinic> loadClinics() {
        java.io.File file = new java.io.File(CLINICS_DATA_FILE);
        if (!file.exists()) {
            System.out.println("No saved clinic data found. Starting fresh.");
            return new ArrayList<>();
        }
        
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(CLINICS_DATA_FILE))) {
            ArrayList<Clinic> loadedClinics = (ArrayList<Clinic>) ois.readObject();
            System.out.println("Clinics loaded successfully! (" + loadedClinics.size() + " clinics)");
            return loadedClinics;
        } catch (java.io.IOException | ClassNotFoundException e) {
            System.err.println("Error loading clinics: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Save only the currently active clinic. Adds it to the clinics list if missing.
     */
    public void saveCurrentClinic() {
        if (clinic == null) {
            showError("No clinic is currently loaded to save.");
            return;
        }
        if (!clinics.contains(clinic)) {
            clinics.add(clinic);
        }
        saveClinics();
        showSuccess("Clinic saved successfully.");
    }
    
    /**
     * Auto-save after important operations
     */
    private void autoSave() {
        if (clinic != null && clinics.contains(clinic)) {
            saveClinics();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ClinicGUI());
    }
}
