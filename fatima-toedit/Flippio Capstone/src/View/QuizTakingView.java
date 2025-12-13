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
            new Color(206, 199, 225),
            new Color(206, 199, 225),
            new Color(206, 199, 225),
            new Color(206, 199, 225)
    };

    // State Colors
    private final Color COLOR_OPT_CORRECT = new Color(124, 255, 160);
    private final Color COLOR_OPT_WRONG = new Color(255, 124, 124);
    private final Color COLOR_DISABLED = new Color(220, 220, 220);
    private final Color COLOR_NAV_BG = new Color(200, 180, 240);
    private final Color COLOR_FINISH_BG = new Color(45, 35, 85);

    // Selected Option Color (Darker Lavender)
    private final Color COLOR_OPT_SELECTED = new Color(78, 67, 124);

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
        // Window Setup - FULL WINDOW MODE
        setTitle("Flippio - Quiz");
        setMinimumSize(new Dimension(1200, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        topPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Use FlowLayout so unused buttons are hidden properly
        JPanel circlesContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        circlesContainer.setBackground(COLOR_BG_OUTER);

        // Initialize 10 circles (max capacity)
        questionCircles = new JButton[10];
        for (int i = 0; i < 10; i++) {
            questionCircles[i] = createCircleButton(String.valueOf(i + 1));
            questionCircles[i].setActionCommand(String.valueOf(i));
            circlesContainer.add(questionCircles[i]);
        }

        btnSubmit = createCustomButton("Finish Attempt", COLOR_FINISH_BG, Color.WHITE, 180, 50, 25);

        JPanel rightGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 40, 0));
        rightGroupPanel.setBackground(COLOR_BG_OUTER);
        rightGroupPanel.add(circlesContainer);
        rightGroupPanel.add(btnSubmit);

        topPanel.add(rightGroupPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // =================================================================
        // 2. CENTER: Floating Question Card (Responsive)
        // =================================================================
        JPanel outerCenterPanel = new JPanel(new GridBagLayout());
        outerCenterPanel.setBackground(COLOR_BG_OUTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 100, 60, 100);

        RoundedPanel cardPanel = new RoundedPanel(40, COLOR_BG_CARD);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBorder(new EmptyBorder(50, 60, 50, 60));

        // -- Question Text --
        questionLabel = new JLabel("Loading Question...", SwingConstants.CENTER);
        questionLabel.setFont(FontUtil.montserrat(32f, Font.BOLD, new Font("SansSerif", Font.BOLD, 32)));
        questionLabel.setForeground(COLOR_TEXT_WHITE);
        cardPanel.add(questionLabel, BorderLayout.NORTH);

        // -- Options Grid --
        JPanel optionsPanel = new JPanel(new GridLayout(1, 4, 30, 0));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(60, 0, 60, 0));

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton("Option " + (i + 1), i);
            optionsPanel.add(optionButtons[i]);
        }
        cardPanel.add(optionsPanel, BorderLayout.CENTER);

        // -- Navigation Buttons (Prev/Next) --
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 80, 10));
        navPanel.setOpaque(false);
        btnPrev = createCustomButton("Prev", COLOR_NAV_BG, Color.BLACK, 220, 60, 30);
        btnNext = createCustomButton("Next", COLOR_NAV_BG, Color.BLACK, 220, 60, 30);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        cardPanel.add(navPanel, BorderLayout.SOUTH);

        outerCenterPanel.add(cardPanel, gbc);
        add(outerCenterPanel, BorderLayout.CENTER);
    }

    // =================================================================
    // LOGIC & SETUP METHODS
    // =================================================================

    public void setupQuestionNavigator(int totalQuestions) {
        for (int i = 0; i < 10; i++) {
            questionCircles[i].setText(String.valueOf(i + 1));
            if (i < totalQuestions) {
                questionCircles[i].setVisible(true);
                questionCircles[i].setEnabled(true);
                questionCircles[i].setBackground(COLOR_CIRCLE_DEFAULT);
            } else {
                questionCircles[i].setVisible(false);
                questionCircles[i].setEnabled(false);
            }
        }
    }

    public void addNavigationListener(ActionListener listener) {
        for (JButton btn : questionCircles) {
            btn.addActionListener(listener);
        }
    }

    public void setQuestionText(String text) {
        questionLabel.setText("<html><div style='text-align: center; width: 600px;'>" + text + "</div></html>");
    }

    public void setOptions(String[] choices) {
        // Reset selection when loading new options
        selectedOptionIndex = -1;

        for (int i = 0; i < optionButtons.length; i++) {
            if (i < choices.length) {

                optionButtons[i].setText("<html><center>" + choices[i] + "</center></html>");
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
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < OPTION_COLORS.length) {
                optionButtons[i].setBackground(OPTION_COLORS[i]);
            }
        }
        for (JButton btn : optionButtons) {
            btn.repaint();
        }
    }

    public void setQuestionStatus(int questionIndex, int status) {
        if (questionIndex >= 0 && questionIndex < 10) {
            Color c = COLOR_CIRCLE_DEFAULT;
            if (status == 1) c = COLOR_CIRCLE_GREEN;
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
        String htmlText = "<html><body style='width: 230px; text-align: center'>" + text + "</body></html>";
        CustomHoverButton btn = new CustomHoverButton(htmlText, btnColor, Color.BLACK, 20);
        btn.setButtonIndex(index);
        btn.setFont(FontUtil.montserrat(18f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 18)));
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
        btn.setMargin(new Insets(0, 0, 0, 0));
        return btn;
    }

    // =================================================================
    // INNER CLASSES (Custom Components)
    // =================================================================

    class CustomHoverButton extends JButton {
        private Color normalBg;
        private Color defaultFg; // Store original text color
        private int radius;
        private boolean isHovered = false;
        private int buttonIndex = -1;

        public CustomHoverButton(String text, Color bg, Color fg, int radius) {
            super(text);
            this.normalBg = bg;
            this.defaultFg = fg; // Save the passed foreground color
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
                    if (isEnabled()) { isHovered = true; repaint(); }
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

            // --- 1. HANDLE TEXT COLOR CHANGE ---
            if (isEnabled() && buttonIndex != -1 && buttonIndex == selectedOptionIndex) {
                setForeground(Color.WHITE); // Turn text White if selected
            } else {
                setForeground(defaultFg);   // Revert to Black (or default) if not selected
            }

            // --- 2. DETERMINE BACKGROUND COLOR ---
            if (!isEnabled()) {
                paintColor = COLOR_DISABLED;
            }
            // If Selected, make the background Dark Lavender
            else if (buttonIndex != -1 && buttonIndex == selectedOptionIndex) {
                paintColor = COLOR_OPT_SELECTED;
            }
            else if (getModel().isPressed()) {
                paintColor = normalBg.darker();
            }

            // --- 3. DRAW BACKGROUND ---
            g2.setColor(paintColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // --- 4. DRAW BORDERS (Selected or Hovered) ---
            if (isEnabled() && buttonIndex != -1 && buttonIndex == selectedOptionIndex) {
                g2.setColor(Color.WHITE);
                // Thick 4px stroke
                g2.setStroke(new BasicStroke(6f));
                // margin thickness
                g2.drawRoundRect(4, 4, getWidth() - 8, getHeight() - 8, radius, radius);
            }
            else if (isEnabled() && isHovered) {
                g2.setColor(new Color(255, 255, 255, 200));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);
            }

            // --- 5. DRAW TEXT ---
            // Calls the parent paintComponent, which paints the text using the setForeground color set in Step 1
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
