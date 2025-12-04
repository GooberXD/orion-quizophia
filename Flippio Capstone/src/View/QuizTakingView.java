package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Utility.FontUtil;
import Utility.ResourceLoader;

public class QuizTakingView extends JFrame {

    // --- Colors Constants ---
    private final Color COLOR_BG_OUTER = Color.WHITE;
    private final Color COLOR_BG_CARD = new Color(22, 22, 30); // Dark Card Background
    private final Color COLOR_TEXT_WHITE = Color.WHITE;

    // Specific Option Colors (Pastel tones)
    private final Color[] OPTION_COLORS = {
            new Color(163, 171, 249), // Option 1 (Purple-ish)
            new Color(163, 171, 249), // Option 2
            new Color(114, 245, 166), // Option 3 (Green-ish)
            new Color(255, 128, 128)  // Option 4 (Red-ish)
    };

    // State Colors
    private final Color COLOR_OPT_CORRECT = new Color(124, 255, 160);
    private final Color COLOR_OPT_WRONG = new Color(255, 124, 124);
    private final Color COLOR_DISABLED = new Color(220, 220, 220);
    private final Color COLOR_NAV_BG = new Color(200, 180, 240);
    private final Color COLOR_FINISH_BG = new Color(45, 35, 85);

    // Circle Navigation Colors
    private final Color COLOR_CIRCLE_DEFAULT = new Color(235, 207, 178); // Beige
    private final Color COLOR_CIRCLE_GREEN = new Color(110, 235, 150);   // Answered
    private final Color COLOR_CIRCLE_RED = new Color(255, 120, 120);     // Flagged/Wrong

    // Components
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JButton[] questionCircles; // The 1 to 10 circles at top
    private JButton btnPrev, btnNext, btnSubmit;

    // State tracking
    private int selectedOptionIndex = -1;

    public QuizTakingView() {
        // Window Setup
        setTitle("Flippio - Quiz");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG_OUTER);

        // Window Icon
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (appIcon == null) appIcon = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (appIcon != null) setIconImage(appIcon.getImage());

        // =================================================================
        // 1. TOP PANEL: Navigation Circles + Finish Button
        // =================================================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BG_OUTER);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel circlesContainer = new JPanel(new GridLayout(2, 5, 8, 8));
        circlesContainer.setBackground(COLOR_BG_OUTER);

        // Initialize 10 circles (max capacity)
        questionCircles = new JButton[10];
        for (int i = 0; i < 10; i++) {
            questionCircles[i] = createCircleButton(String.valueOf(i + 1));
            // IMPORTANT: Set ActionCommand to the index so Controller knows which question to load
            questionCircles[i].setActionCommand(String.valueOf(i));
            circlesContainer.add(questionCircles[i]);
        }

        btnSubmit = createCustomButton("Finish Attempt", COLOR_FINISH_BG, Color.WHITE, 160, 45, 20);

        // Right side wrapper
        JPanel rightGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        rightGroupPanel.setBackground(COLOR_BG_OUTER);
        rightGroupPanel.add(circlesContainer);
        rightGroupPanel.add(btnSubmit);

