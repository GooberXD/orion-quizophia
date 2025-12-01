package View;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

public class StudentDashboardView extends JFrame {
    // Components
    private JLabel welcomeLabel;
    private JLabel avgScoreLabel;
    private JPanel quizButtonPanel; // Panel to hold dynamic quiz buttons
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton logoutButton;

    public StudentDashboardView() throws IOException, FontFormatException {
        // 1. Window Setup
        setTitle("Flippio - Student Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // 2. TOP PANEL: User Info
        JPanel topPanel = new JPanel(new BorderLayout());
        //topPanel.setBackground(Color.decode("#9146FF")); // Flippio Purple
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Student!");
        welcomeLabel.setFont(getCustomFont(25.0f));
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 15, 40));
        welcomeLabel.setForeground(Color.black);

        logoutButton = new JButton("Logout");

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 3. CENTER PANEL: Tabs for "Home" and "Performance"
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(getCustomFont(12.0f));

        // --- TAB 1: AVAILABLE QUIZZES (Matches Page 10) ---
        JPanel homePanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("Select a Subject / Quiz to Start:");
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 10));
        selectLabel.setFont(getCustomFont(16.0f));
        homePanel.add(selectLabel, BorderLayout.NORTH);

        quizButtonPanel = new JPanel();
        quizButtonPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Grid for big buttons
        quizButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // (Buttons will be added dynamically by the Controller)
        homePanel.add(new JScrollPane(quizButtonPanel), BorderLayout.CENTER);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tabbedPane.addTab("Home / Quizzes", homePanel);

        // --- TAB 2: PERFORMANCE (Matches Page 11) ---
        JPanel statsPanel = new JPanel(new BorderLayout());

        // Average Score Display
        JPanel scorePanel = new JPanel();
        avgScoreLabel = new JLabel("Average Performance: 0%");
        avgScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        scorePanel.add(avgScoreLabel);
        statsPanel.add(scorePanel, BorderLayout.NORTH);

        // Results Table
        String[] columnNames = {"Quiz Name", "Score", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);

        statsPanel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        tabbedPane.addTab("My Performance", statsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private Font getCustomFont(float size) throws IOException, FontFormatException{
        java.io.InputStream is = getClass().getResourceAsStream("rsc/Montserrat-Bold.ttf");
        return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
    }

    // --- METHODS FOR CONTROLLER TO UPDATE UI ---

    public void setStudentName(String name) {
        welcomeLabel.setText("Hi, Student " + name + "!");
    }

    public void updateAverageScore(double avg) {
        avgScoreLabel.setText(String.format("Average Performance: %.2f%%", avg));
    }

    // Adds a quiz button dynamically
    public void addQuizButton(String quizName, ActionListener action) throws IOException, FontFormatException {
        RoundedButton btn = new RoundedButton(quizName);
        btn.setRadius(25); // Optional: adjust roundness
        btn.setBorderThickness(3); // Adjust border thickness here (e.g., 5 pixels)

        btn.setBackground(Color.decode("#c8abed"));
        btn.setFont(getCustomFont(16.0f));
        btn.setFocusPainted(false);
        btn.addActionListener(action);

        btn.setPreferredSize(new Dimension(140, 90));
        quizButtonPanel.add(btn);

        quizButtonPanel.revalidate();
        quizButtonPanel.repaint();
    }


    // --- CUSTOM ROUNDED BUTTON CLASS ---
    class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2; // Thickness of the border

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

            // Anti-aliasing for smooth edges
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Draw text
            super.paintComponent(g);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Set the border thickness
            g2.setStroke(new BasicStroke(borderThickness)); // Set the thickness here

            g2.setColor(Color.black); // Border color
            g2.drawRoundRect(0, 0, getWidth()-borderThickness, getHeight()-borderThickness, radius, radius);

            g2.dispose();
        }
    }



    // Clears buttons (useful when refreshing)
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
}
