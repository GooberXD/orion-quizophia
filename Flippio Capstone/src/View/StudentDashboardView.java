package View;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
// removed unused geom import

import Utility.FontUtil;
import Utility.ResourceLoader;

public class StudentDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JLabel avgScoreLabel;
    private JPanel quizButtonPanel;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;
    private JButton logoutButton;
    private JButton downloadReportBtn;
    private JPanel topPanel;
    private JLabel logoLabel;
    // removed appLogoImage; center logo uses Prototype Design asset directly
    private final double BASE_W = 1280.0;
    private final double BASE_H = 720.0;
    private JTabbedPane tabbedPane;
    private JLabel selectLabel;
    // Card navigation between Subjects and Dashboard
    private CardLayout mainCards;
    private JPanel mainCardContainer;
    private JPanel subjectPanelRef;
    private JPanel dashboardPanelRef;
    private JButton dashboardBackButton;

    public StudentDashboardView() {
        setTitle("Flippio - Student Dashboard");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon != null) { setIconImage(appIcon.getImage()); }

        topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.decode("#1A171E"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        welcomeLabel = new JLabel("Hi, Student!");
        welcomeLabel.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        welcomeLabel.setForeground(Color.WHITE);

        logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(Color.decode("#1A171E"));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        if (logoutButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) logoutButton;
            rb.setRadius(22);
            rb.setBorderThickness(3);
            rb.setBorderColor(Color.decode("#9146FF"));
        }
        logoutButton.setPreferredSize(new Dimension(140, 48));

        // Back button styled like teacher header back
        dashboardBackButton = new RoundedButton("Back");
        dashboardBackButton.setBackground(Color.decode("#1A171E"));
        dashboardBackButton.setForeground(Color.WHITE);
        dashboardBackButton.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        if (dashboardBackButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) dashboardBackButton;
            rb.setRadius(22);
            rb.setBorderThickness(3);
            rb.setBorderColor(Color.decode("#9146FF"));
        }
        dashboardBackButton.setPreferredSize(new Dimension(140, 48));

        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // In-panel logo should be Flippio_Logo
        ImageIcon centerLogo = ResourceLoader.loadImageIcon("Flippio_Logo.png");
        if (centerLogo != null) {
            Image scaled = centerLogo.getImage().getScaledInstance(200, 80, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.insets = new Insets(0,0,0,0); gbc.fill = GridBagConstraints.NONE;

        // Left label
        gbc.gridx = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(welcomeLabel, gbc);

        // Center logo (fixed dimension like teacher dashboard)
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(logoLabel, gbc);

        // Right button(s)
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(dashboardBackButton);
        rightButtons.add(logoutButton);
        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(rightButtons, gbc);
        add(topPanel, BorderLayout.NORTH);

        // Initial responsive scaling and on-resize updates
        applyResponsiveScaling();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveScaling();
            }
        });

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FontUtil.montserrat(16f, Font.PLAIN, tabbedPane.getFont()));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.BLACK);

        // Subjects tab (table layout similar to teacher subjects)
        JPanel subjectPanel = new JPanel(new BorderLayout());
        subjectPanel.setBackground(Color.WHITE);
        subjectTableModel = new DefaultTableModel(new String[]{"Subject ID", "Subject Name", "Quizzes Available"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        subjectTable = new JTable(subjectTableModel);
        subjectTable.setFont(FontUtil.montserrat(18f, Font.PLAIN, subjectTable.getFont()));
        subjectTable.setRowHeight(28);
        subjectTable.setBackground(Color.WHITE);
        subjectTable.setForeground(Color.BLACK);
        subjectTable.setGridColor(new Color(240, 240, 240));
        JTableHeader subjHeader = subjectTable.getTableHeader();
        subjHeader.setFont(FontUtil.montserrat(18f, Font.BOLD, subjHeader.getFont()));
        subjHeader.setBackground(Color.decode("#c8abed"));
        subjHeader.setForeground(Color.BLACK);
        // Center align all columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        subjectTable.getColumnModel().getColumn(0).setCellRenderer(center);
        subjectTable.getColumnModel().getColumn(1).setCellRenderer(center);
        subjectTable.getColumnModel().getColumn(2).setCellRenderer(center);
        JScrollPane subjectScroll = createStyledScrollPane(subjectTable);
        JPanel subjectCenterPad = new JPanel(new BorderLayout());
        subjectCenterPad.setOpaque(false);
        subjectCenterPad.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        subjectCenterPad.add(subjectScroll, BorderLayout.CENTER);
        subjectPanel.add(subjectCenterPad, BorderLayout.CENTER);
        tabbedPane.addTab("My Subjects", subjectPanel);

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(Color.WHITE);
        selectLabel = new JLabel("Select a Subject / Quiz to Start:");
        selectLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        selectLabel.setFont(FontUtil.montserrat(20f, Font.BOLD, selectLabel.getFont()));
        selectLabel.setForeground(Color.BLACK);
        homePanel.add(selectLabel, BorderLayout.NORTH);

        // Fixed 4 columns layout
        quizButtonPanel = new JPanel(new GridLayout(0, 4, 16, 16));
        quizButtonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        quizButtonPanel.setBackground(Color.WHITE);
        // Wrap with a scroll pane; only vertical scrolling should be noticeable
        JScrollPane quizScroll = createStyledScrollPane(quizButtonPanel);
        homePanel.add(quizScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Home / Quizzes", homePanel);

        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        avgScoreLabel = new JLabel("Average Performance: 0%");
        avgScoreLabel.setFont(FontUtil.montserrat(22f, Font.BOLD, new Font("Arial", Font.BOLD, 22)));
        avgScoreLabel.setForeground(Color.BLACK);
        downloadReportBtn = new RoundedButton("Download Grade Report");
        downloadReportBtn.setBackground(Color.decode("#4CAF50"));
        downloadReportBtn.setForeground(Color.WHITE);
        downloadReportBtn.setFont(FontUtil.montserrat(18f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));
        headerPanel.add(avgScoreLabel, BorderLayout.WEST);
        headerPanel.add(downloadReportBtn, BorderLayout.EAST);
        // Add extra horizontal padding on both ends
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        statsPanel.add(headerPanel, BorderLayout.NORTH);

        String[] columnNames = {"Quiz Name", "Score", "Total Score", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(FontUtil.montserrat(16f, Font.PLAIN, resultTable.getFont()));
        resultTable.setRowHeight(30);
        resultTable.setBackground(Color.WHITE);
        resultTable.setForeground(Color.BLACK);
        resultTable.setGridColor(new Color(240, 240, 240));
        resultTable.getTableHeader().setFont(FontUtil.montserrat(16f, Font.BOLD, resultTable.getTableHeader().getFont()));
        resultTable.getTableHeader().setBackground(Color.decode("#9146FF"));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 3; i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        resultTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());
        // Wrap the table with a padded panel to add horizontal space on both ends
        JPanel centerPad = new JPanel(new BorderLayout());
        centerPad.setOpaque(false);
        centerPad.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        centerPad.add(createStyledScrollPane(resultTable), BorderLayout.CENTER);
        statsPanel.add(centerPad, BorderLayout.CENTER);
        tabbedPane.addTab("My Performance", statsPanel);

        // Replace tabbed layout with a CardLayout switch between Subjects and Dashboard
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.add(tabbedPane, BorderLayout.CENTER);

        // Persist references and card container for controller methods
        this.subjectPanelRef = subjectPanel;
        this.dashboardPanelRef = dashboardPanel;
        this.mainCards = new CardLayout();
        this.mainCardContainer = new JPanel(mainCards);
        mainCardContainer.setBackground(Color.WHITE);
        mainCardContainer.add(subjectPanelRef, "SUBJECTS");
        mainCardContainer.add(dashboardPanelRef, "DASHBOARD");
        add(mainCardContainer, BorderLayout.CENTER);
        // Show Subjects by default on open
        mainCards.show(mainCardContainer, "SUBJECTS");
        dashboardBackButton.setVisible(false);

        // Removed header navigation buttons (Subjects/Dashboard) per request
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    private void applyResponsiveScaling() {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth() > 0 ? getWidth() : scr.width;
        int h = getHeight() > 0 ? getHeight() : scr.height;
        double scale = Math.min(w / BASE_W, h / BASE_H);
        scale = Math.max(0.75, Math.min(scale, 2.0));

        float nameSize = (float)Math.round(26 * scale);
        welcomeLabel.setFont(FontUtil.montserrat(nameSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)nameSize)));

        float btnSize = (float)Math.round(20 * scale);
        logoutButton.setFont(FontUtil.montserrat(btnSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)btnSize)));
        if (logoutButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) logoutButton;
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            rb.setPreferredSize(new Dimension((int)Math.round(140 * scale), (int)Math.round(44 * scale)));
        }

        // Scale dashboard back button similar to teacher style
        if (dashboardBackButton != null) {
            dashboardBackButton.setFont(FontUtil.montserrat(btnSize, Font.BOLD,
                    new Font("Arial", Font.BOLD, (int)btnSize)));
            if (dashboardBackButton instanceof RoundedButton) {
                RoundedButton rb = (RoundedButton) dashboardBackButton;
                rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
                rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
                dashboardBackButton.setPreferredSize(new Dimension((int)Math.round(140 * scale), (int)Math.round(44 * scale)));
            }
        }

        // Scale tabs and section label (null-safe if called before init)
        float tabSize = (float)Math.round(16 * scale);
        if (tabbedPane != null) {
            tabbedPane.setFont(FontUtil.montserrat(tabSize, Font.PLAIN,
                new Font("Arial", Font.PLAIN, (int)tabSize)));
        }

        float sectionSize = (float)Math.round(20 * scale);
        if (selectLabel != null) {
            selectLabel.setFont(FontUtil.montserrat(sectionSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)sectionSize)));
        }

        float dlSize = (float)Math.round(18 * scale);
        if (downloadReportBtn != null) {
            downloadReportBtn.setFont(FontUtil.montserrat(dlSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)dlSize)));
        }

        // Scale average score label
        if (avgScoreLabel != null) {
            float avgSize = (float)Math.round(22 * scale);
            avgScoreLabel.setFont(FontUtil.montserrat(avgSize, Font.BOLD,
                new Font("Arial", Font.BOLD, (int)avgSize)));
        }

        // Scale table fonts and header
        if (resultTable != null) {
            float cellSize = (float)Math.round(16 * scale);
            resultTable.setFont(FontUtil.montserrat(cellSize, Font.PLAIN,
                new Font("Arial", Font.PLAIN, (int)cellSize)));
            resultTable.setRowHeight((int)Math.round(30 * scale));
            JTableHeader header = resultTable.getTableHeader();
            if (header != null) {
                float headSize = (float)Math.round(16 * scale);
                header.setFont(FontUtil.montserrat(headSize, Font.BOLD,
                    new Font("Arial", Font.BOLD, (int)headSize)));
            }
        }

        // Subject table scaling
        if (subjectTable != null) {
            float cellSize = (float)Math.round(18 * scale);
            subjectTable.setFont(FontUtil.montserrat(cellSize, Font.PLAIN,
                new Font("Arial", Font.PLAIN, (int)cellSize)));
            subjectTable.setRowHeight((int)Math.round(30 * scale));
            JTableHeader sh = subjectTable.getTableHeader();
            if (sh != null) {
                float headSize = (float)Math.round(18 * scale);
                sh.setFont(FontUtil.montserrat(headSize, Font.BOLD,
                    new Font("Arial", Font.BOLD, (int)headSize)));
            }
        }

        // Scale quiz buttons similar to Teacher dashboard for a fixed 4-column layout
        if (quizButtonPanel != null) {
            float quizBtnSize = (float)Math.round(20 * scale);

            // Determine available width from the scroll viewport or panel
            int hgap = 16, cols = 4;
            int paddingLeft = 20, paddingRight = 20;
            int availableWidth;
            Container parent = quizButtonPanel.getParent();
            if (parent instanceof JViewport) {
                availableWidth = ((JViewport) parent).getExtentSize().width;
            } else {
                availableWidth = quizButtonPanel.getWidth();
                if (availableWidth <= 0) availableWidth = getWidth();
            }
            int contentWidth = Math.max(availableWidth - paddingLeft - paddingRight, 400);
            // Target teacher-like tile size while honoring 4 columns
            int targetWidth = (int)Math.round(220 * scale);
            int computedWidth = Math.max(160, (contentWidth - (hgap * (cols - 1))) / cols);
            int tileWidth = Math.max(160, Math.min(targetWidth, computedWidth));
            // Make tile height ~4x shorter than typical teacher dashboard
            // Use a slim aspect ratio ~0.175 of width and a small minimum
            int tileHeight = Math.max(50, (int)Math.round(tileWidth * 0.175));

            for (Component c : quizButtonPanel.getComponents()) {
                if (c instanceof RoundedButton) {
                    RoundedButton b = (RoundedButton)c;
                    b.setFont(FontUtil.montserrat(quizBtnSize, Font.BOLD,
                        new Font("Arial", Font.BOLD, (int)quizBtnSize)));
                    b.setRadius((int)Math.max(18, Math.round(22 * scale)));
                    b.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
                    // Dynamic tile size based on viewport width and fixed 4 columns
                    b.setPreferredSize(new Dimension(tileWidth, tileHeight));
                }
            }
        }

        // Keep center logo size consistent (200x80). If missing, show text.
        int logoW = 200;
        int logoH = 80;
        if (logoLabel.getIcon() != null) {
            Icon ic = logoLabel.getIcon();
            if (ic instanceof ImageIcon) {
                Image scaled = ((ImageIcon) ic).getImage().getScaledInstance(logoW, logoH, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaled));
                logoLabel.setText(null);
            }
        } else {
            logoLabel.setIcon(null);
            logoLabel.setText("Flippio");
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setFont(FontUtil.montserrat((float)Math.round(16 * scale), Font.BOLD,
                    new Font("Arial", Font.BOLD, (int)Math.round(16 * scale))));
        }

        topPanel.revalidate();
        topPanel.repaint();
    }

    // --- Card navigation helpers ---
    public void showSubjects() {
        if (mainCards != null && mainCardContainer != null) {
            mainCards.show(mainCardContainer, "SUBJECTS");
            if (dashboardBackButton != null) dashboardBackButton.setVisible(false);
        }
    }
    public void showDashboard() {
        if (mainCards != null && mainCardContainer != null) {
            mainCards.show(mainCardContainer, "DASHBOARD");
            if (dashboardBackButton != null) dashboardBackButton.setVisible(true);
        }
    }
    public void addDashboardBackListener(ActionListener l) {
        if (dashboardBackButton != null) {
            dashboardBackButton.addActionListener(l);
        }
    }

    public void setStudentName(String name) { welcomeLabel.setText("Hi, Student " + name + "!"); }
    public void updateAverageScore(double avg) { avgScoreLabel.setText(String.format("Average Performance: %.2f%%", avg)); }
    public void addQuizButton(String quizName, ActionListener action) {
        RoundedButton btn = new RoundedButton(quizName);
        btn.setBackground(Color.decode("#c8abed"));
        btn.setFont(FontUtil.montserrat(14f, Font.BOLD, new Font("Arial", Font.BOLD, 14)));
        btn.setFocusPainted(false);
        btn.setForeground(Color.BLACK);
        btn.addActionListener(action);
        // Initial default size with much smaller height; overridden by applyResponsiveScaling()
        btn.setPreferredSize(new Dimension(220, 55));
        quizButtonPanel.add(btn);
        quizButtonPanel.revalidate();
        quizButtonPanel.repaint();
    }
    public void clearQuizButtons() { quizButtonPanel.removeAll(); quizButtonPanel.revalidate(); quizButtonPanel.repaint(); }
    public void addResultRow(String quizName, double score, double totalScore, String status) { tableModel.addRow(new Object[]{quizName, score, totalScore, status}); }
    public void clearResultRows() { tableModel.setRowCount(0); }
    public void addLogoutListener(ActionListener listener) { logoutButton.addActionListener(listener); }
    public void addDownloadListener(ActionListener listener) { downloadReportBtn.addActionListener(listener); }

    // --- Subject table helpers ---
    public void setSubjectRows(java.util.List<String[]> rows) {
        if (subjectTableModel == null) return;
        subjectTableModel.setRowCount(0);
        if (rows == null) return;
        for (String[] r : rows) {
            String sid = (r != null && r.length > 0) ? r[0] : "";
            String name = (r != null && r.length > 1) ? r[1] : "";
            String quizzes = (r != null && r.length > 2) ? r[2] : "";
            subjectTableModel.addRow(new Object[]{sid, name, quizzes});
        }
    }
    public void addSubjectSelectionListener(ListSelectionListener l) {
        if (subjectTable != null) {
            subjectTable.getSelectionModel().addListSelectionListener(l);
        }
    }
    public String getSelectedSubjectId() {
        if (subjectTable == null) return null;
        int row = subjectTable.getSelectedRow();
        if (row < 0) return null;
        Object val = subjectTable.getValueAt(row, 0);
        return val == null ? null : val.toString();
    }
    public void selectSubjectRow(int row) {
        if (subjectTable == null) return;
        if (row >= 0 && row < subjectTable.getRowCount()) {
            subjectTable.setRowSelectionInterval(row, row);
        }
    }
    public void clearSubjectSelection() {
        if (subjectTable != null) {
            subjectTable.clearSelection();
        }
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (column == 3) {
                String v = value == null ? "" : value.toString();
                if ("PASS".equalsIgnoreCase(v)) { 
                    c.setForeground(new Color(40, 167, 69)); // green
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("FAIL".equalsIgnoreCase(v)) { 
                    c.setForeground(new Color(200, 50, 50)); // red
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if ("INC".equalsIgnoreCase(v)) {
                    c.setForeground(Color.decode("#9146FF")); // purple
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                }
            }
            return c;
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

    class CustomScrollBarUI extends BasicScrollBarUI {
        private Color trackColor = Color.WHITE;
        private Color thumbColor = Color.decode("#9146FF");
        private Color thumbHoverColor = Color.decode("#7A3CD9");
        private boolean isHovered = false;
        @Override protected void configureScrollBarColors() { trackColor = Color.WHITE; thumbColor = Color.decode("#9146FF"); thumbHoverColor = Color.decode("#7A3CD9"); }
        @Override protected JButton createDecreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setBackground(Color.WHITE); return b; }
        @Override protected JButton createIncreaseButton(int orientation) { JButton b = new JButton(); b.setPreferredSize(new Dimension(0,0)); b.setBackground(Color.WHITE); return b; }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle r) { Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(trackColor); g2.fillRect(r.x,r.y,r.width,r.height); }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle r) { Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(isHovered?thumbHoverColor:thumbColor); g2.fillRoundRect(r.x,r.y,r.width,r.height,6,6); }
        @Override protected void installListeners() { super.installListeners(); scrollbar.addMouseListener(new MouseAdapter(){ public void mouseEntered(MouseEvent e){ isHovered=true; scrollbar.repaint(); } public void mouseExited(MouseEvent e){ isHovered=false; scrollbar.repaint(); }}); }
    }

    class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2;
        private Color borderColor = Color.BLACK;
        public RoundedButton(String text){ super(text); setOpaque(false); setFocusPainted(false); setBorderPainted(false); setBackground(Color.decode("#c8abed")); setForeground(Color.BLACK);}        
        public void setRadius(int r){ radius=r; repaint(); }
        public void setBorderThickness(int t){ borderThickness=t; repaint(); }
        public void setBorderColor(Color c){ this.borderColor=c; repaint(); }
        @Override protected void paintComponent(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillRoundRect(0,0,Math.abs(getWidth()-borderThickness),Math.abs(getHeight()-borderThickness),radius,radius); super.paintComponent(g); g2.dispose(); }
        @Override protected void paintBorder(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setStroke(new BasicStroke(borderThickness)); g2.setColor(borderColor); g2.drawRoundRect(1,1,Math.abs(getWidth()-borderThickness-1),Math.abs(getHeight()-borderThickness-1),radius,radius); g2.dispose(); }
    }
}