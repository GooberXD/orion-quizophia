package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import Utility.FontUtil;
import Utility.ResourceLoader;

public class TeacherDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JButton createQuizButton;
    private JButton deleteQuizButton;
    private JButton logoutButton;

    private JTable scoresTable;
    private DefaultTableModel tableModel;

    private JTable rankTable;
    private DefaultTableModel rankModel;
    private JButton refreshRankBtn;

    public TeacherDashboardView() {
        setTitle("Flippio - Teacher Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Window Icon
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon == null) appIcon = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (appIcon != null) setIconImage(appIcon.getImage());

        // 1. Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.decode("#9146FF"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Teacher!");
        welcomeLabel.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        welcomeLabel.setForeground(Color.WHITE);
        logoutButton = new JButton("Logout");
        logoutButton.setFont(FontUtil.montserrat(12f, Font.BOLD, logoutButton.getFont()));

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FontUtil.montserrat(12f, Font.PLAIN, tabbedPane.getFont()));

        // Tab A: Student Scores
        JPanel scoresPanel = new JPanel(new BorderLayout());
        String[] columns = {"Student ID", "Quiz Name", "Score", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        scoresTable = new JTable(tableModel);
        scoresTable.setFont(FontUtil.montserrat(12f, Font.PLAIN, scoresTable.getFont()));
        scoresTable.setRowHeight(22);
        scoresTable.getTableHeader().setFont(FontUtil.montserrat(12f, Font.BOLD, scoresTable.getTableHeader().getFont()));
        scoresPanel.add(new JScrollPane(scoresTable), BorderLayout.CENTER);
        tabbedPane.addTab("Student Performance", scoresPanel);

        // Tab B: Manage Quizzes
        JPanel quizPanel = new JPanel(new FlowLayout());
        createQuizButton = new JButton("Create New Quiz");
        createQuizButton.setFont(FontUtil.montserrat(14f, Font.BOLD, createQuizButton.getFont()));
        createQuizButton.setPreferredSize(new Dimension(200, 50));

        deleteQuizButton = new JButton("Delete a Quiz");
        deleteQuizButton.setFont(FontUtil.montserrat(14f, Font.BOLD, createQuizButton.getFont()));
        deleteQuizButton.setPreferredSize(new Dimension(200, 50));
        deleteQuizButton.setBackground(new Color(200, 50, 50));
        deleteQuizButton.setForeground(Color.WHITE);

        quizPanel.add(createQuizButton);
        quizPanel.add(Box.createHorizontalStrut(20));
        quizPanel.add(deleteQuizButton);
        tabbedPane.addTab("Manage Quizzes", quizPanel);

        // Tab C: Ranking System
        JPanel rankingPanel = new JPanel(new BorderLayout());
        String[] rankCols = {"Rank", "Student Name", "Average Score"};
        rankModel = new DefaultTableModel(rankCols, 0);
        rankTable = new JTable(rankModel);
        rankTable.setFont(FontUtil.montserrat(12f, Font.PLAIN, rankTable.getFont()));
        rankTable.setRowHeight(22);
        rankTable.getTableHeader().setFont(FontUtil.montserrat(12f, Font.BOLD, rankTable.getTableHeader().getFont()));
        rankingPanel.add(new JScrollPane(rankTable), BorderLayout.CENTER);

        refreshRankBtn = new JButton("Calculate Rankings");
        refreshRankBtn.setFont(FontUtil.montserrat(12f, Font.BOLD, refreshRankBtn.getFont()));
        rankingPanel.add(refreshRankBtn, BorderLayout.SOUTH);

        tabbedPane.addTab("Ranking System", rankingPanel);

        add(tabbedPane, BorderLayout.CENTER);
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    public void setTeacherName(String name) { welcomeLabel.setText("Hi, Teacher " + name + "!"); }
    public void addScoreRow(String id, String quiz, double score, int total, String status) {
        tableModel.addRow(new Object[]{id, quiz, score, total, status});
    }

    // NEW: Method to clear table before reloading
    public void clearScoreTable() {
        tableModel.setRowCount(0);
    }

    public void addRankingRow(int rank, String name, double avg) {
        rankModel.addRow(new Object[]{rank, name, String.format("%.2f", avg)});
    }
    public void clearRankings() { rankModel.setRowCount(0); }

    public void addRefreshRankListener(ActionListener listener) { refreshRankBtn.addActionListener(listener); }
    public void addLogoutListener(ActionListener listener) { logoutButton.addActionListener(listener); }
    public void addCreateQuizListener(ActionListener listener) { createQuizButton.addActionListener(listener); }
    public void addDeleteQuizListener(ActionListener listener) { deleteQuizButton.addActionListener(listener); }
}