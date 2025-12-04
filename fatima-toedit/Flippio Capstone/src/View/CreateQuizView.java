package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CreateQuizView extends JFrame {

    // --- Components ---
    private CustomTextField quizTitleField;
    private CustomTextArea questionTextArea;
    private CustomTextField[] optionFields;
    private JList<String> questionList;
    private DefaultListModel<String> listModel;

    // --- Buttons ---
    private CustomButton addQuestionBtn;
    private CustomButton removeQuestionBtn;
    private CustomButton saveQuizBtn;
    private CustomButton cancelBtn;

    // --- State ---
    private int selectedCorrectIndex = -1;

    // --- Exact Colors from Image ---
    private final Color COLOR_BEIGE = Color.decode("#EED6C4");
    private final Color COLOR_BLUE = Color.decode("#A8B0FF");
    private final Color COLOR_GREEN = Color.decode("#74FCAA");
    private final Color COLOR_DARK_BTN = Color.decode("#19191E");
    private final Color COLOR_RED_TEXT = Color.decode("#FF7878");

    public CreateQuizView() {
        setTitle("Flippio - Create Quiz");
        setSize(750, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        // Main Panel (Vertical Stack)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // ==================== 1. QUIZ DETAILS ====================
        mainPanel.add(createHeaderLabel("QUIZ DETAILS"));
        mainPanel.add(Box.createVerticalStrut(10));

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

        // ==================== 2. ADD QUESTIONS ====================
        mainPanel.add(createHeaderLabel("ADD QUESTIONS"));
        mainPanel.add(Box.createVerticalStrut(10));

        // Centered Label
        JLabel qTextLbl = createLabel("Question Text:");
        qTextLbl.setAlignmentX(Component.CENTER_ALIGNMENT); // EXPLICIT CENTER
        mainPanel.add(qTextLbl);
        mainPanel.add(Box.createVerticalStrut(5));

        // Blue Area
        questionTextArea = new CustomTextArea();
        CustomScrollPane questionScroll = new CustomScrollPane(questionTextArea, COLOR_BLUE);
        questionScroll.setPreferredSize(new Dimension(600, 130));
        questionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        mainPanel.add(questionScroll);
        mainPanel.add(Box.createVerticalStrut(20));

        // Options Panel (GridBag for perfect alignment)
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 0); // Vertical spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        optionFields = new CustomTextField[4];
        for (int i = 0; i < 4; i++) {
            // Label (Right Aligned)
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            gbc.anchor = GridBagConstraints.LINE_END;
            JLabel optLbl = createLabel("Option " + (i + 1) + ":");
            optLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
            optionsPanel.add(optLbl, gbc);

            // Field (Fill remaining width)
            gbc.gridx = 1; gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.CENTER;
            optionFields[i] = new CustomTextField(COLOR_BEIGE, true);
            optionFields[i].setPreferredSize(new Dimension(200, 45));

            // Selection Logic
            final int index = i;
            optionFields[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedCorrectIndex = index;
                    repaint();
                }
            });
            optionsPanel.add(optionFields[i], gbc);
        }
        mainPanel.add(optionsPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // ==================== 3. MIDDLE BUTTONS ====================
        JPanel midBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        midBtnPanel.setBackground(Color.WHITE);

        addQuestionBtn = new CustomButton("Add Question", COLOR_DARK_BTN, Color.WHITE, false);
        addQuestionBtn.setPreferredSize(new Dimension(180, 50));

        removeQuestionBtn = new CustomButton("Remove Question", Color.WHITE, Color.BLACK, true);
        removeQuestionBtn.setPreferredSize(new Dimension(200, 50));

        midBtnPanel.add(addQuestionBtn);
        midBtnPanel.add(removeQuestionBtn);
        mainPanel.add(midBtnPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ==================== 4. PREVIEW SECTION ====================
        JLabel prevLbl = createLabel("Questions Preview:");
        prevLbl.setAlignmentX(Component.CENTER_ALIGNMENT); // EXPLICIT CENTER
        prevLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(prevLbl);
        mainPanel.add(Box.createVerticalStrut(5));

        listModel = new DefaultListModel<>();
        questionList = new JList<>(listModel);
        questionList.setCellRenderer(new TransparentListRenderer());
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionList.setBackground(new Color(0,0,0,0));
        questionList.setOpaque(false);

        // Blue Box for Preview (Thick Border)
        CustomScrollPane previewScroll = new CustomScrollPane(questionList, COLOR_BLUE);
        previewScroll.setThickBorder(true);
        previewScroll.setPreferredSize(new Dimension(600, 180));
        previewScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        mainPanel.add(previewScroll);
        mainPanel.add(Box.createVerticalStrut(30));

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

        add(mainPanel);
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
        repaint();
    }
    public void addQuestionToPreview(String txt) { listModel.addElement(txt); }
    public void removeQuestionFromPreview(int idx) { if(idx >= 0) listModel.remove(idx); }

    public void addAddQuestionListener(ActionListener l) { addQuestionBtn.addActionListener(l); }
    public void addRemoveQuestionListener(ActionListener l) { removeQuestionBtn.addActionListener(l); }
    public void addSaveQuizListener(ActionListener l) { saveQuizBtn.addActionListener(l); }
    public void addCancelListener(ActionListener l) { cancelBtn.addActionListener(l); }

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
            if (isOption && optionFields != null) {
                for(int i=0; i<optionFields.length; i++) {
                    if(optionFields[i] == this && selectedCorrectIndex == i) isSelected = true;
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
}