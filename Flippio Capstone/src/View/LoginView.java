package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class LoginView extends JFrame {

    // --- Components required by the Controller ---
    private JTextField idField;
    private JPasswordField passField;
    private JButton btnSubmitLogin; // The actual button the controller listens to

    // --- Navigation Components ---
    private JPanel cards; // The container for switching views
    private CardLayout cardLayout;

    // --- Design Constants ---
    private final Color COLOR_BG = new Color(205, 185, 240); // Lavender
    private final Color COLOR_DARK = new Color(25, 25, 25);
    private final Font FONT_MAIN = new Font("Montserrat", Font.BOLD, 14);

    public LoginView() {
        // 1. Window Setup
        setTitle("Flippio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setResizable(true);

        // 2. Main Layout (CardLayout to switch between Landing and Forms)
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        cards.setBackground(COLOR_BG);

        // 3. Create the Panels
        JPanel landingPanel = createLandingPanel();
        JPanel loginFormPanel = createLoginFormPanel();
        JPanel createAccountPanel = createSignupPlaceholder();

        // 4. Add them to the Card Deck
        cards.add(landingPanel, "LANDING");
        cards.add(loginFormPanel, "LOGIN_FORM");
        cards.add(createAccountPanel, "SIGNUP_FORM");

        setContentPane(cards);
    }

    // =========================================================
    // PANEL 1: LANDING PAGE (Logo + 2 Options)
    // =========================================================
    private JPanel createLandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();

        // --- Left Side: Logo ---
        JLabel logoLabel = new JLabel();
        ImageIcon icon = loadLogoIcon("flippio_logo.svg");
        if(icon != null) {
            logoLabel.setIcon(icon);
        } else {
            logoLabel.setText("Logo Missing");
        }

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        panel.add(logoLabel, gbc);

        // --- Right Side: Navigation Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton btnGoToLogin = new RoundedButton("Log In", COLOR_DARK, Color.WHITE);
        JButton btnGoToCreate = new RoundedButton("Create an Account", new Color(0,0,0,0), COLOR_DARK);

        // --- NAVIGATION & FOCUS LOGIC ---
        btnGoToLogin.addActionListener(e -> {
            cardLayout.show(cards, "LOGIN_FORM");

            // KEYBOARD RESPONSIVENESS:
            // 1. Set the "Sign In" button as the default (Enter key triggers it)
            getRootPane().setDefaultButton(btnSubmitLogin);

            // 2. Move cursor to the ID field immediately
            SwingUtilities.invokeLater(() -> idField.requestFocusInWindow());
        });

        btnGoToCreate.addActionListener(e -> {
            cardLayout.show(cards, "SIGNUP_FORM");
            getRootPane().setDefaultButton(null); // Reset default button
        });

        buttonPanel.add(btnGoToLogin);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnGoToCreate);

        gbc.gridx = 1;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // =========================================================
    // PANEL 2: LOGIN FORM (Inputs + Submit)
    // =========================================================
    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Back Button
        JButton btnBack = new JButton("← Back");
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Navigation Logic
        btnBack.addActionListener(e -> {
            cardLayout.show(cards, "LANDING");
            getRootPane().setDefaultButton(null); // Reset Enter key behavior
        });

        // Inputs
        JLabel lblId = new JLabel("ID / Username");
        lblId.setFont(FONT_MAIN);
        idField = new JTextField();
        styleField(idField);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(FONT_MAIN);
        passField = new JPasswordField();
        styleField(passField);

        // THE BUTTON CONTROLLER LISTENS TO
        btnSubmitLogin = new RoundedButton("Sign In", COLOR_DARK, Color.WHITE);

        // --- KEYBOARD LISTENERS ---

        // 1. Pressing Enter in ID Field -> Jumps to Password Field
        idField.addActionListener(e -> passField.requestFocusInWindow());

        // 2. Pressing Enter in Password Field -> Simulates Click on Submit Button
        passField.addActionListener(e -> btnSubmitLogin.doClick());

        // Layout Adding
        formBox.add(btnBack);
        formBox.add(Box.createVerticalStrut(20));
        formBox.add(lblId);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(idField);
        formBox.add(Box.createVerticalStrut(15));
        formBox.add(lblPass);
        formBox.add(Box.createVerticalStrut(5));
        formBox.add(passField);
        formBox.add(Box.createVerticalStrut(30));
        formBox.add(btnSubmitLogin);

        panel.add(formBox);
        return panel;
    }

    private JPanel createSignupPlaceholder() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        JLabel lbl = new JLabel("Create Account Feature Coming Soon");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> cardLayout.show(cards, "LANDING"));

        JPanel container = new JPanel(new FlowLayout());
        container.setOpaque(false);
        container.add(lbl);
        container.add(btnBack);

        panel.add(container);
        return panel;
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private void styleField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(250, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_DARK, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(new Color(255, 255, 255, 220));
    }

    private ImageIcon loadLogoIcon(String path) {
        File f = new File(path);
        if(f.exists()) {
            return new ImageIcon(path);
        }
        return null;
    }

    // =========================================================
    // METHODS REQUIRED BY CONTROLLER
    // =========================================================

    public String getIdInput() {
        return idField.getText();
    }

    public String getPassInput() {
        return new String(passField.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        btnSubmitLogin.addActionListener(listener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // CUSTOM ROUNDED BUTTON
    // =========================================================
    class RoundedButton extends JButton {
        private Color fillColor;
        private Color textColor;

        public RoundedButton(String text, Color fill, Color textCol) {
            super(text);
            this.fillColor = fill;
            this.textColor = textCol;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(textColor);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Fixed size for buttons
            Dimension size = new Dimension(200, 50);
            setPreferredSize(size);
            setMaximumSize(size);
            setMinimumSize(size);
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (fillColor.getAlpha() == 0) {
                // Outline Style
                g2.setColor(textColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.setStroke(new BasicStroke(2));
            } else {
                // Filled Style
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}
