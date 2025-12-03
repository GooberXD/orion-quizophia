package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class TeacherDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JButton createQuizButton;
    private JButton logoutButton;

    // Components for the Student Scores Table
    private JTable scoresTable;
    private DefaultTableModel tableModel;

    // NEW: Components for Ranking System
    private JTable rankTable;
    private DefaultTableModel rankModel;
    private JButton refreshRankBtn;

    public TeacherDashboardView() {
        setTitle("Flippio - Teacher Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // 1. Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#9146FF")); // Branding color
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Teacher!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        logoutButton = new JButton("Logout");

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab A: Student Scores
        JPanel scoresPanel = new JPanel(new BorderLayout());
        String[] columns = {"Student ID", "Quiz Name", "Score", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        scoresTable = new JTable(tableModel);
        scoresPanel.add(new JScrollPane(scoresTable), BorderLayout.CENTER);
        tabbedPane.addTab("Student Performance", scoresPanel);

        // Tab B: Manage Quizzes
        JPanel quizPanel = new JPanel(new FlowLayout());
        createQuizButton = new JButton("Create New Quiz");
        createQuizButton.setPreferredSize(new Dimension(200, 50));
        quizPanel.add(createQuizButton);
        tabbedPane.addTab("Manage Quizzes", quizPanel);

        // NEW: Tab C: Ranking System (Ranking Exception Context)
        JPanel rankingPanel = new JPanel(new BorderLayout());
        String[] rankCols = {"Rank", "Student Name", "Average Score"};
        rankModel = new DefaultTableModel(rankCols, 0);
        rankTable = new JTable(rankModel);
        rankingPanel.add(new JScrollPane(rankTable), BorderLayout.CENTER);

        refreshRankBtn = new JButton("Calculate Rankings");
        rankingPanel.add(refreshRankBtn, BorderLayout.SOUTH);

        tabbedPane.addTab("Ranking System", rankingPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    // Setters for Controller
    public void setTeacherName(String name) {
        welcomeLabel.setText("Hi, Teacher " + name + "!");
    }

    public void addScoreRow(String id, String quiz, double score, int total, String status) {
        tableModel.addRow(new Object[]{id, quiz, score, total, status});
    }

    // NEW: Ranking Methods
    public void addRankingRow(int rank, String name, double avg) {
        rankModel.addRow(new Object[]{rank, name, String.format("%.2f", avg)});
    }
    public void clearRankings() { rankModel.setRowCount(0); }
    public void addRefreshRankListener(ActionListener listener) { refreshRankBtn.addActionListener(listener); }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void addCreateQuizListener(ActionListener listener) {
        createQuizButton.addActionListener(listener);
    }
}