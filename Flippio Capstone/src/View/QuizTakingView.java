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

    // --- Colors ---
    private final Color COLOR_BG_OUTER = Color.WHITE;
    private final Color COLOR_BG_CARD = new Color(22, 22, 30);
    private final Color COLOR_TEXT_WHITE = Color.WHITE;

    // Specific Option Colors
    private final Color[] OPTION_COLORS = {
            new Color(163, 171, 249), // Option 1
            new Color(163, 171, 249), // Option 2
            new Color(114, 245, 166), // Option 3
            new Color(255, 128, 128)  // Option 4
    };

    // State Colors
    private final Color COLOR_OPT_CORRECT = new Color(124, 255, 160);
    private final Color COLOR_OPT_WRONG = new Color(255, 124, 124);
    private final Color COLOR_DISABLED = new Color(220, 220, 220); // Grey for inactive buttons
    private final Color COLOR_NAV_BG = new Color(200, 180, 240);
    private final Color COLOR_FINISH_BG = new Color(45, 35, 85);
    private final Color COLOR_CIRCLE_DEFAULT = new Color(235, 207, 178);
    private final Color COLOR_CIRCLE_GREEN = new Color(110, 235, 150);
    private final Color COLOR_CIRCLE_RED = new Color(255, 120, 120);

    // Components
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JButton[] questionCircles;
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

        // Icon
        ImageIcon appIcon = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (appIcon == null) appIcon = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (appIcon != null) setIconImage(appIcon.getImage());

        // =================================================================
        // TOP PANEL: Tiny Ball Circles + Finish Button
        // =================================================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BG_OUTER);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel circlesContainer = new JPanel(new GridLayout(2, 5, 8, 8));
        circlesContainer.setBackground(COLOR_BG_OUTER);

        questionCircles = new JButton[10];
        for (int i = 0; i < 10; i++) {
            // Initialize with "1", "2", etc.
            questionCircles[i] = createCircleButton(String.valueOf(i + 1));
            // Set ActionCommand to index (0-9) for Controller
            questionCircles[i].setActionCommand(String.valueOf(i));
            circlesContainer.add(questionCircles[i]);
        }

        btnSubmit = createCustomButton("Finish Attempt", COLOR_FINISH_BG, Color.WHITE, 160, 45, 20);

        JPanel rightGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        rightGroupPanel.setBackground(COLOR_BG_OUTER);
        rightGroupPanel.add(circlesContainer);
        rightGroupPanel.add(btnSubmit);

        topPanel.add(rightGroupPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // =================================================================
        // CENTER: Floating Card
        // =================================================================
        JPanel outerCenterPanel = new JPanel(new GridBagLayout());
        outerCenterPanel.setBackground(COLOR_BG_OUTER);

        RoundedPanel cardPanel = new RoundedPanel(30, COLOR_BG_CARD);
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(1000, 450));
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        questionLabel = new JLabel("Loading Question...", SwingConstants.CENTER);
        questionLabel.setFont(FontUtil.montserrat(22f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 22)));
        questionLabel.setForeground(COLOR_TEXT_WHITE);
        cardPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(new EmptyBorder(40, 0, 40, 0));

        optionButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createOptionButton("Option " + (i + 1), i);
            optionsPanel.add(optionButtons[i]);
        }
        cardPanel.add(optionsPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        navPanel.setOpaque(false);
        btnPrev = createCustomButton("Prev", COLOR_NAV_BG, Color.BLACK, 200, 50, 20);
        btnNext = createCustomButton("Next", COLOR_NAV_BG, Color.BLACK, 200, 50, 20);

        btnPrev.setEnabled(true);
        btnNext.setEnabled(true);

        navPanel.add(btnPrev);
        navPanel.add(btnNext);
        cardPanel.add(navPanel, BorderLayout.SOUTH);

        outerCenterPanel.add(cardPanel);
        add(outerCenterPanel, BorderLayout.CENTER);
    }

    // =================================================================
    //  LOGIC & SETUP METHODS
    // =================================================================


    public void setupQuestionNavigator(int totalQuestions) {
        for (int i = 0; i < 10; i++) {
            // ALWAYS set the text to the number (1-10) to avoid "..."
            questionCircles[i].setText(String.valueOf(i + 1));

            if (i < totalQuestions) {
                // Active Button (part of the quiz)
                questionCircles[i].setEnabled(true);
                questionCircles[i].setBackground(COLOR_CIRCLE_DEFAULT);
            } else {
                // Inactive Button (beyond the quiz size)
                // It shows the number but is disabled (greyed out)
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
        selectedOptionIndex = -1;
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < choices.length) {
                optionButtons[i].setText(choices[i]);
                optionButtons[i].setVisible(true);
            } else {
                optionButtons[i].setVisible(false);
            }
            if (i < OPTION_COLORS.length) {
                optionButtons[i].setBackground(OPTION_COLORS[i]);
            }
        }
        repaint();
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    public void setSelectedOption(int index) {
        selectedOptionIndex = index;
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < OPTION_COLORS.length) {
                optionButtons[i].setBackground(OPTION_COLORS[i]);
            }
        }
        for (JButton btn : optionButtons) {
            btn.repaint();
        }
    }

    public void highlightOption(int index, boolean isCorrect) {
        if (index >= 0 && index < optionButtons.length) {
            optionButtons[index].setBackground(isCorrect ? COLOR_OPT_CORRECT : COLOR_OPT_WRONG);
        }
    }

    public void setQuestionStatus(int questionIndex, int status) {
        // status: 0=default, 1=correct, 2=wrong
        if (questionIndex >= 0 && questionIndex < 10) {
            Color c = COLOR_CIRCLE_DEFAULT;
            if (status == 1) c = COLOR_CIRCLE_GREEN;
            else if (status == 2) c = COLOR_CIRCLE_RED;

            if (questionCircles[questionIndex].isEnabled()) {
                questionCircles[questionIndex].setBackground(c);
            }
        }
    }

    // Getters
    public JButton getBtnNext() { return btnNext; }
    public JButton getBtnPrev() { return btnPrev; }
    public JButton getBtnSubmit() { return btnSubmit; }
    public JButton[] getQuestionCircles() { return questionCircles; }

    // =================================================================
    // UI HELPERS
    // =================================================================

    private JButton createOptionButton(String text, int index) {
        Color btnColor = (index < OPTION_COLORS.length) ? OPTION_COLORS[index] : OPTION_COLORS[0];
        CustomHoverButton btn = new CustomHoverButton(text, btnColor, Color.BLACK, 20);
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

        // FIX: Set Margin to 0.
        // Default Swing margins are too wide for a 35px button, causing "..." truncation.
        btn.setMargin(new Insets(0, 0, 0, 0));

        return btn;
    }

    class CustomHoverButton extends JButton {
        private Color normalBg;
        private Color foregroundColor;
        private int radius;
        private boolean isHovered = false;
        private int buttonIndex = -1;

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

            if (isEnabled() && buttonIndex != -1 && buttonIndex == selectedOptionIndex) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(5f));
                g2.drawRoundRect(2, 2, getWidth()-5, getHeight()-5, radius, radius);
            } else if (isEnabled() && isHovered) {
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
