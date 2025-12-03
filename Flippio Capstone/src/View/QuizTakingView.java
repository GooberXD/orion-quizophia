package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;

public class QuizTakingView extends JFrame {

    // --- Main Layout Components ---
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // --- Screen 1: Quiz View Components ---
    private JPanel quizPanel;
    private JLabel questionLabel;
    private CustomOptionButton[] options; // Custom rounded buttons
    private ButtonGroup optionsGroup;
    private RoundedButton btnPrev, btnNext, btnSubmit;

    // --- Screen 2: Confirmation View Components ---
    private JPanel confirmPanel;
    private RoundedButton btnConfirmYes, btnConfirmNo;

    // --- Screen 3: Result View Components ---
    private JPanel resultPanel;
    private RoundedButton btnExit;

    // --- Colors from Screenshots ---
    private final Color BG_DARK = new Color(28, 28, 36);      // Dark Background
    private final Color BG_GREEN = new Color(105, 240, 174);  // Success Background
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color OPTION_BLUE = new Color(165, 180, 252);
    private final Color OPTION_GREEN = new Color(118, 255, 163); // Specific green card
    private final Color OPTION_RED = new Color(255, 138, 128);   // Specific red card
    private final Color BTN_PURPLE = new Color(209, 196, 233);   // Nav buttons
    private final Color BTN_YES_GREEN = new Color(156, 204, 165);
    private final Color BTN_NO_RED = new Color(255, 128, 128);
    private final Color BTN_EXIT_DARK = new Color(30, 30, 35);

    // Font
    private Font mainFont;

    public QuizTakingView() {
        // Window Setup
        setTitle("Flippio - Quiz");
        setSize(900, 500); // Widened to match landscape aspect ratio of screenshots
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load Font (Montserrat or fallback)
        try {
            mainFont = new Font("Montserrat", Font.PLAIN, 18);
        } catch (Exception e) {
            mainFont = new Font("SansSerif", Font.PLAIN, 18);
        }

        // Setup CardLayout to swap screens
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        add(mainContainer);

        // Initialize Screens
        initQuizScreen();
        initConfirmationScreen();
        initResultScreen();

        // Show first screen
        cardLayout.show(mainContainer, "QUIZ");
    }

    // =========================================================================
    // SCREEN 1: THE QUIZ INTERFACE
    // =========================================================================
    private void initQuizScreen() {
        quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(BG_DARK);

        // 1. TOP: Question
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BG_DARK);
        topPanel.setPreferredSize(new Dimension(800, 150));

        questionLabel = new JLabel("What is the powerhouse of the cell?", SwingConstants.CENTER);
        questionLabel.setFont(mainFont.deriveFont(Font.PLAIN, 28f));
        questionLabel.setForeground(TEXT_WHITE);
        topPanel.add(questionLabel);
        quizPanel.add(topPanel, BorderLayout.NORTH);

