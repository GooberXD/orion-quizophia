package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionListener;
import Utility.ResourceLoader;
import Utility.FontUtil;

public class CreateQuizView extends JFrame {
    private CustomTextField quizTitleField;
    private CustomTextArea questionTextArea;
    private CustomTextField[] optionFields;
    private JCheckBox[] correctBoxes;
    private JList<String> questionList;
    private DefaultListModel<String> listModel;

    private CustomButton addQuestionBtn;
    private CustomButton removeQuestionBtn;
    private CustomButton saveQuizBtn;
    private CustomButton cancelBtn;

    private int selectedCorrectIndex = -1;
    private int editingQuestionIndex = -1; // -1 means adding new, >=0 means editing existing

    private final Color COLOR_BEIGE = Color.decode("#EED6C4");
    private final Color COLOR_BLUE = Color.decode("#A8B0FF");
    private final Color COLOR_GREEN = Color.decode("#74FCAA");
    private final Color COLOR_DARK_BTN = Color.decode("#19191E");
    private final Color COLOR_RED_TEXT = Color.decode("#FF7878");

    public CreateQuizView() {
        setTitle("Flippio - Create Quiz");
        // Flexible fullscreen across displays
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Window icon
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon == null) appIcon = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (appIcon != null) setIconImage(appIcon.getImage());

        // Main Panel (Vertical Stack)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // ==================== 1. QUIZ DETAILS ====================
        JLabel quizHeader = createHeaderLabel("QUIZ DETAILS");
        quizHeader.setAlignmentX(Component.CENTER_ALIGNMENT); // center the header
        mainPanel.add(quizHeader);
        mainPanel.add(Box.createVerticalStrut(15)); // extra space before title row

        // Horizontal Panel for "Quiz Title: [Field]"
        JPanel titlePanel = new JPanel(new BorderLayout(15, 0));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel titleLbl = createLabel("Quiz Title:");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titlePanel.add(titleLbl, BorderLayout.WEST);

        quizTitleField = new CustomTextField(COLOR_BEIGE, false);
        titlePanel.add(quizTitleField, BorderLayout.CENTER);

        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // ==================== 2. QUESTION ENTRY ====================
        // Removed the "ADD QUESTIONS" label per request
        mainPanel.add(Box.createVerticalStrut(5));

        // Centered Label
        JLabel qTextLbl = createLabel("Question Text:");
        qTextLbl.setAlignmentX(Component.CENTER_ALIGNMENT); // EXPLICIT CENTER
        mainPanel.add(qTextLbl);
        mainPanel.add(Box.createVerticalStrut(5));

        // Blue Area
        questionTextArea = new CustomTextArea();
        CustomScrollPane questionScroll = new CustomScrollPane(questionTextArea, COLOR_BLUE);
        questionScroll.setPreferredSize(new Dimension(600, 110));
        questionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        mainPanel.add(questionScroll);
        mainPanel.add(Box.createVerticalStrut(10));