        topPanel.add(rightGroupPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // =================================================================
        // 2. CENTER: Floating Question Card
        // =================================================================
        JPanel outerCenterPanel = new JPanel(new GridBagLayout());
        outerCenterPanel.setBackground(COLOR_BG_OUTER);

        RoundedPanel cardPanel = new RoundedPanel(30, COLOR_BG_CARD);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(1000, 450));
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Question Text
        questionLabel = new JLabel("Loading Question...", SwingConstants.CENTER);
        questionLabel.setFont(FontUtil.montserrat(22f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 22)));
        questionLabel.setForeground(COLOR_TEXT_WHITE);
        cardPanel.add(questionLabel, BorderLayout.NORTH);

        // Options Grid
        JPanel optionsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(40, 0, 40, 0));

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton("Option " + (i + 1), i);
            optionsPanel.add(optionButtons[i]);
        }
        cardPanel.add(optionsPanel, BorderLayout.CENTER);

        // Navigation Buttons (Prev/Next)
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        navPanel.setOpaque(false);
        btnPrev = createCustomButton("Prev", COLOR_NAV_BG, Color.BLACK, 200, 50, 20);
        btnNext = createCustomButton("Next", COLOR_NAV_BG, Color.BLACK, 200, 50, 20);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        cardPanel.add(navPanel, BorderLayout.SOUTH);

        outerCenterPanel.add(cardPanel);
        add(outerCenterPanel, BorderLayout.CENTER);
    }

    // =================================================================
    // LOGIC & SETUP METHODS (Called by Controller)
    // =================================================================

    /**
     * Configures the top-right circles based on how many questions the quiz actually has.
     * E.g., if quiz has 5 questions, circles 6-10 are disabled.
     */
    public void setupQuestionNavigator(int totalQuestions) {
        for (int i = 0; i < 10; i++) {
            // Text is always "1", "2", etc.
            questionCircles[i].setText(String.valueOf(i + 1));

            if (i < totalQuestions) {
                questionCircles[i].setEnabled(true);
                questionCircles[i].setBackground(COLOR_CIRCLE_DEFAULT);
            } else {
                questionCircles[i].setEnabled(false);
                questionCircles[i].setBackground(COLOR_DISABLED);
            }
        }
    }

    public void addNavigationListener(ActionListener listener) {
        for (JButton btn : questionCircles) {
            btn.addActionListener(listener);
        }
    }

    public void setQuestionText(String text) {
        // Use HTML to wrap text if it's too long
        questionLabel.setText("<html><div style='text-align: center; width: 600px;'>" + text + "</div></html>");
    }

    public void setOptions(String[] choices) {
        // Reset selection when loading new options
        selectedOptionIndex = -1;

        for (int i = 0; i < optionButtons.length; i++) {
            if (i < choices.length) {
                optionButtons[i].setText(choices[i]);
                optionButtons[i].setVisible(true);
                // Reset color
                if (i < OPTION_COLORS.length) {
                    optionButtons[i].setBackground(OPTION_COLORS[i]);
                }
            } else {
                optionButtons[i].setVisible(false);
            }
        }
        repaint();
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOption(int index) {
        this.selectedOptionIndex = index;

        // Reset colors first
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < OPTION_COLORS.length) {
                optionButtons[i].setBackground(OPTION_COLORS[i]);
            }
        }
        // Force repaint to show the white border selection effect
        for (JButton btn : optionButtons) {
            btn.repaint();
        }
    }

    /**
     * Updates the color of the navigation circles based on status.
     * @param questionIndex The index of the question (0-9)
     * @param status 0 = Unanswered, 1 = Answered (Green)
     */
    public void setQuestionStatus(int questionIndex, int status) {
        if (questionIndex >= 0 && questionIndex < 10) {
            Color c = COLOR_CIRCLE_DEFAULT;
            if (status == 1) c = COLOR_CIRCLE_GREEN; // Answered

            if (questionCircles[questionIndex].isEnabled()) {
                questionCircles[questionIndex].setBackground(c);
            }
        }
    }

    // Getters
    public JButton getBtnNext() { return btnNext; }
    public JButton getBtnPrev() { return btnPrev; }
    public JButton getBtnSubmit() { return btnSubmit; }

    // =================================================================
    // UI COMPONENT HELPERS
    // =================================================================

    private JButton createOptionButton(String text, int index) {
        Color btnColor = (index < OPTION_COLORS.length) ? OPTION_COLORS[index] : OPTION_COLORS[0];
        CustomHoverButton btn = new CustomHoverButton(text, btnColor, Color.BLACK, 20);
        btn.setButtonIndex(index); // Link button to its index for selection logic
        btn.setFont(FontUtil.montserrat(18f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 18)));
        // Self-contained listener for selection visual
        btn.addActionListener(e -> setSelectedOption(index));
        return btn;
    }

    private JButton createCustomButton(String text, Color bg, Color fg, int width, int height, int radius) {
        JButton btn = new CustomHoverButton(text, bg, fg, radius);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setFont(FontUtil.montserrat(14f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 14)));
        return btn;
    }

    private JButton createCircleButton(String text) {
        JButton btn = new CustomHoverButton(text, COLOR_CIRCLE_DEFAULT, Color.BLACK, 30);
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setFont(FontUtil.montserrat(11f, Font.BOLD, new Font("SansSerif", Font.BOLD, 11)));
        // Fix for text truncation on small buttons
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    // =================================================================
    // INNER CLASSES (Custom Components)
    // =================================================================

    class CustomHoverButton extends JButton {
        private Color normalBg;
        private Color foregroundColor;
        private int radius;
        private boolean isHovered = false;
        private int buttonIndex = -1; // -1 means it's not an option button

        public CustomHoverButton(String text, Color bg, Color fg, int radius) {
            super(text);
            this.normalBg = bg;
            this.foregroundColor = fg;
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if(isEnabled()) { isHovered = true; repaint(); }
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false; repaint();
                }
            });
        }

        @Override
        public void setEnabled(boolean b) {
            super.setEnabled(b);
            if (!b) {
                isHovered = false;
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            repaint();
        }

        public void setButtonIndex(int index) { this.buttonIndex = index; }

        @Override
        public void setBackground(Color bg) {
            this.normalBg = bg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color paintColor = normalBg;
            if (!isEnabled()) paintColor = COLOR_DISABLED;
            else if (getModel().isPressed()) paintColor = normalBg.darker();

            g2.setColor(paintColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Draw White Border if this is the Selected Option
            if (isEnabled() && buttonIndex != -1 && buttonIndex == selectedOptionIndex) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, radius, radius);
            }
            // Draw lighter border on Hover
            else if (isEnabled() && isHovered) {
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radius, radius);
            }
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
        }
    }
}