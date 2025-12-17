package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import Utility.FontUtil;
import Utility.ResourceLoader;
import Model.Student;

public class TeacherDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JButton createQuizButton;
    private JButton deleteQuizButton;
    private JButton logoutButton;

    // Removed Student Performance table in favor of popup performance view

    private JTable rankTable;
    private DefaultTableModel rankModel;
    private JButton refreshRankBtn;
    private JButton backButton;
    // Removed JList-based quiz list in favor of button panel
    private JTable studentTable;
    private DefaultTableModel studentTableModel;
    private JButton addStudentBtn;
    private JButton deleteStudentBtn;

    private JPanel quizButtonsPanel; // container for quiz buttons
    private JScrollPane quizScroll; // scroll viewport for width calculations
    private JPanel quizContainer; // container to control top gap under tabs
    private JTabbedPane tabbedPane;
    private JLabel logoLabel;
    private Image appLogoImage;
    private final double BASE_W = 1280.0;
    private final double BASE_H = 720.0;

    public TeacherDashboardView() {
        setTitle("Flippio - Teacher Dashboard");
        // Flexible fullscreen across displays
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Window Icon: app icon should be Logo.png
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon != null) { setIconImage(appIcon.getImage()); }

        // 1. Top Panel
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.decode("#1A171E"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Teacher!");
        welcomeLabel.setFont(FontUtil.montserrat(34f, Font.BOLD, new Font("Arial", Font.BOLD, 34)));
        welcomeLabel.setForeground(Color.WHITE);
        backButton = new RoundedButton("Back");
        backButton.setBackground(Color.decode("#1A171E"));
        backButton.setForeground(Color.WHITE);
        ((RoundedButton)backButton).setBorderColor(Color.decode("#9146FF"));
        backButton.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        ((RoundedButton)backButton).setRadius(22);
        ((RoundedButton)backButton).setBorderThickness(3);
        backButton.setPreferredSize(new Dimension(140, 48));

        logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(Color.decode("#1A171E"));
        logoutButton.setForeground(Color.WHITE);
        ((RoundedButton)logoutButton).setBorderColor(Color.decode("#9146FF"));
        logoutButton.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        ((RoundedButton)logoutButton).setRadius(22);
        ((RoundedButton)logoutButton).setBorderThickness(3);
        logoutButton.setPreferredSize(new Dimension(160, 48));

        logoLabel = new JLabel();
        ImageIcon logoIcon = ResourceLoader.loadImageIcon("Flippio_Logo.png");
        if (logoIcon != null) {
            Image scaled = logoIcon.getImage().getScaledInstance(200, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.insets = new Insets(0,0,0,0); gbc.fill = GridBagConstraints.NONE;

        // Left label
        gbc.gridx = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(welcomeLabel, gbc);
        // Center logo
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(logoLabel, gbc);
        // Right buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(backButton);
        rightButtons.add(logoutButton);
        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(rightButtons, gbc);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FontUtil.montserrat(18f, Font.PLAIN, tabbedPane.getFont()));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        // Tab A: Subjects
        // Subjects tab removed as requested
        // The entire block of code for the Subjects tab has been removed.

        // Removed Student Performance tab; performance now shown in quiz modal

        // Tab B: Manage Quizzes
        JPanel quizPanel = new JPanel(new BorderLayout());
        quizButtonsPanel = new JPanel();
        // GridBagLayout allows us to control how many tiles per row
        quizButtonsPanel.setLayout(new GridBagLayout());
        quizButtonsPanel.setBackground(Color.WHITE);
        quizContainer = new JPanel(new BorderLayout());
        quizContainer.setBackground(Color.WHITE);
        // Start tiles ~20px below tabs
        // Tighten lower padding slightly to reduce perceived empty space
        quizContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
        // Place grid at NORTH so it doesn't consume extra vertical space
        quizContainer.add(quizButtonsPanel, BorderLayout.NORTH);
        quizScroll = createStyledScrollPane(quizContainer);
        // Enforce vertical-only scrolling; disable horizontal scrolling
        quizScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        quizScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        quizPanel.add(quizScroll, BorderLayout.CENTER);

        JPanel quizActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        createQuizButton = new RoundedButton("Create New Quiz");
        createQuizButton.setBackground(new Color(76, 175, 80)); // green
        createQuizButton.setForeground(Color.WHITE);
        createQuizButton.setFont(FontUtil.montserrat(18f, Font.BOLD, createQuizButton.getFont()));
        ((RoundedButton)createQuizButton).setRadius(22);
        ((RoundedButton)createQuizButton).setBorderThickness(3);
        createQuizButton.setPreferredSize(new Dimension(220, 48));

        deleteQuizButton = new RoundedButton("Delete a Quiz");
        deleteQuizButton.setBackground(new Color(200, 50, 50));
        deleteQuizButton.setForeground(Color.WHITE);
        deleteQuizButton.setFont(FontUtil.montserrat(18f, Font.BOLD, createQuizButton.getFont()));
        ((RoundedButton)deleteQuizButton).setRadius(22);
        ((RoundedButton)deleteQuizButton).setBorderThickness(3);
        deleteQuizButton.setPreferredSize(new Dimension(220, 48));

        quizActions.add(createQuizButton);
        quizActions.add(deleteQuizButton);
        quizPanel.add(quizActions, BorderLayout.SOUTH);
        tabbedPane.addTab("Manage Quizzes", quizPanel);

        // Tab C: Ranking System
        JPanel rankingPanel = new JPanel(new BorderLayout());
        rankingPanel.setBackground(Color.WHITE);
        String[] rankCols = {"Rank", "Student Name", "Average Score"};
        rankModel = new DefaultTableModel(rankCols, 0);
        rankTable = new JTable(rankModel);
        // Increase base font sizes
        rankTable.setFont(FontUtil.montserrat(20f, Font.PLAIN, rankTable.getFont()));
        rankTable.setRowHeight(32);
        rankTable.setBackground(Color.WHITE);
        rankTable.setForeground(Color.BLACK);
        rankTable.setGridColor(new Color(240,240,240));
        JTableHeader rankHeader = rankTable.getTableHeader();
        rankHeader.setFont(FontUtil.montserrat(20f, Font.BOLD, rankHeader.getFont()));
        rankHeader.setBackground(Color.decode("#9146FF"));
        rankHeader.setForeground(Color.WHITE);
        // Center alignment for all columns
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        rankTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        rankTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        rankTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        // Add horizontal padding on both ends of the table
        JScrollPane rankScroll = createStyledScrollPane(rankTable);
        JPanel rankPaddedCenter = new JPanel(new BorderLayout());
        rankPaddedCenter.setOpaque(false);
        // Add vertical space above the table header and horizontal padding on both sides
        rankPaddedCenter.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));
        rankPaddedCenter.add(rankScroll, BorderLayout.CENTER);
        rankingPanel.add(rankPaddedCenter, BorderLayout.CENTER);

        refreshRankBtn = new RoundedButton("Calculate Rankings");
        refreshRankBtn.setFont(FontUtil.montserrat(22f, Font.BOLD, refreshRankBtn.getFont()));
        refreshRankBtn.setBackground(new Color(76, 175, 80));
        refreshRankBtn.setForeground(Color.WHITE);
        // Rounded corners to match Add Student style
        if (refreshRankBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) refreshRankBtn;
            rb.setRadius(22);
            rb.setBorderThickness(3);
            // Increase width
            refreshRankBtn.setPreferredSize(new Dimension(300, 48));
        }
        JPanel rankSouth = new JPanel(new BorderLayout());
        rankSouth.setOpaque(false);
        rankSouth.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        JPanel rankSouthInner = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rankSouthInner.setOpaque(false);
        rankSouthInner.add(refreshRankBtn);
        rankSouth.add(rankSouthInner, BorderLayout.CENTER);
        rankingPanel.add(rankSouth, BorderLayout.SOUTH);

        tabbedPane.addTab("Ranking System", rankingPanel);

        // Tab D: Students (fixed initialization order)
        JPanel studentsPanel = new JPanel(new BorderLayout());
        studentsPanel.setBackground(Color.WHITE);
        studentTableModel = new DefaultTableModel(new String[]{"ID", "Name"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        studentTable = new JTable(studentTableModel);
        studentTable.setFont(FontUtil.montserrat(20f, Font.PLAIN, studentTable.getFont()));
        studentTable.setRowHeight(30);
        studentTable.setBackground(Color.WHITE);
        studentTable.setForeground(Color.BLACK);
        studentTable.setGridColor(new Color(240,240,240));
        JTableHeader studentHeader = studentTable.getTableHeader();
        studentHeader.setFont(FontUtil.montserrat(20f, Font.BOLD, studentHeader.getFont()));
        studentHeader.setBackground(Color.decode("#9146FF"));
        studentHeader.setForeground(Color.WHITE);
        // Center align all columns
        DefaultTableCellRenderer studentCenterRenderer = new DefaultTableCellRenderer();
        studentCenterRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 2; i++) {
            studentTable.getColumnModel().getColumn(i).setCellRenderer(studentCenterRenderer);
        }
        JScrollPane studentScroll = createStyledScrollPane(studentTable);
        JPanel paddedCenter = new JPanel(new BorderLayout());
        paddedCenter.setOpaque(false);
        paddedCenter.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        paddedCenter.add(studentScroll, BorderLayout.CENTER);
        studentsPanel.add(paddedCenter, BorderLayout.CENTER);
        addStudentBtn = new RoundedButton("Add Student");
        addStudentBtn.setBackground(new Color(76, 175, 80));
        addStudentBtn.setForeground(Color.WHITE);
        addStudentBtn.setFont(FontUtil.montserrat(20f, Font.BOLD, addStudentBtn.getFont()));
        deleteStudentBtn = new RoundedButton("Delete Student");
        deleteStudentBtn.setBackground(new Color(200, 50, 50));
        deleteStudentBtn.setForeground(Color.WHITE);
        deleteStudentBtn.setFont(FontUtil.montserrat(20f, Font.BOLD, deleteStudentBtn.getFont()));
        JPanel studentsActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        studentsActions.setOpaque(false);
        studentsActions.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        studentsActions.add(addStudentBtn);
        studentsActions.add(deleteStudentBtn);
        studentsPanel.add(studentsActions, BorderLayout.SOUTH);
        tabbedPane.addTab("Students", studentsPanel);

        add(tabbedPane, BorderLayout.CENTER);
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));

        applyResponsiveScaling();
        addComponentListener(new java.awt.event.ComponentAdapter(){
            @Override public void componentResized(java.awt.event.ComponentEvent e){ applyResponsiveScaling(); }
        });
    }

    public void setTeacherName(String name) { welcomeLabel.setText("Hi, " + name + "!"); }
    // Quiz buttons management
    public void clearQuizButtons() {
        quizButtonsPanel.removeAll();
        quizButtonsPanel.revalidate();
        quizButtonsPanel.repaint();
    }
    public void addQuizButton(String quizName, ActionListener listener) {
        RoundedButton btn = new RoundedButton(quizName);
        btn.setBackground(Color.decode("#c8abed"));
        btn.setFont(FontUtil.montserrat(14f, Font.BOLD, new Font("Arial", Font.BOLD, 14)));
        btn.setFocusPainted(false);
        btn.setForeground(Color.BLACK);
        // Base preferred size; width increased for better readability
        // Restore larger default tile size
        btn.setPreferredSize(new Dimension(220, 150));
        btn.addActionListener(listener);

        quizButtonsPanel.add(btn);
        // Relayout to respect current column count
        relayoutQuizButtons(getCurrentQuizCols());
        quizButtonsPanel.revalidate();
        quizButtonsPanel.repaint();
    }

    // Compute dynamic column count based on window width and tile width
    private int getCurrentQuizCols() {
        // Target 4 tiles per row; width calculation will shrink to fit
        return 4;
    }

    // Apply GridBag constraints to lay out buttons into specified columns
    private void relayoutQuizButtons(int cols) {
        // Only lay out actual quiz tiles; ignore previous placeholders/fillers
        java.util.List<Component> tiles = new java.util.ArrayList<>();
        for (Component c : quizButtonsPanel.getComponents()) {
            if (c instanceof RoundedButton) { tiles.add(c); }
        }
        quizButtonsPanel.removeAll();
        for (int i = 0; i < tiles.size(); i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = i % cols;
            gbc.gridy = i / cols;
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.anchor = GridBagConstraints.NORTHWEST; // keep items aligned to a 3-column grid
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            quizButtonsPanel.add(tiles.get(i), gbc);
        }
        // If the last row is not full, add invisible placeholders to complete the row
        int remainder = tiles.size() % cols;
        if (remainder != 0) {
            int lastRow = tiles.size() / cols;
            for (int c = remainder; c < cols; c++) {
                GridBagConstraints ph = new GridBagConstraints();
                ph.gridx = c;
                ph.gridy = lastRow;
                ph.insets = new Insets(6, 6, 6, 6);
                ph.anchor = GridBagConstraints.NORTHWEST;
                ph.fill = GridBagConstraints.NONE;
                ph.weightx = 0;
                // lightweight spacer to occupy the grid cell
                quizButtonsPanel.add(Box.createHorizontalStrut(0), ph);
            }
        }
        // Add filler to consume remaining vertical space and keep tiles up
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = (int)Math.ceil(tiles.size() / (double)cols) + 1;
        filler.weighty = 1.0;
        filler.gridwidth = cols;
        filler.fill = GridBagConstraints.VERTICAL;
        quizButtonsPanel.add(Box.createVerticalGlue(), filler);
    }

    public void addRankingRow(int rank, String name, double avg) {
        rankModel.addRow(new Object[]{rank, name, String.format("%.2f", avg)});
    }
    public void clearRankings() { rankModel.setRowCount(0); }

    public void addRefreshRankListener(ActionListener listener) { refreshRankBtn.addActionListener(listener); }
    public void addLogoutListener(ActionListener listener) { logoutButton.addActionListener(listener); }
    public void addCreateQuizListener(ActionListener listener) { createQuizButton.addActionListener(listener); }
    public void addDeleteQuizListener(ActionListener listener) { deleteQuizButton.addActionListener(listener); }
    public void addBackListener(ActionListener listener) { backButton.addActionListener(listener); }
        public void setQuizList(java.util.List<String> names) {
            clearQuizButtons();
            for (String n : names) { addQuizButton(n, e -> {}); }
        }
    // removed old JList-based setter

    public void addAddStudentToSubjectListener(ActionListener listener) { addStudentBtn.addActionListener(listener); }
    public void addDeleteStudentFromSubjectListener(ActionListener listener) { deleteStudentBtn.addActionListener(listener); }

    public void setStudents(List<Student> students) {
        studentTableModel.setRowCount(0);
        for (Student s : students) {
            studentTableModel.addRow(new Object[]{s.getIdNumber(), s.getName()});
        }
    }

    private JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        JScrollBar v = scrollPane.getVerticalScrollBar();
        v.setUI(new CustomScrollBarUI());
        v.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        v.setUnitIncrement(16);
        v.setOpaque(false);
        v.setBackground(Color.WHITE);
        JScrollBar h = scrollPane.getHorizontalScrollBar();
        h.setUI(new CustomScrollBarUI());
        h.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
        h.setUnitIncrement(16);
        h.setOpaque(false);
        h.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setOpaque(true);
        return scrollPane;
    }

    private void applyResponsiveScaling() {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth() > 0 ? getWidth() : scr.width;
        int h = getHeight() > 0 ? getHeight() : scr.height;
        double scale = Math.min(w / BASE_W, h / BASE_H);
        scale = Math.max(0.75, Math.min(scale, 2.5));

        float nameSize = (float)Math.round(30 * scale);
        welcomeLabel.setFont(FontUtil.montserrat(nameSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)nameSize)));

        float tabSize = (float)Math.round(18 * scale);
        if (tabbedPane != null) {
            tabbedPane.setFont(FontUtil.montserrat(tabSize, Font.PLAIN,
                    new Font("Arial", Font.PLAIN, (int)tabSize)));
        }

        // Header buttons
        float hbSize = (float)Math.round(20 * scale);
        if (backButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) backButton;
            backButton.setFont(FontUtil.montserrat(hbSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)hbSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            backButton.setPreferredSize(new Dimension((int)Math.round(140 * scale), (int)Math.round(44 * scale)));
        }
        if (logoutButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) logoutButton;
            logoutButton.setFont(FontUtil.montserrat(hbSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)hbSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            logoutButton.setPreferredSize(new Dimension((int)Math.round(160 * scale), (int)Math.round(44 * scale)));
        }

        // Quiz action buttons
        float qaSize = (float)Math.round(18 * scale);
        if (createQuizButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) createQuizButton;
            createQuizButton.setFont(FontUtil.montserrat(qaSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)qaSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            createQuizButton.setPreferredSize(new Dimension((int)Math.round(220 * scale), (int)Math.round(48 * scale)));
        }
        if (deleteQuizButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) deleteQuizButton;
            deleteQuizButton.setFont(FontUtil.montserrat(qaSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)qaSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            deleteQuizButton.setPreferredSize(new Dimension((int)Math.round(220 * scale), (int)Math.round(48 * scale)));
        }

        // Quiz tiles: keep compact sizing and relayout to 4-per-row adaptively
        if (quizButtonsPanel != null) {
            float tileSize = (float)Math.round(16 * scale);
            int panelW;
            if (quizScroll != null && quizScroll.getViewport() != null) {
                panelW = quizScroll.getViewport().getExtentSize().width;
                if (panelW <= 0) panelW = quizScroll.getWidth();
            } else {
                panelW = getWidth() > 0 ? getWidth() : (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            }
            int horizPadding = 40; // container border left+right
            int gap = 12; // inter-tile gap
            int cols = getCurrentQuizCols();
            // Compute width to fit exactly cols per row with gaps and padding
            int available = Math.max(300, panelW - horizPadding);
            // Prefer teacher-sized tiles (~220px wide) while honoring 4 columns
            int computed = (available - (cols - 1) * gap) / cols;
            int tileWidth = Math.max(220, computed);
            int tileHeight = (int)Math.round(150 * scale);
            for (Component c : quizButtonsPanel.getComponents()) {
                if (c instanceof RoundedButton) {
                    RoundedButton b = (RoundedButton)c;
                    b.setFont(FontUtil.montserrat(tileSize, Font.BOLD,
                            new Font("Arial", Font.BOLD, (int)tileSize)));
                    b.setRadius((int)Math.max(20, Math.round(24 * scale)));
                    b.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
                    b.setPreferredSize(new Dimension(tileWidth, tileHeight));
                }
            }
            relayoutQuizButtons(cols);
        }

        // Ranking table
        if (rankTable != null) {
            float cellSize = (float)Math.round(20 * scale);
            rankTable.setFont(FontUtil.montserrat(cellSize, Font.PLAIN,
                new Font("Arial", Font.PLAIN, (int)cellSize)));
            rankTable.setRowHeight((int)Math.round(32 * scale));
            JTableHeader header = rankTable.getTableHeader();
            if (header != null) {
            float headSize = (float)Math.round(20 * scale);
            header.setFont(FontUtil.montserrat(headSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)headSize)));
            }
        }

        // Calculate Rankings button scaling
        if (refreshRankBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) refreshRankBtn;
            float crSize = (float)Math.round(22 * scale);
            refreshRankBtn.setFont(FontUtil.montserrat(crSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)crSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            // Increased scaled width
            refreshRankBtn.setPreferredSize(new Dimension((int)Math.round(300 * scale), (int)Math.round(48 * scale)));
        }

        // Students table and action buttons
        if (studentTable != null) {
            float cellSize = (float)Math.round(20 * scale);
            studentTable.setFont(FontUtil.montserrat(cellSize, Font.PLAIN,
                    new Font("Arial", Font.PLAIN, (int)cellSize)));
            studentTable.setRowHeight((int)Math.round(30 * scale));
            JTableHeader header = studentTable.getTableHeader();
            if (header != null) {
                float headSize = (float)Math.round(20 * scale);
                header.setFont(FontUtil.montserrat(headSize, Font.BOLD,
                        new Font("Arial", Font.BOLD, (int)headSize)));
            }
        }
        float saSize = (float)Math.round(20 * scale);
        if (addStudentBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) addStudentBtn;
            addStudentBtn.setFont(FontUtil.montserrat(saSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)saSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            addStudentBtn.setPreferredSize(new Dimension((int)Math.round(220 * scale), (int)Math.round(48 * scale)));
        }
        if (deleteStudentBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) deleteStudentBtn;
            deleteStudentBtn.setFont(FontUtil.montserrat(saSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)saSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            deleteStudentBtn.setPreferredSize(new Dimension((int)Math.round(220 * scale), (int)Math.round(48 * scale)));
        }

        // Dynamic logo scaling (base 200x80)
        int logoW = (int)Math.round(200 * scale);
        int logoH = (int)Math.round(80 * scale);
        if (appLogoImage != null && logoLabel != null) {
            Image scaled = appLogoImage.getScaledInstance(logoW, logoH, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
            logoLabel.setText(null);
        }

        // Keep ~20px space under tabs across resizes
        if (quizContainer != null) {
            quizContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 12, 20));
            quizContainer.revalidate();
        }
    }

    // Expose a safe way to refresh sizing from controllers after dynamic content changes
    public void refreshLayoutSizing() {
        // Defer to end of event queue to ensure layout sizes (like viewport) are up to date
        javax.swing.SwingUtilities.invokeLater(this::applyResponsiveScaling);
    }

    class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        private Color trackColor = Color.WHITE;
        private Color thumbColor = Color.decode("#9146FF");
        private Color thumbHoverColor = Color.decode("#7A3CD9");
        private boolean isHovered = false;
        @Override protected void configureScrollBarColors() { trackColor = Color.WHITE; thumbColor = Color.decode("#9146FF"); thumbHoverColor = Color.decode("#7A3CD9"); }
        @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setBackground(Color.WHITE); return b; }
        @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setBackground(Color.WHITE); return b; }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(trackColor); g2.fillRect(r.x,r.y,r.width,r.height); }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) { Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(isHovered?thumbHoverColor:thumbColor); g2.fillRoundRect(r.x,r.y,r.width,r.height,6,6); }
        @Override protected void installListeners() { super.installListeners(); scrollbar.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){ isHovered=true; scrollbar.repaint(); } public void mouseExited(java.awt.event.MouseEvent e){ isHovered=false; scrollbar.repaint(); }}); }
    }

    class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2;
        private Color borderColor = Color.BLACK;
        public RoundedButton(String text){ super(text); setOpaque(false); setFocusPainted(false); setBorderPainted(false); setBackground(Color.decode("#c8abed")); setForeground(Color.BLACK);}        
        public void setRadius(int r){ radius=r; repaint(); }
        public void setBorderThickness(int t){ borderThickness=t; repaint(); }
        public void setBorderColor(Color c){ this.borderColor = c; repaint(); }
        @Override protected void paintComponent(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillRoundRect(0,0,Math.abs(getWidth()-borderThickness),Math.abs(getHeight()-borderThickness),radius,radius); super.paintComponent(g); g2.dispose(); }
        @Override protected void paintBorder(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setStroke(new BasicStroke(borderThickness)); g2.setColor(borderColor); g2.drawRoundRect(1,1,Math.abs(getWidth()-borderThickness-1),Math.abs(getHeight()-borderThickness-1),radius,radius); g2.dispose(); }
    }
}