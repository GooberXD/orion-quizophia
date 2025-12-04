package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import Utility.FontUtil;
import Utility.ResourceLoader;

public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private JButton btnSubmitLogin;

    // Signup Components
    private JTextField signNameField;
    private JTextField signIdField;
    private JTextField signRoleField;
    private JPasswordField signPassField;
    private JButton btnSubmitSignup;

    // Navigation and Design
    private CardLayout cardLayout;
    private JPanel cardContainer; // The container inside the purple box
    private final Color COL_WHITE_BG = Color.WHITE;
    private final Color COL_PURPLE_BG = new Color(164, 172, 249); // #A4ACF9
    private final Color COL_BEIGE_INPUT = new Color(235, 207, 178); // #EBCFB2
    private final Color COL_DARK = new Color(23, 20, 29); // #17141D
    private final Font FONT_LABEL = FontUtil.montserrat(15f, Font.BOLD, new Font("SansSerif", Font.BOLD, 15));

    public LoginView() {
        setTitle("Flippio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setResizable(true);
        // Window icon: use Logo.png from Resources (scaled to 60x60), fallback to Prototype Design assets
        ImageIcon winIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (winIcon != null) {
            Image scaled = winIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            setIconImage(scaled);
        } else {
            ImageIcon fallback = ResourceLoader.loadImageIcon("Prototype Design.png");
            if (fallback == null) fallback = ResourceLoader.loadImageIcon("Prototype Design.jpg");
            if (fallback != null) setIconImage(fallback.getImage());
        }

        // Outer White Background
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(COL_WHITE_BG);
        // Padding to create the white border effect around the purple box
        outerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Rounded Purple Box
        RoundedPanel purpleCard = new RoundedPanel();
        purpleCard.setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setOpaque(false);
        cardContainer.add(createLandingPanel(), "LANDING");
        cardContainer.add(createLoginPanel(), "LOGIN_FORM");
        cardContainer.add(createSignupPanel(), "SIGNUP_FORM");

        purpleCard.add(cardContainer, BorderLayout.CENTER);
        outerPanel.add(purpleCard, BorderLayout.CENTER);

        setContentPane(outerPanel);
        // Apply global Montserrat to the whole view
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));
    }

    //  LANDING PANEL
    private JPanel createLandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Left: Logo
        JLabel logoLabel = new JLabel();
        // Load Prototype Design logo (keep existing large panel logo)
        ImageIcon icon = null;
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null) {
            // Adjusted size per request: 260 x 130
            Image scaled = png.getImage().getScaledInstance(260, 130, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        if(icon != null){
            logoLabel.setIcon(icon);
        } else {
            logoLabel.setText("Flippio Logo");
            logoLabel.setFont(FontUtil.montserrat(28f, Font.BOLD, new Font("SansSerif", Font.BOLD, 28)));
        }

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton btnGoToLogin = new RoundedButton("Log In", COL_DARK, Color.WHITE);
        JButton btnGoToCreate = new RoundedButton("Create an Account", new Color(0,0,0,0), COL_DARK);

        // Navigation
        btnGoToLogin.addActionListener(e -> {
            cardLayout.show(cardContainer, "LOGIN_FORM");
            getRootPane().setDefaultButton(btnSubmitLogin);
            SwingUtilities.invokeLater(() -> idField.requestFocusInWindow());
        });

        btnGoToCreate.addActionListener(e -> {
            cardLayout.show(cardContainer, "SIGNUP_FORM");
            getRootPane().setDefaultButton(btnSubmitSignup);
            SwingUtilities.invokeLater(() -> signNameField.requestFocusInWindow());
        });

        buttonPanel.add(btnGoToLogin);
        buttonPanel.add(Box.createVerticalStrut(20));
        buttonPanel.add(btnGoToCreate);

        gbc.gridx = 1;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // LOGIN PANEL
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 50, 50, 50));

        // Back Button at top-left
        JButton btnBack = createBackButton();
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        topBar.add(btnBack);
        panel.add(topBar, BorderLayout.NORTH);

        // Center content
        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        // Logo (Smaller for Login screen)
        JLabel logoLabel = new JLabel();
        ImageIcon icon = null;
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null) {
            // Adjusted size per request: 200 x 90
            Image scaled = png.getImage().getScaledInstance(200, 90, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        if(icon != null) {
            // Use scaled icon for this view
            logoLabel.setIcon(icon);
        } else {
            logoLabel.setText("Flippio");
            logoLabel.setFont(FontUtil.montserrat(30f, Font.BOLD, new Font("SansSerif", Font.BOLD, 30)));
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Inputs
        JLabel lblId = createLabel("ID Number:");
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        idField = createBeigeField();

        JLabel lblPass = createLabel("Password:");
        lblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField = createBeigePasswordField();

        // Submit Button
        btnSubmitLogin = new RoundedButton("Sign In", COL_DARK, Color.WHITE);
        btnSubmitLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        // keyboard
        idField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> btnSubmitLogin.doClick());

        centerBox.add(Box.createVerticalStrut(20));
        centerBox.add(logoLabel);
        centerBox.add(Box.createVerticalStrut(30));

        // Field Group
        centerBox.add(lblId);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(idField);
        centerBox.add(Box.createVerticalStrut(15));
        centerBox.add(lblPass);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(passField);
        centerBox.add(Box.createVerticalStrut(30));
        centerBox.add(btnSubmitLogin);

        panel.add(centerBox, BorderLayout.CENTER);

        return panel;
    }

    // SIGNUP PANEL
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Form
        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(0, 50, 0, 50));

        JButton btnBack = createBackButton();
        formBox.add(btnBack);
        formBox.add(Box.createVerticalStrut(20));

        // Fields
        signNameField = addLabelAndField(formBox, "Name:");
        signIdField = addLabelAndField(formBox, "ID Number:");
        signRoleField = addLabelAndField(formBox, "Student / Teacher:");

        formBox.add(createLabel("Password:"));
        formBox.add(Box.createVerticalStrut(5));
        signPassField = createBeigePasswordField();
        formBox.add(signPassField);
        formBox.add(Box.createVerticalStrut(30));


        btnSubmitSignup = new RoundedButton("Create Account", COL_DARK, Color.WHITE);
        btnSubmitSignup.setPreferredSize(new Dimension(180, 40));

        // keyboard
        signNameField.addActionListener(e -> signIdField.requestFocusInWindow());
        signIdField.addActionListener(e -> signRoleField.requestFocusInWindow());
        signRoleField.addActionListener(e -> signPassField.requestFocusInWindow());
        signPassField.addActionListener(e -> btnSubmitSignup.doClick());

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(formBox, gbc);

        // Right side container with profile icon and button
        JPanel rightContainer = new JPanel(new GridBagLayout());
        rightContainer.setOpaque(false);
        GridBagConstraints rightGbc = new GridBagConstraints();

        JPanel profilePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                g2.setColor(COL_DARK);

                // Head
                int headSize = 100;
                g2.fillOval(cx - (headSize/2), cy - 80, headSize, headSize);

                // Body (Shoulders)
                // Using a GeneralPath for smoother shoulder look or simple Arc
                g2.fillRoundRect(cx - 75, cy + 25, 150, 120, 90, 90);
            }
        };
        profilePanel.setOpaque(false);
        profilePanel.setPreferredSize(new Dimension(200, 250));

        // Add profile icon
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.anchor = GridBagConstraints.CENTER;
        rightContainer.add(profilePanel, rightGbc);

        // Add button below icon
        rightGbc.gridy = 1;
        rightGbc.insets = new Insets(20, 0, 0, 0);
        rightContainer.add(btnSubmitSignup, rightGbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(rightContainer, gbc);

        return panel;
    }
    class RoundedPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int strokeWidth = 3;
            int arc = 40;
            int w = getWidth() - strokeWidth;
            int h = getHeight() - strokeWidth;

            // Shift x,y by stroke/2 to keep outline inside bounds
            int x = strokeWidth / 2;
            int y = strokeWidth / 2;

            // 1. Draw Background
            g2.setColor(COL_PURPLE_BG);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            // 2. Draw Outline
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawRoundRect(x, y, w, h, arc, arc);
        }
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField addLabelAndField(JPanel panel, String text) {
        panel.add(createLabel(text));
        panel.add(Box.createVerticalStrut(5));
        JTextField tf = createBeigeField();
        panel.add(tf);
        panel.add(Box.createVerticalStrut(15));
        return tf;
    }

    private JTextField createBeigeField() {
        JTextField tf = new JTextField();
        styleBeigeInput(tf);
        return tf;
    }

    private JPasswordField createBeigePasswordField() {
        JPasswordField pf = new JPasswordField();
        styleBeigeInput(pf);
        return pf;
    }

    private void styleBeigeInput(JTextField tf) {
        tf.setBackground(COL_BEIGE_INPUT);
        // Thick black border
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tf.setFont(FontUtil.montserrat(14f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 14)));
        tf.setPreferredSize(new Dimension(280, 40));
        tf.setMaximumSize(new Dimension(280, 40));
        tf.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton createBackButton() {
        JButton btn = new JButton("← Back");
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFont(FontUtil.montserrat(12f, Font.BOLD, new Font("SansSerif", Font.BOLD, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            cardLayout.show(cardContainer, "LANDING");
            getRootPane().setDefaultButton(null);
        });
        return btn;
    }

    // =========================================================
    // GETTERS & LISTENERS (For Controller)
    // =========================================================
    public String getIdInput() { return idField.getText(); }
    public String getPassInput() { return new String(passField.getPassword()); }

    // Sign Up Getters
    public String getSignName() { return signNameField.getText(); }
    public String getSignId() { return signIdField.getText(); }
    public String getSignRole() { return signRoleField.getText(); }
    public String getSignPass() { return new String(signPassField.getPassword()); }

    public void addLoginListener(ActionListener listener) {
        btnSubmitLogin.addActionListener(listener);
    }
    public void addSignupListener(ActionListener listener) {
        if(btnSubmitSignup != null) btnSubmitSignup.addActionListener(listener);
    }
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // =========================================================
    // ROUNDED BUTTON CLASS
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
            Dimension size = new Dimension(220, 45);
            setPreferredSize(size);
            setMaximumSize(size);
            setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (fillColor.getAlpha() == 0) {
                // Outline Style
                g2.setColor(textColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            } else {
                // Filled Style
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}