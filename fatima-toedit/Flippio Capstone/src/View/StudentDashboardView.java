package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import Utility.FontUtil;
import Utility.ResourceLoader;

public class StudentDashboardView extends JFrame {
    // Components
    private JLabel welcomeLabel;
    private JLabel avgScoreLabel;
    private JPanel quizButtonPanel; // Panel to hold dynamic quiz buttons
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton logoutButton;
    private JButton downloadReportBtn; // NEW

    public StudentDashboardView() {
        // 1. Window Setup
        setTitle("Flippio - Student Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        // Try to set window icon from Resources (PNG/JPG recommended)
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (appIcon == null) appIcon = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (appIcon != null) {
            setIconImage(appIcon.getImage());
        }

        // 2. TOP PANEL: User Info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#9146FF")); // Flippio Purple
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Student!");
        // Use Montserrat if available; fallback to Arial
        welcomeLabel.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 3. CENTER PANEL: Tabs for "Home" and "Performance"
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FontUtil.montserrat(12f, Font.PLAIN, tabbedPane.getFont()));

        // --- TAB 1: AVAILABLE QUIZZES ---
        JPanel homePanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("Select a Subject / Quiz to Start:");
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectLabel.setFont(FontUtil.montserrat(14f, Font.BOLD, selectLabel.getFont()));
        homePanel.add(selectLabel, BorderLayout.NORTH);

        quizButtonPanel = new JPanel();
        quizButtonPanel.setLayout(new GridLayout(0, 2, 10, 10));
        quizButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        homePanel.add(new JScrollPane(quizButtonPanel), BorderLayout.CENTER);
        tabbedPane.addTab("Home / Quizzes", homePanel);

        // --- TAB 2: PERFORMANCE ---
        JPanel statsPanel = new JPanel(new BorderLayout());

        // Average Score Display & Download Button
        JPanel headerPanel = new JPanel(new BorderLayout()); // Changed to BorderLayout for spacing

        avgScoreLabel = new JLabel("Average Performance: 0%");
        avgScoreLabel.setFont(FontUtil.montserrat(16f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));

        // NEW: Download Button
        downloadReportBtn = new JButton("Download Grade Report ⬇");
        downloadReportBtn.setBackground(Color.decode("#4CAF50")); // Green
        downloadReportBtn.setForeground(Color.WHITE);

        headerPanel.add(avgScoreLabel, BorderLayout.WEST);
        headerPanel.add(downloadReportBtn, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statsPanel.add(headerPanel, BorderLayout.NORTH);

        // Results Table
        String[] columnNames = {"Quiz Name", "Score", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        // Apply Montserrat to table and header
        resultTable.setFont(FontUtil.montserrat(12f, Font.PLAIN, resultTable.getFont()));
        resultTable.setRowHeight(22);
        resultTable.getTableHeader().setFont(FontUtil.montserrat(12f, Font.BOLD, resultTable.getTableHeader().getFont()));

        statsPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        tabbedPane.addTab("My Performance", statsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Global font application for remaining components (safe)
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    // --- METHODS FOR CONTROLLER TO UPDATE UI ---

    public void setStudentName(String name) {
        welcomeLabel.setText("Hi, Student " + name + "!");
    }

    public void updateAverageScore(double avg) {
        avgScoreLabel.setText(String.format("Average Performance: %.2f%%", avg));
    }

    public void addQuizButton(String quizName, ActionListener action) {
        RoundedButton btn = new RoundedButton(quizName);
        btn.setRadius(25);
        btn.setBorderThickness(3);
        btn.setBackground(Color.decode("#c8abed"));
        btn.setFont(FontUtil.montserrat(14f, Font.BOLD, new Font("Arial", Font.BOLD, 14)));
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        btn.setPreferredSize(new Dimension(150, 100));
        quizButtonPanel.add(btn);
        quizButtonPanel.revalidate();
        quizButtonPanel.repaint();
    }

    public void clearQuizButtons() {
        quizButtonPanel.removeAll();
        quizButtonPanel.revalidate();
        quizButtonPanel.repaint();
    }

    public void addResultRow(String quizName, double score, String status) {
        tableModel.addRow(new Object[]{quizName, score, status});
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // NEW Listener
    public void addDownloadListener(ActionListener listener) {
        downloadReportBtn.addActionListener(listener);
    }

    // --- Rounded Button implementation (used by quiz buttons) ---
    class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2;

        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        public void setRadius(int radius) {
            this.radius = radius;
            repaint();
        }

        public void setBorderThickness(int thickness) {
            this.borderThickness = thickness;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(Color.black);
            g2.drawRoundRect(0, 0, getWidth() - borderThickness, getHeight() - borderThickness, radius, radius);
            g2.dispose();
        }
    }

    // Note: Font loading centralized in Utility.FontUtil
}