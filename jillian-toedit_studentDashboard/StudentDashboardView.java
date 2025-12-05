package View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

import Utility.FontUtil;
import Utility.ResourceLoader;
import java.lang.Math;

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
        // Set background to white
        getContentPane().setBackground(Color.WHITE);

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

        RoundedButton logBtn = new RoundedButton("Logout");
        logBtn.setBackground(Color.decode("#c8abed"));
        logBtn.setForeground(Color.BLACK);
        logBtn.setBorderThickness(2);
        logoutButton = logBtn;

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 3. CENTER PANEL: Tabs for "Home" and "Performance"
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FontUtil.montserrat(12f, Font.PLAIN, tabbedPane.getFont()));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        // --- TAB 1: AVAILABLE QUIZZES ---
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        JLabel selectLabel = new JLabel("Select a Subject / Quiz to Start:");
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectLabel.setFont(FontUtil.montserrat(14f, Font.BOLD, selectLabel.getFont()));
        selectLabel.setForeground(Color.BLACK);
        homePanel.add(selectLabel, BorderLayout.NORTH);

        quizButtonPanel = new JPanel();
        quizButtonPanel.setLayout(new GridLayout(0, 2, 20, 20));
        quizButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        quizButtonPanel.setBackground(Color.WHITE);

        homePanel.add(createStyledScrollPane(quizButtonPanel), BorderLayout.CENTER);

        tabbedPane.addTab("Home / Quizzes", homePanel);

        // --- TAB 2: PERFORMANCE ---
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);

        // Average Score Display & Download Button
        JPanel headerPanel = new JPanel(new BorderLayout()); // Changed to BorderLayout for spacing
        headerPanel.setBackground(Color.WHITE);

        avgScoreLabel = new JLabel("Average Performance: 0%");
        avgScoreLabel.setFont(FontUtil.montserrat(16f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));
        avgScoreLabel.setForeground(Color.BLACK);

        // NEW: Download Button
        downloadReportBtn = new RoundedButton("Download Grade Report");
        downloadReportBtn.setBackground(Color.decode("#4CAF50"));
        downloadReportBtn.setForeground(Color.WHITE);

        headerPanel.add(avgScoreLabel, BorderLayout.WEST);
        headerPanel.add(downloadReportBtn, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statsPanel.add(headerPanel, BorderLayout.NORTH);

        // Results Table - MODIFIED: Added "Total Score" column
        String[] columnNames = {"Quiz Name", "Score", "Total Score", "Status"}; // MODIFIED
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        // Apply Montserrat to table and header
        resultTable.setFont(FontUtil.montserrat(12f, Font.PLAIN, resultTable.getFont()));
        resultTable.setRowHeight(22);
        resultTable.setBackground(Color.WHITE);
        resultTable.setForeground(Color.BLACK);

        resultTable.setGridColor(new Color(240, 240, 240)); // Light gray grid lines
        resultTable.getTableHeader().setFont(FontUtil.montserrat(12f, Font.BOLD, resultTable.getTableHeader().getFont()));
        resultTable.getTableHeader().setBackground(Color.decode("#9146FF")); // Purple header
        resultTable.getTableHeader().setForeground(Color.WHITE);

        // Apply custom renderer for Status column (column index 3)
        resultTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer()); // ADDED

        statsPanel.add(createStyledScrollPane(resultTable), BorderLayout.CENTER);

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
        btn.setForeground(Color.BLACK);
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

    // MODIFIED: Added totalScore parameter
    public void addResultRow(String quizName, double score, double totalScore, String status) {
        tableModel.addRow(new Object[]{quizName, score, totalScore, status});
    }

    public void addLogoutListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // NEW Listener
    public void addDownloadListener(ActionListener listener) {
        downloadReportBtn.addActionListener(listener);
    }

    // --- Custom Cell Renderer for Status Column ---
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 3) { // Status column
                if ("PASSED".equals(value)) {
                    c.setForeground(new Color(0, 128, 0)); // Dark Green
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("FAILED".equals(value)) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                }
            }
            return c;
        }
    }

    // --- Custom Scroll Pane Creation ---

    private JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        // Remove the border that was causing the box line
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);

        // Customize vertical scroll bar
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());
        verticalScrollBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        verticalScrollBar.setUnitIncrement(16);
        verticalScrollBar.setOpaque(false);
        verticalScrollBar.setBackground(Color.WHITE);

        // Customize horizontal scroll bar
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setUI(new CustomScrollBarUI());
        horizontalScrollBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
        horizontalScrollBar.setUnitIncrement(16);
        horizontalScrollBar.setOpaque(false);
        horizontalScrollBar.setBackground(Color.WHITE);

        // Set viewport properties
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(Color.WHITE);

        return scrollPane;
    }

    // --- Custom ScrollBar UI Class ---

    class CustomScrollBarUI extends BasicScrollBarUI {
        private Color trackColor = Color.WHITE;
        private Color thumbColor = Color.decode("#9146FF");
        private Color thumbHoverColor = Color.decode("#7A3CD9");
        private boolean isHovered = false;

        public CustomScrollBarUI() {
            super();
        }

        @Override
        protected void configureScrollBarColors() {
            this.trackColor = Color.WHITE;
            this.thumbColor = Color.decode("#9146FF");
            this.thumbHoverColor = Color.decode("#7A3CD9");
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setBackground(Color.WHITE);
            return button;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setBackground(Color.WHITE);
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentThumbColor = isHovered ? thumbHoverColor : thumbColor;
            g2.setColor(currentThumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6);
        }

        @Override
        protected void installListeners() {
            super.installListeners();

            // Add hover effect listeners
            scrollbar.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    scrollbar.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    scrollbar.repaint();
                }
            });
        }
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
            setBackground(Color.decode("#c8abed"));
            setForeground(Color.BLACK);
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
            g2.fillRoundRect(0, 0, Math.abs(getWidth() - borderThickness), Math.abs(getHeight() - borderThickness), radius, radius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(borderThickness));
            g2.setColor(Color.black);
            g2.drawRoundRect(1, 1, Math.abs(getWidth() - borderThickness - 1), Math.abs(getHeight() - borderThickness - 1), radius, radius);
            g2.dispose();
        }
    }
}