        // 2. CENTER: Options (Horizontal Cards)
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        centerWrapper.setBackground(BG_DARK);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(1, 4, 20, 0)); // 1 Row, 4 Cols, 20px gap
        optionsPanel.setBackground(BG_DARK);
        optionsPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        options = new CustomOptionButton[4];
        optionsGroup = new ButtonGroup();

        // Colors to match the specific screenshot design (Blue, Blue, Green, Red)
        Color[] cardColors = {OPTION_BLUE, OPTION_BLUE, OPTION_GREEN, OPTION_RED};

        for (int i = 0; i < 4; i++) {
            options[i] = new CustomOptionButton("Option " + (i + 1), cardColors[i]);
            optionsGroup.add(options[i]);
            optionsPanel.add(options[i]);
        }

        centerWrapper.add(optionsPanel);
        quizPanel.add(centerWrapper, BorderLayout.CENTER);

        // 3. BOTTOM: Navigation
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 30));
        navPanel.setBackground(BG_DARK);

        btnPrev = new RoundedButton("Prev", BTN_PURPLE, Color.BLACK);
        btnNext = new RoundedButton("Next", BTN_PURPLE, Color.BLACK);
        btnSubmit = new RoundedButton("Submit", BTN_PURPLE, Color.BLACK);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        navPanel.add(btnSubmit);
        quizPanel.add(navPanel, BorderLayout.SOUTH);

        // Logic for Submit Button (View Transition)
        btnSubmit.addActionListener(e -> {
            // "You can only click submit if all questions are answered"
            // For this specific view, we check if an option is selected.
            if (getSelectedOptionIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select an answer before submitting.", "Answer Required", JOptionPane.WARNING_MESSAGE);
            } else {
                cardLayout.show(mainContainer, "CONFIRM");
            }
        });

        mainContainer.add(quizPanel, "QUIZ");
    }

    // =========================================================================
    // SCREEN 2: CONFIRMATION MODAL
    // =========================================================================
    private void initConfirmationScreen() {
        confirmPanel = new JPanel(new GridBagLayout());
        confirmPanel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 40, 10);

        JLabel confirmLabel = new JLabel("Would you like to submit?");
        confirmLabel.setFont(mainFont.deriveFont(Font.PLAIN, 32f));
        confirmLabel.setForeground(TEXT_WHITE);
        confirmPanel.add(confirmLabel, gbc);

        // Button Container
        JPanel btnContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnContainer.setOpaque(false);

        btnConfirmYes = new RoundedButton("Yes", BTN_YES_GREEN, Color.BLACK);
        btnConfirmNo = new RoundedButton("No", BTN_NO_RED, Color.BLACK);

        btnContainer.add(btnConfirmYes);
        btnContainer.add(btnConfirmNo);

        gbc.insets = new Insets(0, 0, 0, 0);
        confirmPanel.add(btnContainer, gbc);

        // Logic
        btnConfirmNo.addActionListener(e -> cardLayout.show(mainContainer, "QUIZ"));
        btnConfirmYes.addActionListener(e -> cardLayout.show(mainContainer, "RESULT"));

        mainContainer.add(confirmPanel, "CONFIRM");
    }

    // =========================================================================
    // SCREEN 3: CONGRATULATIONS / RESULT
    // =========================================================================
    private void initResultScreen() {
        resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setBackground(BG_GREEN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 50, 10);

        JLabel congratsLabel = new JLabel("Congratulations! You've finished it");
        congratsLabel.setFont(mainFont.deriveFont(Font.PLAIN, 32f));
        congratsLabel.setForeground(Color.BLACK);
        resultPanel.add(congratsLabel, gbc);

        btnExit = new RoundedButton("Exit", BTN_EXIT_DARK, Color.WHITE);
        btnExit.addActionListener(e -> System.exit(0)); // Exit Application

        gbc.insets = new Insets(0, 0, 0, 0);
        resultPanel.add(btnExit, gbc);

        mainContainer.add(resultPanel, "RESULT");
    }

    // =========================================================================
    // PUBLIC METHODS (API FOR CONTROLLER)
    // =========================================================================
    public void setQuestionText(String text) {
        questionLabel.setText("<html><body style='text-align: center'>" + text + "</body></html>");
    }

    public void setOptions(String[] choices) {
        for (int i = 0; i < choices.length && i < options.length; i++) {
            options[i].setText(choices[i]);
            options[i].setSelected(false);
        }
    }

    public int getSelectedOptionIndex() {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    public void setSelectedOption(int index) {
        if (index >= 0 && index < options.length) {
            options[index].setSelected(true);
        }
    }

    public JButton getBtnNext() {
        return btnNext;
    }

    public JButton getBtnPrev() {
        return btnPrev;
    }

    // Note: getBtnSubmit() listener is now handled internally for the view flow,
    // but the controller can still add logic to it if needed.
    public JButton getBtnSubmit() {
        return btnSubmit;
    }


    // =========================================================================
    // CUSTOM UI COMPONENTS (INNER CLASSES)
    // =========================================================================

    /**
     * Custom Toggle Button for Quiz Options (The Colorful Cards)
     */
    private class CustomOptionButton extends JToggleButton {
        private Color baseColor;
        private Color selectedBorderColor = new Color(145, 70, 255); // Purple border like image

        public CustomOptionButton(String text, Color bg) {
            super(text);
            this.baseColor = bg;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(mainFont.deriveFont(Font.PLAIN, 18f));
            setForeground(Color.BLACK);
            setPreferredSize(new Dimension(200, 150)); // Large cards
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Shape
            int arc = 20;
            RoundRectangle2D.Float shape = new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, arc, arc);

            // Background
            g2.setColor(baseColor);
            g2.fill(shape);

            // Selection Border
            if (isSelected()) {
                g2.setStroke(new BasicStroke(4f));
                g2.setColor(selectedBorderColor);
                g2.draw(shape);
            }

            // Text
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int x = (getWidth() - stringBounds.width) / 2;
            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    /**
     * Rounded Pill Button for Navigation
     */
    private class RoundedButton extends JButton {
        private Color bgColor;
        private Color textColor;

        public RoundedButton(String text, Color bg, Color txt) {
            super(text);
            this.bgColor = bg;
            this.textColor = txt;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(mainFont.deriveFont(Font.PLAIN, 16f));
            setPreferredSize(new Dimension(160, 50));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(bgColor.darker());
            } else {
                g2.setColor(bgColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // 30px radius for pill shape

            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
            int x = (getWidth() - stringBounds.width) / 2;
            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }
}


//THIS IS AN ALT DESIGN IM WORKING ON, WILL REVISIT
// package View;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//import java.awt.geom.Ellipse2D;
//import java.awt.geom.RoundRectangle2D;
//
//public class QuizTakingView extends JFrame {
//
//    // --- Main Layout Components ---
//    private JPanel mainContainer;
//    private CardLayout cardLayout;
//
//    // --- Screen 1: Quiz View Components ---
//    private JPanel quizViewPanel;
//    private JPanel headerPanel;
//    private JPanel bodyPanel;
//
//    private JLabel questionLabel;
//    private CustomOptionButton[] options;
//    private ButtonGroup optionsGroup;
//
//    // Navigation & Tracker
//    private QuestionNavButton[] questionTrackerButtons;
//    private RoundedButton btnPrev, btnNext, btnSubmit;
//
//    // --- Screen 2 & 3 components ---
//    private JPanel confirmPanel;
//    private JPanel resultPanel;
//
//    // --- Colors ---
//    private final Color BG_DARK = new Color(28, 28, 36);
//    private final Color BG_LIGHT = new Color(255, 255, 255);
//    private final Color TEXT_WHITE = Color.WHITE;
//
//    private final Color CARD_BLUE = new Color(165, 180, 252);
//    private final Color CARD_GREEN = new Color(118, 255, 163);
//    private final Color CARD_RED = new Color(255, 138, 128);
//
//    private final Color BTN_PURPLE = new Color(209, 196, 233);
//    private final Color BTN_SUBMIT = new Color(190, 170, 230);
//    private final Color TRACKER_BG = new Color(240, 220, 205);
//    private final Color DOT_COLOR  = new Color(80, 50, 120);
//
//    private Font mainFont;
//
//    public QuizTakingView() {
//        setTitle("Flippio - Quiz");
//        setSize(1000, 600);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        try {
//            mainFont = new Font("Montserrat", Font.PLAIN, 18);
//        } catch (Exception e) {
//            mainFont = new Font("SansSerif", Font.PLAIN, 18);
//        }
//
//        cardLayout = new CardLayout();
//        mainContainer = new JPanel(cardLayout);
//        add(mainContainer);
//
//        initQuizScreen();
//        initConfirmationScreen();
//        initResultScreen();
//
//        cardLayout.show(mainContainer, "QUIZ");
//    }
//
//    // =========================================================================
//    // SCREEN 1: QUIZ INTERFACE
//    // =========================================================================
//    private void initQuizScreen() {
//        quizViewPanel = new JPanel(new BorderLayout());
//
//        // A. TOP HEADER
//        initHeaderPanel();
//        quizViewPanel.add(headerPanel, BorderLayout.NORTH);
//
//        // B. CENTER BODY
//        initBodyPanel();
//        quizViewPanel.add(bodyPanel, BorderLayout.CENTER);
//
//        mainContainer.add(quizViewPanel, "QUIZ");
//    }
//
//    private void initHeaderPanel() {
//        headerPanel = new JPanel(new BorderLayout());
//        headerPanel.setBackground(BG_LIGHT);
//        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));
//        headerPanel.setPreferredSize(new Dimension(1000, 100));
//
//        // 1. Center: Question Tracker Grid
//        JPanel trackerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//        trackerWrapper.setBackground(BG_LIGHT);
//
//        JPanel trackerGrid = new JPanel(new GridLayout(2, 5, 8, 8));
//        trackerGrid.setBackground(BG_LIGHT);
//
//        questionTrackerButtons = new QuestionNavButton[10];
//        for (int i = 0; i < 10; i++) {
//            questionTrackerButtons[i] = new QuestionNavButton(String.valueOf(i + 1));
//            trackerGrid.add(questionTrackerButtons[i]);
//        }
//        trackerWrapper.add(trackerGrid);
//
//        // 2. Right: Submit Button
//        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        rightPanel.setBackground(BG_LIGHT);
//
//        btnSubmit = new RoundedButton("Submit", BTN_SUBMIT, Color.BLACK);
//        btnSubmit.setPreferredSize(new Dimension(180, 50));
//        rightPanel.add(btnSubmit);
//
//        headerPanel.add(trackerWrapper, BorderLayout.CENTER);
//        headerPanel.add(rightPanel, BorderLayout.EAST);
//
//        // Internal Logic: Submit leads to Confirmation
//        btnSubmit.addActionListener(e -> cardLayout.show(mainContainer, "CONFIRM"));
//    }
//
//    private void initBodyPanel() {
//        bodyPanel = new JPanel(new BorderLayout());
//        bodyPanel.setBackground(BG_DARK);
//        bodyPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
//
//        // 1. Question Label
//        questionLabel = new JLabel("Loading Question...", SwingConstants.CENTER);
//        questionLabel.setFont(mainFont.deriveFont(Font.PLAIN, 26f));
//        questionLabel.setForeground(TEXT_WHITE);
//        questionLabel.setBorder(new EmptyBorder(0, 0, 40, 0));
//        bodyPanel.add(questionLabel, BorderLayout.NORTH);
//
//        // 2. Options
//        JPanel optionsPanel = new JPanel(new GridLayout(1, 4, 25, 0));
//        optionsPanel.setBackground(BG_DARK);
//        optionsPanel.setBorder(new EmptyBorder(10, 0, 30, 0));
//
//        options = new CustomOptionButton[4];
//        optionsGroup = new ButtonGroup();
//        Color[] colors = {CARD_BLUE, CARD_BLUE, CARD_GREEN, CARD_RED};
//
//        for (int i = 0; i < 4; i++) {
//            options[i] = new CustomOptionButton("Option " + (i + 1), colors[i]);
//            optionsGroup.add(options[i]);
//            optionsPanel.add(options[i]);
//        }
//        bodyPanel.add(optionsPanel, BorderLayout.CENTER);
//
//        // 3. Navigation Buttons
//        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 0));
//        navPanel.setBackground(BG_DARK);
//
//        btnPrev = new RoundedButton("Prev", BTN_PURPLE, Color.BLACK);
//        btnNext = new RoundedButton("Next", BTN_PURPLE, Color.BLACK);
//
//        navPanel.add(btnPrev);
//        navPanel.add(btnNext);
//        bodyPanel.add(navPanel, BorderLayout.SOUTH);
//    }
//
//    // =========================================================================
//    // API METHODS FOR CONTROLLER
//    // =========================================================================
//
//    public JButton getBtnNext() {
//        return btnNext;
//    }
//
//    public JButton getBtnPrev() {
//        return btnPrev;
//    }
//
//    public JButton getBtnSubmit() {
//        return btnSubmit;
//    }
//
//    /**
//     * NEW: Allows the Controller to get a specific tracker button (0-9)
//     * to add navigation listeners (e.g., jump to question X).
//     */
//    public JButton getTrackerButton(int index) {
//        if (index >= 0 && index < questionTrackerButtons.length) {
//            return questionTrackerButtons[index];
//        }
//        return null;
//    }
//
//    public void setQuestionText(String text) {
//        if (!text.toLowerCase().startsWith("<html>")) {
//            questionLabel.setText("<html><body style='text-align: center'>" + text + "</body></html>");
//        } else {
//            questionLabel.setText(text);
//        }
//    }
//
//    public void setOptions(String[] choices) {
//        if (choices == null) return;
//        for (int i = 0; i < choices.length && i < options.length; i++) {
//            options[i].setText(choices[i]);
//            options[i].setVisible(true);
//        }
//        for (int i = choices.length; i < options.length; i++) {
//            options[i].setText("");
//            options[i].setVisible(false);
//        }
//    }
//
//    public void setSelectedOption(int index) {
//        if (index >= 0 && index < options.length) {
//            options[index].setSelected(true);
//        } else {
//            optionsGroup.clearSelection();
//        }
//    }
//
//    public int getSelectedOptionIndex() {
//        for (int i = 0; i < options.length; i++) {
//            if (options[i].isSelected()) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    public void setQuestionAnswered(int questionIndex, boolean isAnswered) {
//        if (questionIndex >= 0 && questionIndex < questionTrackerButtons.length) {
//            questionTrackerButtons[questionIndex].setAnswered(isAnswered);
//        }
//    }
//
//    // =========================================================================
//    // HELPER SCREENS
//    // =========================================================================
//    private void initConfirmationScreen() {
//        confirmPanel = new JPanel(new GridBagLayout());
//        confirmPanel.setBackground(BG_DARK);
//
//        JLabel lbl = new JLabel("Submit Answers?");
//        lbl.setFont(mainFont.deriveFont(28f));
//        lbl.setForeground(Color.WHITE);
//        confirmPanel.add(lbl);
//
//        RoundedButton yes = new RoundedButton("Yes", CARD_GREEN, Color.BLACK);
//        RoundedButton no = new RoundedButton("No", CARD_RED, Color.BLACK);
//
//        JPanel p = new JPanel(); p.setOpaque(false); p.add(yes); p.add(no);
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridy=1; confirmPanel.add(p, gbc);
//
//        yes.addActionListener(e -> cardLayout.show(mainContainer, "RESULT"));
//        no.addActionListener(e -> cardLayout.show(mainContainer, "QUIZ"));
//
//        mainContainer.add(confirmPanel, "CONFIRM");
//    }
//
//    private void initResultScreen() {
//        resultPanel = new JPanel(new GridBagLayout());
//        resultPanel.setBackground(CARD_GREEN);
//        JLabel lbl = new JLabel("Quiz Finished!");
//        lbl.setFont(mainFont.deriveFont(30f));
//        resultPanel.add(lbl);
//        mainContainer.add(resultPanel, "RESULT");
//    }
//
//    // =========================================================================
//    // CUSTOM COMPONENTS
//    // =========================================================================
//
//    // 1. Tracker Button (1-10 with Dot)
//    private class QuestionNavButton extends JButton {
//        private boolean isAnswered = false;
//
//        public QuestionNavButton(String text) {
//            super(text);
//            setPreferredSize(new Dimension(45, 35));
//            setBorderPainted(false);
//            setFocusPainted(false);
//            setContentAreaFilled(false);
//            setFont(mainFont.deriveFont(Font.BOLD, 14f));
//            setForeground(Color.BLACK);
//            // Added Hand Cursor to indicate functionality
//            setCursor(new Cursor(Cursor.HAND_CURSOR));
//        }
//
//        public void setAnswered(boolean answered) {
//            this.isAnswered = answered;
//            repaint();
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            g2.setColor(TRACKER_BG);
//            g2.fillRect(0, 0, getWidth(), getHeight());
//
//            FontMetrics fm = g2.getFontMetrics();
//            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
//            int x = (getWidth() - stringBounds.width) / 2;
//            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
//            g2.setColor(getForeground());
//            g2.drawString(getText(), x, y);
//
//            if (isAnswered) {
//                g2.setColor(DOT_COLOR);
//                int dotSize = 8;
//                // Dot placed in top-right corner
//                g2.fill(new Ellipse2D.Double(getWidth() - dotSize - 4, 4, dotSize, dotSize));
//            }
//            g2.dispose();
//        }
//    }
//
//    // 2. Option Card Button
//    private class CustomOptionButton extends JToggleButton {
//        private Color baseColor;
//        private Color borderColor = new Color(145, 70, 255);
//
//        public CustomOptionButton(String text, Color bg) {
//            super(text);
//            this.baseColor = bg;
//            setFocusPainted(false);
//            setContentAreaFilled(false);
//            setBorderPainted(false);
//            setFont(mainFont.deriveFont(Font.PLAIN, 18f));
//            setForeground(Color.BLACK);
//            setPreferredSize(new Dimension(0, 200));
//            setCursor(new Cursor(Cursor.HAND_CURSOR));
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            int arc = 20;
//            RoundRectangle2D.Float shape = new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, arc, arc);
//
//            g2.setColor(baseColor);
//            g2.fill(shape);
//
//            if (isSelected()) {
//                g2.setStroke(new BasicStroke(4f));
//                g2.setColor(borderColor);
//                g2.draw(shape);
//            }
//
//            FontMetrics fm = g2.getFontMetrics();
//            Rectangle stringBounds = fm.getStringBounds(getText(), g2).getBounds();
//            int x = (getWidth() - stringBounds.width) / 2;
//            int y = (getHeight() - stringBounds.height) / 2 + fm.getAscent();
//
//            g2.setColor(getForeground());
//            g2.drawString(getText(), x, y);
//            g2.dispose();
//        }
//    }
//
//    // 3. Navigation Pill Button
//    private class RoundedButton extends JButton {
//        private Color bgColor;
//        private Color textColor;
//
//        public RoundedButton(String text, Color bg, Color txt) {
//            super(text);
//            this.bgColor = bg;
//            this.textColor = txt;
//            setContentAreaFilled(false);
//            setFocusPainted(false);
//            setBorderPainted(false);
//            setFont(mainFont.deriveFont(Font.PLAIN, 16f));
//            setPreferredSize(new Dimension(160, 50));
//            setCursor(new Cursor(Cursor.HAND_CURSOR));
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            Graphics2D g2 = (Graphics2D) g.create();
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//            if (getModel().isPressed()) g2.setColor(bgColor.darker());
//            else g2.setColor(bgColor);
//
//            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
//
//            g2.setColor(textColor);
//            FontMetrics fm = g2.getFontMetrics();
//            Rectangle sb = fm.getStringBounds(getText(), g2).getBounds();
//            int x = (getWidth() - sb.width) / 2;
//            int y = (getHeight() - sb.height) / 2 + fm.getAscent();
//            g2.drawString(getText(), x, y);
//            g2.dispose();
//        }
//    }
//}
