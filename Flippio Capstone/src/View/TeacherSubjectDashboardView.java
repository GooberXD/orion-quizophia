package View;

import Model.Subject;
import Utility.FontUtil;
import Utility.ResourceLoader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.function.Consumer;

public class TeacherSubjectDashboardView extends JFrame {
    private JLabel titleLabel;
    private JButton logoutButton;
    private JLabel logoLabel;
    private JTable subjectTable;
    private DefaultTableModel subjectTableModel;
    private JButton addSubjectBtn;
    private JButton removeSubjectBtn;
    private JButton openSubjectBtn;
    private JTextField idField;
    private JTextField nameField;
    private Consumer2 onAdd;
    private Consumer<Subject> onRemove;
    private Consumer<Subject> onOpen;
    private final double BASE_W = 1280.0;
    private final double BASE_H = 720.0;

    public TeacherSubjectDashboardView() {
        setTitle("Flippio - Teacher Subject Dashboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Window Icon
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon != null) setIconImage(appIcon.getImage());

        // 1. TOP PANEL - Styled like TeacherDashboardView (dark header)
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.decode("#1A171E"));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        titleLabel = new JLabel("Hi, Teacher!");
        titleLabel.setFont(FontUtil.montserrat(34f, Font.BOLD, new Font("Arial", Font.BOLD, 34)));
        titleLabel.setForeground(Color.WHITE);

        logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(Color.decode("#1A171E"));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(FontUtil.montserrat(20f, Font.BOLD, new Font("Arial", Font.BOLD, 20)));
        ((RoundedButton)logoutButton).setRadius(22);
        ((RoundedButton)logoutButton).setBorderThickness(3);
        ((RoundedButton)logoutButton).setBorderColor(Color.decode("#9146FF"));
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

        // Left: Title
        gbc.gridx = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(titleLabel, gbc);

        // Center: Logo
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.CENTER;
        topPanel.add(logoLabel, gbc);

        // Right: Buttons
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightButtons.setOpaque(false);
        rightButtons.add(logoutButton);
        gbc.gridx = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(rightButtons, gbc);
        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER PANEL - Styled table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Subject Table
        subjectTableModel = new DefaultTableModel(new String[]{"Subject ID", "Subject Name", "Number Enrolled"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        subjectTable = new JTable(subjectTableModel);
        subjectTable.setFont(FontUtil.montserrat(18f, Font.PLAIN, subjectTable.getFont()));
        subjectTable.setRowHeight(30);
        subjectTable.setBackground(Color.WHITE);
        subjectTable.setForeground(Color.BLACK);
        subjectTable.setGridColor(new Color(240, 240, 240));
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JTableHeader header = subjectTable.getTableHeader();
        header.setFont(FontUtil.montserrat(18f, Font.BOLD, header.getFont()));
        header.setBackground(Color.decode("#c8abed"));
        header.setForeground(Color.BLACK);

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        subjectTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        subjectTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        subjectTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = createStyledScrollPane(subjectTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. SOUTH PANEL - Form and Action Buttons
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Form panel removed; inputs now captured via modals per action

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actionPanel.setBackground(Color.WHITE);

        addSubjectBtn = new RoundedButton("Add Subject");
        addSubjectBtn.setBackground(new Color(76, 175, 80));
        addSubjectBtn.setForeground(Color.WHITE);
        addSubjectBtn.setFont(FontUtil.montserrat(18f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));
        ((RoundedButton)addSubjectBtn).setRadius(22);
        ((RoundedButton)addSubjectBtn).setBorderThickness(3);
        addSubjectBtn.setPreferredSize(new Dimension(200, 48));

        removeSubjectBtn = new RoundedButton("Delete Subject");
        removeSubjectBtn.setBackground(new Color(200, 50, 50));
        removeSubjectBtn.setForeground(Color.WHITE);
        removeSubjectBtn.setFont(FontUtil.montserrat(18f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));
        ((RoundedButton)removeSubjectBtn).setRadius(22);
        ((RoundedButton)removeSubjectBtn).setBorderThickness(3);
        removeSubjectBtn.setPreferredSize(new Dimension(220, 48));

        openSubjectBtn = new RoundedButton("Open Subject");
        openSubjectBtn.setBackground(Color.decode("#9146FF"));
        openSubjectBtn.setForeground(Color.WHITE);
        openSubjectBtn.setFont(FontUtil.montserrat(18f, Font.BOLD, new Font("Arial", Font.BOLD, 18)));
        ((RoundedButton)openSubjectBtn).setRadius(22);
        ((RoundedButton)openSubjectBtn).setBorderThickness(3);
        openSubjectBtn.setPreferredSize(new Dimension(200, 48));

        actionPanel.add(addSubjectBtn);
        actionPanel.add(removeSubjectBtn);
        actionPanel.add(openSubjectBtn);
        southPanel.add(actionPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        // Button listeners
        addSubjectBtn.addActionListener(e -> showAddSubjectDialog());

        removeSubjectBtn.addActionListener(e -> showDeleteSubjectDialog());

        openSubjectBtn.addActionListener(e -> {
            Subject selected = getSelectedSubject();
            if (selected != null && !selected.getId().isEmpty() && onOpen != null) {
                onOpen.accept(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a subject from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        subjectTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Subject s = getSelectedSubject();
                    if (s != null && onOpen != null) {
                        onOpen.accept(s); // direct open to quiz dashboard
                    } else {
                        showOpenSubjectDialog();
                    }
                }
            }
        });

        // Responsive scaling
        applyResponsiveScaling();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyResponsiveScaling();
            }
        });

        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    private void applyResponsiveScaling() {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getWidth() > 0 ? getWidth() : scr.width;
        int h = getHeight() > 0 ? getHeight() : scr.height;
        double scale = Math.min(w / BASE_W, h / BASE_H);
        scale = Math.max(0.75, Math.min(scale, 2.5));

        float nameSize = (float)Math.round(30 * scale);
        titleLabel.setFont(FontUtil.montserrat(nameSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)nameSize)));

