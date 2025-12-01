package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StudentDashboardView extends JFrame {
    // Components
    private JLabel welcomeLabel;
    private JLabel avgScoreLabel;
    private JPanel quizButtonPanel; // Panel to hold dynamic quiz buttons
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton logoutButton;

    public StudentDashboardView() {
        // 1. Window Setup
        setTitle("Flippio - Student Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // 2. TOP PANEL: User Info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#9146FF")); // Flippio Purple
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Student!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new JButton("Logout");

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 3. CENTER PANEL: Tabs for "Home" and "Performance"
        JTabbedPane tabbedPane = new JTabbedPane();

        // --- TAB 1: AVAILABLE QUIZZES (Matches Page 10) ---
        JPanel homePanel = new JPanel(new BorderLayout());
        JLabel selectLabel = new JLabel("Select a Subject / Quiz to Start:");
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        homePanel.add(selectLabel, BorderLayout.NORTH);

        quizButtonPanel = new JPanel();
        quizButtonPanel.setLayout(new GridLayout(0, 2, 10, 10)); // Grid for big buttons
        quizButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // (Buttons will be added dynamically by the Controller)
        homePanel.add(new JScrollPane(quizButtonPanel), BorderLayout.CENTER);

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

    // --- METHODS FOR CONTROLLER TO UPDATE UI ---

    public void setStudentName(String name) {
        welcomeLabel.setText("Hi, Student " + name + "!");
    }

    public void updateAverageScore(double avg) {
        avgScoreLabel.setText(String.format("Average Performance: %.2f%%", avg));
    }

    // Adds a quiz button dynamically
    public void addQuizButton(String quizName, ActionListener action) {
        JButton btn = new JButton(quizName);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        // Style allows the button to look like the cards in your prototype
        btn.setPreferredSize(new Dimension(150, 100));
        quizButtonPanel.add(btn);
        quizButtonPanel.revalidate();
        quizButtonPanel.repaint();
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