        // Options Panel (GridBag for perfect alignment and dynamic height)
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 0); // Vertical spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        optionFields = new CustomTextField[4];
        correctBoxes = new JCheckBox[4];
        for (int i = 0; i < 4; i++) {
            // Label (Right Aligned) row 2*i
            gbc.gridx = 0; gbc.gridy = i * 2; gbc.weightx = 0; gbc.weighty = 0;
            gbc.anchor = GridBagConstraints.LINE_END;
            JLabel optLbl = createLabel("Option " + (i + 1) + ":");
            optLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            optionsPanel.add(optLbl, gbc);

            // Field spans two rows to align with label and checkbox
            gbc.gridx = 1; gbc.gridy = i * 2; gbc.weightx = 1.0; gbc.gridheight = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            optionFields[i] = new CustomGrowingTextField(COLOR_BEIGE, true);
            optionFields[i].setPreferredSize(new Dimension(200, 50));
            optionFields[i].setMinimumSize(new Dimension(200, 50));
            optionFields[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
            optionsPanel.add(optionFields[i], gbc);
            gbc.gridheight = 1; // reset

            // Checkbox row 2*i + 1 under label, to the left of field
            gbc.gridx = 0; gbc.gridy = i * 2 + 1; gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.LINE_END;
            JCheckBox cb = new JCheckBox("Correct");
            cb.setOpaque(false);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 14));
            correctBoxes[i] = cb;
            final int idx = i;
            cb.addActionListener(e -> {
                // allow only one selection at a time
                for (int k = 0; k < correctBoxes.length; k++) {
                    if (k != idx) correctBoxes[k].setSelected(false);
                }
                selectedCorrectIndex = cb.isSelected() ? idx : -1;
                // repaint option fields to reflect green state
                for (CustomTextField f : optionFields) f.repaint();
            });
            optionsPanel.add(cb, gbc);
        }
        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // move up options

        // ==================== 3. MIDDLE BUTTONS ====================
        JPanel midBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        midBtnPanel.setBackground(Color.WHITE);

        addQuestionBtn = new CustomButton("Add Question", COLOR_DARK_BTN, Color.WHITE, false);
        addQuestionBtn.setPreferredSize(new Dimension(180, 50));

        removeQuestionBtn = new CustomButton("Delete Question", Color.WHITE, Color.BLACK, true);
        removeQuestionBtn.setPreferredSize(new Dimension(180, 50));

        midBtnPanel.add(addQuestionBtn);
        midBtnPanel.add(removeQuestionBtn);
        mainPanel.add(midBtnPanel);
        mainPanel.add(Box.createVerticalStrut(10)); // move buttons higher

        // ==================== 4. PREVIEW SECTION ====================
        JLabel prevLbl = createLabel("Questions Preview:");
        prevLbl.setAlignmentX(Component.CENTER_ALIGNMENT); // EXPLICIT CENTER
        prevLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(Box.createVerticalStrut(20)); // push preview further down
        mainPanel.add(prevLbl);
        mainPanel.add(Box.createVerticalStrut(8));

        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);
        questionList.setCellRenderer(new TransparentListRenderer());
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionList.setBackground(new Color(0,0,0,0));
        questionList.setOpaque(false);

        // Blue Box for Preview (Thick Border)
        CustomScrollPane previewScroll = new CustomScrollPane(questionList, COLOR_BLUE);
        previewScroll.setThickBorder(true);
        previewScroll.setPreferredSize(new Dimension(600, 140)); // slightly larger visible area
        previewScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        previewScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // ensure scrollable
        mainPanel.add(previewScroll);
        mainPanel.add(Box.createVerticalStrut(15));

        // ==================== 5. BOTTOM BUTTONS ====================
        JPanel bottomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        bottomBtnPanel.setBackground(Color.WHITE);

        cancelBtn = new CustomButton("Cancel", Color.WHITE, COLOR_RED_TEXT, true);
        cancelBtn.setPreferredSize(new Dimension(160, 50));

        saveQuizBtn = new CustomButton("Save Quiz", COLOR_GREEN, Color.BLACK, true);
        saveQuizBtn.setPreferredSize(new Dimension(180, 50));

        bottomBtnPanel.add(cancelBtn);
        bottomBtnPanel.add(saveQuizBtn);
        mainPanel.add(bottomBtnPanel);

        // Wrap entire content in a scrollable container to avoid compressing UI
        JScrollPane rootScroll = new JScrollPane(mainPanel);
        rootScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        rootScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rootScroll.getViewport().setBackground(Color.WHITE);
        rootScroll.setBorder(null);
        // Apply styled scrollbars similar to dashboard
        JScrollBar vBar = rootScroll.getVerticalScrollBar();
        vBar.setUI(new PurpleScrollBarUI());
        vBar.setPreferredSize(new Dimension(12, Integer.MAX_VALUE));
        vBar.setUnitIncrement(16);
        vBar.setOpaque(false);
        vBar.setBackground(Color.WHITE);
        JScrollBar hBar = rootScroll.getHorizontalScrollBar();
        hBar.setUI(new PurpleScrollBarUI());
        hBar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 12));
        hBar.setUnitIncrement(16);
        hBar.setOpaque(false);
        hBar.setBackground(Color.WHITE);
        add(rootScroll);

        // Show add/delete only when a preview item is selected
        addQuestionBtn.setEnabled(true); // adding is always allowed
        removeQuestionBtn.setEnabled(false);
        questionList.addListSelectionListener(e -> {
            boolean hasSelection = questionList.getSelectedIndex() >= 0;
            removeQuestionBtn.setEnabled(hasSelection);
        });

        // Double-click to edit question
        questionList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = questionList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        loadQuestionForEditing(index);
                    }
                }
            }
        });

        // Apply Montserrat globally to this view (keep consistent with other views)
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    // --- Helper Methods ---
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 18));
        l.setForeground(Color.BLACK);
        return l;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 18));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // --- Getters for Controller ---
    public String getQuizTitle() { return quizTitleField.getText().trim(); }
    public void setQuizTitle(String title) { quizTitleField.setText(title); }
    public void setQuizTitleEditable(boolean editable) { quizTitleField.setEditable(editable); }
    public String getQuestionText() { return questionTextArea.getText().trim(); }
    public String getOption(int index) {
        if(index >=0 && index < 4) return optionFields[index].getText().trim();
        return "";
    }
    public int getSelectedCorrectIndex() { return selectedCorrectIndex; }
    public int getSelectedListIndex() { return questionList.getSelectedIndex(); }

    public void clearQuestionInputs() {
        questionTextArea.setText("");
        for (CustomTextField f : optionFields) f.setText("");
        selectedCorrectIndex = -1;
        editingQuestionIndex = -1;
        addQuestionBtn.setText("Add Question");
        for (JCheckBox cb : correctBoxes) cb.setSelected(false);
        repaint();
    }
    public void addQuestionToPreview(String txt) { listModel.addElement(txt); }
    public void removeQuestionFromPreview(int idx) { if(idx >= 0) listModel.remove(idx); }
    // Compatibility: append and clear preview helpers like previous implementation
    public void appendToPreview(String text) { listModel.addElement(text); }
    public void clearPreview() { listModel.clear(); }

    public void addAddQuestionListener(ActionListener l) { addQuestionBtn.addActionListener(l); }
    public void addRemoveQuestionListener(ActionListener l) { removeQuestionBtn.addActionListener(l); }
    public void addSaveQuizListener(ActionListener l) { saveQuizBtn.addActionListener(l); }
    public void addCancelListener(ActionListener l) { cancelBtn.addActionListener(l); }
    // Compatibility: previous name for remove listener
    public void addRemoveLastQuestionListener(ActionListener l) { removeQuestionBtn.addActionListener(l); }

    // --- Edit Question Support ---
    public int getEditingQuestionIndex() { return editingQuestionIndex; }
    public boolean isEditingQuestion() { return editingQuestionIndex >= 0; }

    public void setQuestionData(String questionText, String[] options, int correctIndex) {
        questionTextArea.setText(questionText);
        for (int i = 0; i < 4 && i < options.length; i++) {
            optionFields[i].setText(options[i]);
        }
        for (int i = 0; i < correctBoxes.length; i++) {
            correctBoxes[i].setSelected(i == correctIndex);
        }
        selectedCorrectIndex = correctIndex;
        repaint();
    }

    private void loadQuestionForEditing(int index) {
        editingQuestionIndex = index;
        addQuestionBtn.setText("Update Question");
        // Notify controller to load this question's data
        firePropertyChange("editQuestion", -1, index);
    }

    // ==========================================================
    // CUSTOM DESIGN COMPONENTS
    // ==========================================================

    /** Beige Rounded TextField that turns GREEN when selected */
    class CustomTextField extends JTextField {
        private Color fillColor;
        private boolean isOption;

        public CustomTextField(Color bg, boolean isOption) {
            this.fillColor = bg;
            this.isOption = isOption;
            setOpaque(false);
            setBorder(new EmptyBorder(5, 15, 5, 15));
            setFont(new Font("SansSerif", Font.PLAIN, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Determine if this is the "Selected" correct answer
            boolean isSelected = false;
            if (isOption && optionFields != null && correctBoxes != null) {
                for(int i=0; i<optionFields.length; i++) {
                    if(optionFields[i] == this && correctBoxes[i].isSelected()) isSelected = true;
                }
            }

            // Fill
            g2.setColor(isSelected ? COLOR_GREEN : fillColor);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(isSelected ? 4f : 1.5f)); // Thick border if selected
            int offset = isSelected ? 2 : 0;
            g2.drawRoundRect(offset, offset, getWidth()-1-(offset*2), getHeight()-1-(offset*2), 20, 20);

            super.paintComponent(g);
            g2.dispose();
        }
    }

    /** Growing TextField: increases its preferred height based on text length to avoid collision */
    class CustomGrowingTextField extends CustomTextField {
        public CustomGrowingTextField(Color bg, boolean isOption) {
            super(bg, isOption);
        }

        @Override
        public void setText(String t) {
            super.setText(t);
            adjustHeight();
        }

        @Override
        protected void processKeyEvent(java.awt.event.KeyEvent e) {
            super.processKeyEvent(e);
            SwingUtilities.invokeLater(this::adjustHeight);
        }

        private void adjustHeight() {
            FontMetrics fm = getFontMetrics(getFont());
            String text = getText();
            int width = getWidth() > 0 ? getWidth() : 300;
            if (width <= 0) width = 300;
            // Estimate lines by measuring string width vs available width minus padding
            int padding = 30;
            int lines = Math.max(1, (int) Math.ceil((double) fm.stringWidth(text) / Math.max(1, (width - padding))));
            int lineHeight = fm.getHeight();
            int target = Math.min(120, Math.max(50, (lineHeight + 10) * lines));
            Dimension pref = getPreferredSize();
            setPreferredSize(new Dimension(pref.width, target));
            revalidate();
        }
    }

    /** Blue Rounded Area */
    class CustomScrollPane extends JScrollPane {
        private Color bgColor;
        private boolean thickBorder = false;

        public CustomScrollPane(JComponent view, Color color) {
            super(view);
            this.bgColor = color;
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(null);
        }
        public void setThickBorder(boolean b) { this.thickBorder = b; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

            g2.setColor(Color.BLACK);
            // Preview box has a thick border in image
            g2.setStroke(new BasicStroke(thickBorder ? 3f : 1.5f));
            int offset = thickBorder ? 1 : 0;
            g2.drawRoundRect(offset, offset, getWidth()-1-(offset*2), getHeight()-1-(offset*2), 20, 20);

            g2.dispose();
        }
    }

    class CustomTextArea extends JTextArea {
        public CustomTextArea() {
            setOpaque(false);
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setFont(new Font("SansSerif", Font.PLAIN, 18));
        }
    }

    class CustomButton extends JButton {
        private Color bg, fg;
        private boolean border;
        public CustomButton(String text, Color bg, Color fg, boolean border) {
            super(text);
            this.bg = bg; this.fg = fg; this.border = border;
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(fg); setFont(new Font("SansSerif", Font.BOLD, 15));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);

            if (border) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
            // Ensure foreground text color is applied consistently
            setForeground(fg);
            super.paintComponent(g); g2.dispose();
        }
    }

    class TransparentListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            l.setOpaque(isSelected);
            l.setBackground(isSelected ? new Color(255, 255, 255, 100) : null);
            l.setBorder(new EmptyBorder(5, 10, 5, 10));
            l.setFont(new Font("SansSerif", Font.PLAIN, 16));
            return l;
        }
    }

    // Styled scrollbar consistent with StudentDashboardView
    static class PurpleScrollBarUI extends BasicScrollBarUI {
        private Color trackColor = Color.WHITE;
        private Color thumbColor = Color.decode("#9146FF");
        private Color thumbHoverColor = Color.decode("#7A3CD9");
        private boolean isHovered = false;

        @Override
        protected void configureScrollBarColors() {
            trackColor = Color.WHITE;
            thumbColor = Color.decode("#9146FF");
            thumbHoverColor = Color.decode("#7A3CD9");
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setBackground(Color.WHITE);
            return b;
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setBackground(Color.WHITE);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.dispose();
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isHovered ? thumbHoverColor : thumbColor);
            g2.fillRoundRect(r.x, r.y, r.width, r.height, 6, 6);
            g2.dispose();
        }

        @Override
        protected void installListeners() {
            super.installListeners();
            scrollbar.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    scrollbar.repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    scrollbar.repaint();
                }
            });
        }
    }
}