        // Header buttons
        float btnSize = (float)Math.round(20 * scale);
        if (logoutButton instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) logoutButton;
            logoutButton.setFont(FontUtil.montserrat(btnSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)btnSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            logoutButton.setPreferredSize(new Dimension((int)Math.round(160 * scale), (int)Math.round(44 * scale)));
        }

        // Table scaling
        float tableSize = (float)Math.round(18 * scale);
        subjectTable.setFont(FontUtil.montserrat(tableSize, Font.PLAIN, new Font("Arial", Font.PLAIN, (int)tableSize)));
        subjectTable.setRowHeight((int)Math.round(30 * scale));
        if (subjectTable.getTableHeader() != null) {
            subjectTable.getTableHeader().setFont(FontUtil.montserrat(tableSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)tableSize)));
        }

        // Form scaling
        float formSize = (float)Math.round(14 * scale);
        if (idField != null) idField.setFont(FontUtil.montserrat(formSize, Font.PLAIN, new Font("Arial", Font.PLAIN, (int)formSize)));
        if (nameField != null) nameField.setFont(FontUtil.montserrat(formSize, Font.PLAIN, new Font("Arial", Font.PLAIN, (int)formSize)));

        // Action button scaling
        float actionSize = (float)Math.round(18 * scale);
        if (addSubjectBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) addSubjectBtn;
            addSubjectBtn.setFont(FontUtil.montserrat(actionSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)actionSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            addSubjectBtn.setPreferredSize(new Dimension((int)Math.round(200 * scale), (int)Math.round(48 * scale)));
        }
        if (removeSubjectBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) removeSubjectBtn;
            removeSubjectBtn.setFont(FontUtil.montserrat(actionSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)actionSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            removeSubjectBtn.setPreferredSize(new Dimension((int)Math.round(220 * scale), (int)Math.round(48 * scale)));
        }
        if (openSubjectBtn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) openSubjectBtn;
            openSubjectBtn.setFont(FontUtil.montserrat(actionSize, Font.BOLD, new Font("Arial", Font.BOLD, (int)actionSize)));
            rb.setRadius((int)Math.max(18, Math.round(22 * scale)));
            rb.setBorderThickness((int)Math.max(2, Math.round(3 * scale)));
            openSubjectBtn.setPreferredSize(new Dimension((int)Math.round(200 * scale), (int)Math.round(48 * scale)));
        }
    }

    // Modal dialogs for subject actions
    private void showAddSubjectDialog() {
        JDialog dialog = new JDialog(this, "Add Subject", true);
        JPanel panel = buildSubjectFormPanel("Subject ID:", "Name:");
        JTextField id = (JTextField) panel.getClientProperty("field1");
        JTextField name = (JTextField) panel.getClientProperty("field2");

        JButton continueBtn = new RoundedButton("Continue");
        styleModalButton(continueBtn, new Color(76, 175, 80));
        JButton cancelBtn = new RoundedButton("Cancel");
        styleModalButton(cancelBtn, new Color(200, 50, 50));

        continueBtn.addActionListener(e -> {
            if (onAdd != null) {
                onAdd.accept(id.getText().trim(), name.getText().trim());
            }
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(wrapFormWithButtons(panel, continueBtn, cancelBtn));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteSubjectDialog() {
        JDialog dialog = new JDialog(this, "Delete Subject", true);
        JPanel panel = buildSubjectFormPanel("Subject ID:", "Name:");
        JTextField id = (JTextField) panel.getClientProperty("field1");
        JTextField name = (JTextField) panel.getClientProperty("field2");

        JButton continueBtn = new RoundedButton("Continue");
        styleModalButton(continueBtn, new Color(200, 50, 50));
        JButton cancelBtn = new RoundedButton("Cancel");
        styleModalButton(cancelBtn, new Color(128, 128, 128));

        continueBtn.addActionListener(e -> {
            if (onRemove != null) {
                onRemove.accept(new Subject(id.getText().trim(), name.getText().trim(), null));
            }
            dialog.dispose();
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(wrapFormWithButtons(panel, continueBtn, cancelBtn));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showOpenSubjectDialog() {
        // Deprecated: now handled directly in button listener using table selection
    }

    // Helpers to build modal content
    private JPanel buildSubjectFormPanel(String label1, String label2) {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel l1 = new JLabel(label1);
        l1.setFont(FontUtil.montserrat(14f, Font.BOLD, l1.getFont()));
        form.add(l1, gbc);
        gbc.gridx = 1;
        JTextField f1 = new JTextField(20);
        f1.setFont(FontUtil.montserrat(14f, Font.PLAIN, f1.getFont()));
        f1.setPreferredSize(new Dimension(260, 36));
        form.add(f1, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel l2 = new JLabel(label2);
        l2.setFont(FontUtil.montserrat(14f, Font.BOLD, l2.getFont()));
        form.add(l2, gbc);
        gbc.gridx = 1;
        JTextField f2 = new JTextField(20);
        f2.setFont(FontUtil.montserrat(14f, Font.PLAIN, f2.getFont()));
        f2.setPreferredSize(new Dimension(260, 36));
        form.add(f2, gbc);

        form.putClientProperty("field1", f1);
        form.putClientProperty("field2", f2);
        return form;
    }

    private JPanel wrapFormWithButtons(JPanel form, JButton continueBtn, JButton cancelBtn) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        wrapper.add(form, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        buttons.add(cancelBtn);   // Cancel on the left
        buttons.add(continueBtn); // Continue on the right
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private void styleModalButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(FontUtil.montserrat(14f, Font.BOLD, btn.getFont()));
        if (btn instanceof RoundedButton) {
            RoundedButton rb = (RoundedButton) btn;
            rb.setRadius(18);
            rb.setBorderThickness(3);
            btn.setPreferredSize(new Dimension(120, 36));
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

    public void setSubjects(List<Subject> subjects) {
        subjectTableModel.setRowCount(0);
        for (Subject s : subjects) subjectTableModel.addRow(new Object[]{s.getId(), s.getName(), ""});
    }

    public void setSubjectSummaries(List<String[]> rows) {
        subjectTableModel.setRowCount(0);
        if (rows == null) return;
        for (String[] r : rows) {
            String id = (r != null && r.length > 0 && r[0] != null) ? r[0] : "";
            String name = (r != null && r.length > 1 && r[1] != null) ? r[1] : "";
            String enrolled = (r != null && r.length > 2 && r[2] != null) ? r[2] : "0";
            subjectTableModel.addRow(new Object[]{id, name, enrolled});
        }
    }

    public void setTeacherGreeting(String teacherName) {
        if (teacherName == null || teacherName.trim().isEmpty()) {
            titleLabel.setText("Hi, Teacher!");
        } else {
            titleLabel.setText("Hi, " + teacherName + "!");
        }
    }

    private Subject getSelectedSubject() {
        int row = subjectTable.getSelectedRow();
        if (row < 0) return null;
        Object idObj = subjectTableModel.getValueAt(row, 0);
        Object nameObj = subjectTableModel.getValueAt(row, 1);
        String id = idObj != null ? idObj.toString() : "";
        String name = nameObj != null ? nameObj.toString() : "";
        return new Subject(id, name, null);
    }

    public void onAddSubject(Consumer2 addHandler) { this.onAdd = addHandler; }
    public void onRemoveSubject(Consumer<Subject> removeHandler) { this.onRemove = removeHandler; }
    public void onOpenSubject(Consumer<Subject> openHandler) { this.onOpen = openHandler; }
    
    public void addLogoutListener(ActionListener listener) { logoutButton.addActionListener(listener); }

    public void showError(String message) { JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE); }

    @FunctionalInterface
    public interface Consumer2 {
        void accept(String id, String name);
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
        @Override protected void installListeners() { super.installListeners(); scrollbar.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseEntered(java.awt.event.MouseEvent e){ isHovered=true; scrollbar.repaint(); } public void mouseExited(java.awt.event.MouseEvent e){ isHovered=false; scrollbar.repaint(); }}); }
    }

    class RoundedButton extends JButton {
        private int radius = 20;
        private int borderThickness = 2;
        private Color borderColor = Color.BLACK;
        public RoundedButton(String text){ super(text); setOpaque(false); setFocusPainted(false); setBorderPainted(false); }        
        public void setRadius(int r){ radius=r; repaint(); }
        public void setBorderThickness(int t){ borderThickness=t; repaint(); }
        public void setBorderColor(Color c){ this.borderColor = c; repaint(); }
        @Override protected void paintComponent(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillRoundRect(0,0,Math.abs(getWidth()-borderThickness),Math.abs(getHeight()-borderThickness),radius,radius); super.paintComponent(g); g2.dispose(); }
        @Override protected void paintBorder(Graphics g){ Graphics2D g2=(Graphics2D)g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setStroke(new BasicStroke(borderThickness)); g2.setColor(borderColor); g2.drawRoundRect(1,1,Math.abs(getWidth()-borderThickness-1),Math.abs(getHeight()-borderThickness-1),radius,radius); g2.dispose(); }
    }
}
