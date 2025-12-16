package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import Utility.FontUtil;
import Utility.ResourceLoader;

public class LoginView extends JFrame {
    private JTextField idField;
    private JPasswordField passField;
    private JButton btnSubmitLogin;
    private JLabel loginLogoLabel;
    private JLabel loginLblId;
    private JLabel loginLblPass;
    private Box.Filler loginSignInGap;
    private JButton loginBackButton;

    // Signup Components
    private JTextField signNameField;
    private JTextField signIdField;
    private JComboBox<String> signRoleCombo;
    private JPasswordField signPassField;        // Create Password
    private JPasswordField signConfirmPassField; // Confirm Password
    private JButton btnSubmitSignup;
    private JButton signupBackButton;
    private JLabel signNameLabel;
    private JLabel signIdLabel;
    private JLabel signRoleLabel;
    private JLabel signPassLabel;
    private JLabel signConfirmPassLabel;         // Confirm Label

    // Navigation and Design
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private final Color COL_WHITE_BG = Color.WHITE;
    private final Color COL_PURPLE_BG = new Color(164, 172, 249); // #A4ACF9
    private final Color COL_BEIGE_INPUT = new Color(235, 207, 178); // #EBCFB2
    private final Color COL_DARK = new Color(23, 20, 29); // #17141D
    private final Font FONT_LABEL = FontUtil.montserrat(15f, Font.BOLD, new Font("SansSerif", Font.BOLD, 15));

    // Landing dynamic components
    private JLabel landingLogoLabel;
    private JButton btnGoToLogin;
    private JButton btnGoToCreate;
    private Box.Filler landingBtnGap;
    private Box.Filler signupRightGap;
    private Box.Filler signupRightTopGap;

    public LoginView() {
        setTitle("Flippio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(true);
        setLocationRelativeTo(null);

        ImageIcon winIcon = ResourceLoader.loadImageIcon("Logo.png");
        if (winIcon != null) {
            Image scaled = winIcon.getImage().getScaledInstance(40, 50, Image.SCALE_SMOOTH);
            setIconImage(scaled);
        } else {
            ImageIcon fallback = ResourceLoader.loadImageIcon("Prototype Design.png");
            if (fallback == null) fallback = ResourceLoader.loadImageIcon("Prototype Design.jpg");
            if (fallback != null) setIconImage(fallback.getImage());
        }

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(COL_WHITE_BG);
        outerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

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
        FontUtil.applyToTree(getContentPane(), FontUtil.montserrat(12f, Font.PLAIN, getFont()));

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateLandingScaling();
                updateSignupScaling();
                updateLoginScaling();
            }
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateLandingScaling();
                updateSignupScaling();
                updateLoginScaling();
            }
        });
    }

    //  LANDING PANEL
    private JPanel createLandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        landingLogoLabel = new JLabel();
        ImageIcon icon = null;
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null) {
            Image scaled = png.getImage().getScaledInstance(250, 100, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        if(icon != null){
            landingLogoLabel.setIcon(icon);
        } else {
            landingLogoLabel.setText("Flippio Logo");
            landingLogoLabel.setFont(FontUtil.montserrat(28f, Font.BOLD, new Font("SansSerif", Font.BOLD, 28)));
        }

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(landingLogoLabel, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        btnGoToLogin = new RoundedButton("Log In", COL_DARK, Color.WHITE);
        btnGoToCreate = new RoundedButton("Create an Account", new Color(0,0,0,0), COL_DARK);

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
        landingBtnGap = new Box.Filler(new Dimension(0,20), new Dimension(0,20), new Dimension(0,200));
        buttonPanel.add(landingBtnGap);
        buttonPanel.add(btnGoToCreate);

        gbc.gridx = 1;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    // SCALING METHODS
    private void updateLandingScaling() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        double scaleW = w / 1920.0;
        double scaleH = h / 1080.0;
        double baseScale = Math.min(scaleW, scaleH) * 2.0;
        double scale = Math.min(Math.max(baseScale, 1.0), 3.2);

        int logoW = (int) Math.round(250 * scale);
        int logoH = (int) Math.round(100 * scale);
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null) {
            Image scaled = png.getImage().getScaledInstance(logoW, logoH, Image.SCALE_SMOOTH);
            landingLogoLabel.setIcon(new ImageIcon(scaled));
        } else {
            landingLogoLabel.setFont(FontUtil.montserrat((float)(28 * scale), Font.BOLD, landingLogoLabel.getFont()));
        }

        int btnW = (int) Math.round(220 * scale);
        int btnH = (int) Math.round(45 * scale);
        Dimension btnSize = new Dimension(btnW, btnH);
        if (btnGoToLogin != null) {
            btnGoToLogin.setPreferredSize(btnSize);
            btnGoToLogin.setMaximumSize(btnSize);
            btnGoToLogin.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, (int) Math.round(14 * scale))));
        }
        if (btnGoToCreate != null) {
            btnGoToCreate.setPreferredSize(btnSize);
            btnGoToCreate.setMaximumSize(btnSize);
            btnGoToCreate.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, (int) Math.round(14 * scale))));
        }
        int gapH = (int) Math.round(30 * scale);
        if (landingBtnGap != null) {
            landingBtnGap.changeShape(new Dimension(0, gapH), new Dimension(0, gapH), new Dimension(0, 300));
        }
        revalidate();
        repaint();
    }

    private void updateSignupScaling() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        double scaleW = w / 1920.0;
        double scaleH = h / 1080.0;
        double baseScale = Math.min(scaleW, scaleH) * 2.0;
        double scale = Math.min(Math.max(baseScale, 1.0), 3.0);

        // Field Dimensions
        int fieldW = (int) Math.round(380 * scale);
        int fieldH = (int) Math.round(60 * scale);
        Dimension fieldSize = new Dimension(fieldW, fieldH);

        if (signNameField != null) { signNameField.setPreferredSize(fieldSize); signNameField.setMaximumSize(fieldSize); }
        if (signIdField != null) { signIdField.setPreferredSize(fieldSize); signIdField.setMaximumSize(fieldSize); }
        if (signRoleCombo != null) { signRoleCombo.setPreferredSize(fieldSize); signRoleCombo.setMaximumSize(fieldSize); }
        if (signPassField != null) { signPassField.setPreferredSize(fieldSize); signPassField.setMaximumSize(fieldSize); }
        if (signConfirmPassField != null) { signConfirmPassField.setPreferredSize(fieldSize); signConfirmPassField.setMaximumSize(fieldSize); }

        // Font Sizes
        int fieldFontSize = Math.max(16, (int) Math.round(18 * scale));
        Font fieldFont = FontUtil.montserrat((float) fieldFontSize, Font.PLAIN, new Font("SansSerif", Font.PLAIN, fieldFontSize));
        if (signNameField != null) signNameField.setFont(fieldFont);
        if (signIdField != null) signIdField.setFont(fieldFont);
        if (signRoleCombo != null) signRoleCombo.setFont(fieldFont);
        if (signPassField != null) signPassField.setFont(fieldFont);
        if (signConfirmPassField != null) signConfirmPassField.setFont(fieldFont);

        // Label Font Sizes
        int labelFontSize = Math.max(14, (int) Math.round(14 * scale));
        Font labelFont = FontUtil.montserrat((float) labelFontSize, Font.BOLD, new Font("SansSerif", Font.BOLD, labelFontSize));
        if (signNameLabel != null) signNameLabel.setFont(labelFont);
        if (signIdLabel != null) signIdLabel.setFont(labelFont);
        if (signRoleLabel != null) signRoleLabel.setFont(labelFont);
        if (signPassLabel != null) signPassLabel.setFont(labelFont);
        if (signConfirmPassLabel != null) signConfirmPassLabel.setFont(labelFont);

        if (btnSubmitSignup != null) {
            int btnW = (int) Math.round(240 * scale);
            int btnH = (int) Math.round(55 * scale);
            Dimension sz = new Dimension(btnW, btnH);
            btnSubmitSignup.setPreferredSize(sz);
            btnSubmitSignup.setMaximumSize(sz);
            btnSubmitSignup.setFont(FontUtil.montserrat((float)Math.max(14, (int) Math.round(16 * scale)), Font.BOLD, btnSubmitSignup.getFont()));
        }

        if (signupBackButton != null) {
            signupBackButton.setFont(FontUtil.montserrat((float)Math.max(12, (int) Math.round(14 * scale)), Font.BOLD, signupBackButton.getFont()));
        }

        int gapH = (int) Math.round(40 * scale);
        if (signupRightGap != null) {
            signupRightGap.changeShape(new Dimension(0, gapH), new Dimension(0, gapH), new Dimension(0, 400));
        }

        int topGapH = (int) Math.round(60 * scale);
        if (signupRightTopGap != null) {
            signupRightTopGap.changeShape(new Dimension(0, topGapH), new Dimension(0, topGapH), new Dimension(0, 300));
        }

        revalidate();
        repaint();
    }

    private void updateLoginScaling() {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;

        double scaleW = w / 1920.0;
        double scaleH = h / 1080.0;
        double baseScale = Math.min(scaleW, scaleH) * 2.0;
        double scale = Math.min(Math.max(baseScale, 1.0), 3.0);

        int logoW = (int) Math.round(300 * scale);
        int logoH = (int) Math.round(120 * scale);
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null && loginLogoLabel != null) {
            Image scaled = png.getImage().getScaledInstance(logoW, logoH, Image.SCALE_SMOOTH);
            loginLogoLabel.setIcon(new ImageIcon(scaled));
        } else if (loginLogoLabel != null) {
            loginLogoLabel.setFont(FontUtil.montserrat((float)(30 * scale), Font.BOLD, loginLogoLabel.getFont()));
        }

        int lblFontSize = Math.max(18, (int) Math.round(22 * scale));
        Font lblFont = FontUtil.montserrat((float) lblFontSize, Font.BOLD, new Font("SansSerif", Font.BOLD, lblFontSize));
        if (loginLblId != null) loginLblId.setFont(lblFont);
        if (loginLblPass != null) loginLblPass.setFont(lblFont);

        int fieldW = (int) Math.round(380 * scale);
        int fieldH = (int) Math.round(60 * scale);
        Dimension fieldSize = new Dimension(fieldW, fieldH);
        int fieldFontSize = Math.max(16, (int) Math.round(18 * scale));
        Font fieldFont = FontUtil.montserrat((float) fieldFontSize, Font.PLAIN, new Font("SansSerif", Font.PLAIN, fieldFontSize));
        if (idField != null) { idField.setPreferredSize(fieldSize); idField.setMaximumSize(fieldSize); idField.setFont(fieldFont); }
        if (passField != null) { passField.setPreferredSize(fieldSize); passField.setMaximumSize(fieldSize); passField.setFont(fieldFont); }

        if (btnSubmitLogin != null) {
            int btnW = (int) Math.round(260 * scale);
            int btnH = (int) Math.round(55 * scale);
            Dimension sz = new Dimension(btnW, btnH);
            btnSubmitLogin.setPreferredSize(sz);
            btnSubmitLogin.setMaximumSize(sz);
            btnSubmitLogin.setFont(FontUtil.montserrat((float)Math.max(14, (int) Math.round(16 * scale)), Font.BOLD, btnSubmitLogin.getFont()));
        }

        if (loginBackButton != null) {
            int backFontSize = Math.max(14, (int) Math.round(16 * scale));
            loginBackButton.setFont(FontUtil.montserrat((float) backFontSize, Font.BOLD, loginBackButton.getFont()));
            loginBackButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        }

        int loginGapH = (int) Math.round(40 * scale);
        if (loginSignInGap != null) {
            loginSignInGap.changeShape(new Dimension(0, loginGapH), new Dimension(0, loginGapH), new Dimension(0, 500));
        }

        revalidate();
        repaint();
    }

    // LOGIN PANEL
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 50, 50, 50));

        JButton btnBack = createBackButton();
        loginBackButton = btnBack;
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setOpaque(false);
        topBar.add(btnBack);
        panel.add(topBar, BorderLayout.NORTH);

        JPanel centerBox = new JPanel();
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setOpaque(false);

        loginLogoLabel = new JLabel();
        ImageIcon icon = null;
        ImageIcon png = ResourceLoader.loadImageIcon("Prototype Design.png");
        if (png == null) png = ResourceLoader.loadImageIcon("Prototype Design.jpg");
        if (png != null) {
            Image scaled = png.getImage().getScaledInstance(300, 120, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        }
        if(icon != null) {
            loginLogoLabel.setIcon(icon);
        } else {
            loginLogoLabel.setText("Flippio");
            loginLogoLabel.setFont(FontUtil.montserrat(30f, Font.BOLD, new Font("SansSerif", Font.BOLD, 30)));
        }
        loginLogoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginLblId = createLabel("ID Number:");
        loginLblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        idField = createBeigeField();
        idField.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginLblPass = createLabel("Password:");
        loginLblPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField = createBeigePasswordField();
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSubmitLogin = new RoundedButton("Sign In", COL_DARK, Color.WHITE);
        btnSubmitLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        idField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> btnSubmitLogin.doClick());

        centerBox.add(Box.createVerticalStrut(20));
        centerBox.add(loginLogoLabel);
        centerBox.add(Box.createVerticalStrut(30));

        centerBox.add(loginLblId);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(idField);
        centerBox.add(Box.createVerticalStrut(15));
        centerBox.add(loginLblPass);
        centerBox.add(Box.createVerticalStrut(5));
        centerBox.add(passField);

        loginSignInGap = new Box.Filler(new Dimension(0, 40), new Dimension(0, 40), new Dimension(0, 400));
        centerBox.add(loginSignInGap);
        centerBox.add(btnSubmitLogin);
        centerBox.add(Box.createVerticalGlue());

        panel.add(centerBox, BorderLayout.CENTER);

        return panel;
    }

    // SIGNUP PANEL
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(10, 10, 0, 10));
        signupBackButton = createBackButton();
        topBar.add(signupBackButton, BorderLayout.WEST);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        panel.add(topBar, gbc);
        gbc.gridwidth = 1;

        JPanel formBox = new JPanel();
        formBox.setLayout(new BoxLayout(formBox, BoxLayout.Y_AXIS));
        formBox.setOpaque(false);
        formBox.setBorder(new EmptyBorder(0, 50, 0, 50));

        // Name
        JLabel[] nameRef = new JLabel[1];
        signNameField = addLabelAndField(formBox, "Name:", nameRef);
        signNameLabel = nameRef[0];

        // ID
        JLabel[] idRef = new JLabel[1];
        signIdField = addLabelAndField(formBox, "ID Number:", idRef);
        signIdLabel = idRef[0];

        // Create Password
        JLabel passwordLabel = createLabel("Create Password:");
        passwordLabel.setFont(FontUtil.montserrat(14f, Font.BOLD, passwordLabel.getFont()));
        signPassLabel = passwordLabel;
        formBox.add(passwordLabel);
        formBox.add(Box.createVerticalStrut(5));
        signPassField = createBeigePasswordField();
        formBox.add(signPassField);
        formBox.add(Box.createVerticalStrut(15));

        // Confirm Password
        JLabel confirmPassLabel = createLabel("Confirm Password:");
        confirmPassLabel.setFont(FontUtil.montserrat(14f, Font.BOLD, confirmPassLabel.getFont()));
        signConfirmPassLabel = confirmPassLabel;
        formBox.add(confirmPassLabel);
        formBox.add(Box.createVerticalStrut(5));
        signConfirmPassField = createBeigePasswordField(); // Reuses logic with eye icon
        formBox.add(signConfirmPassField);
        formBox.add(Box.createVerticalStrut(15));

        // Role
        signRoleLabel = createLabel("Student / Teacher:");
        signRoleLabel.setFont(FontUtil.montserrat(14f, Font.BOLD, signRoleLabel.getFont()));
        formBox.add(signRoleLabel);
        formBox.add(Box.createVerticalStrut(5));
        signRoleCombo = createRoleDropdown();
        formBox.add(signRoleCombo);
        formBox.add(Box.createVerticalStrut(30));

        btnSubmitSignup = new RoundedButton("Create Account", COL_DARK, Color.WHITE);
        btnSubmitSignup.setPreferredSize(new Dimension(180, 40));

        // --- KEYBOARD NAVIGATION (TAB ORDER) ---
        signNameField.addActionListener(e -> signIdField.requestFocusInWindow());
        signIdField.addActionListener(e -> signPassField.requestFocusInWindow());
        signPassField.addActionListener(e -> signConfirmPassField.requestFocusInWindow());
        signConfirmPassField.addActionListener(e -> signRoleCombo.requestFocusInWindow());

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(formBox, gbc);

        // Right Side Image/Button Container
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
                int headSize = 100;
                g2.fillOval(cx - (headSize/2), cy - 80, headSize, headSize);
                g2.fillRoundRect(cx - 75, cy + 25, 150, 120, 90, 90);
            }
        };
        profilePanel.setOpaque(false);
        profilePanel.setPreferredSize(new Dimension(200, 250));

        signupRightTopGap = new Box.Filler(new Dimension(0, 40), new Dimension(0, 40), new Dimension(0, 200));
        rightGbc.gridx = 0;
        rightGbc.gridy = 0;
        rightGbc.anchor = GridBagConstraints.NORTH;
        rightContainer.add(signupRightTopGap, rightGbc);

        rightGbc.gridy = 1;
        rightContainer.add(profilePanel, rightGbc);

        signupRightGap = new Box.Filler(new Dimension(0, 30), new Dimension(0, 30), new Dimension(0, 300));
        rightGbc.gridy = 2;
        rightGbc.insets = new Insets(0, 0, 0, 0);
        rightContainer.add(signupRightGap, rightGbc);

        rightGbc.gridy = 3;
        rightContainer.add(btnSubmitSignup, rightGbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;
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
            int x = strokeWidth / 2;
            int y = strokeWidth / 2;
            g2.setColor(COL_PURPLE_BG);
            g2.fillRoundRect(x, y, w, h, arc, arc);
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

    private JTextField addLabelAndField(JPanel panel, String text, JLabel[] labelRef) {
        JLabel lbl = createLabel(text);
        lbl.setFont(FontUtil.montserrat(14f, Font.BOLD, lbl.getFont()));
        if (labelRef != null && labelRef.length > 0) {
            labelRef[0] = lbl;
        }
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        JTextField tf = createBeigeField();
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(tf);
        panel.add(Box.createVerticalStrut(15));
        return tf;
    }

    private JTextField createBeigeField() {
        JTextField tf = new JTextField();
        styleBeigeInput(tf);
        return tf;
    }

    private JComboBox<String> createRoleDropdown() {
        JComboBox<String> combo = new JComboBox<>(new String[] {"Student", "Teacher"});
        styleBeigeCombo(combo);
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }

    // -------------------------------------------------------------------------
    //  PASSWORD TOGGLE WITH IMAGES ON THE RIGHT
    // -------------------------------------------------------------------------
    private JPasswordField createBeigePasswordField() {
        JPasswordField pf = new JPasswordField();
        styleBeigeInput(pf);
        addPasswordToggle(pf);
        return pf;
    }

    private void addPasswordToggle(JPasswordField pf) {
        // Use BorderLayout to place the button on the Right (EAST)
        pf.setLayout(new BorderLayout());

        JButton toggleBtn = new JButton();
        toggleBtn.setBorderPainted(false);
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setFocusPainted(false);
        toggleBtn.setFocusable(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Define Icon Size
        int iconSize = 20;

        // Load Images
        ImageIcon rawShow = ResourceLoader.loadImageIcon("show.png");
        ImageIcon rawHide = ResourceLoader.loadImageIcon("hide.png");

        // Scale Images helper
        final ImageIcon showIcon;
        final ImageIcon hideIcon;

        if (rawShow != null) {
            showIcon = new ImageIcon(rawShow.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        } else {
            showIcon = null; // Fallback will be text if null
        }

        if (rawHide != null) {
            hideIcon = new ImageIcon(rawHide.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        } else {
            hideIcon = null;
        }

        // Set Initial Icon (Password is hidden by default, so show the "Show" icon)
        if (showIcon != null) {
            toggleBtn.setIcon(showIcon);
        } else {
            toggleBtn.setText("👁"); // Fallback text
        }

        toggleBtn.addActionListener(e -> {
            if (pf.getEchoChar() != (char) 0) {
                // Currently hidden, so SHOW it
                pf.setEchoChar((char) 0);
                if (hideIcon != null) {
                    toggleBtn.setIcon(hideIcon);
                } else {
                    toggleBtn.setText("🔒");
                }
                toggleBtn.setToolTipText("Hide Password");
            } else {
                // Currently shown, so HIDE it
                pf.setEchoChar('•');
                if (showIcon != null) {
                    toggleBtn.setIcon(showIcon);
                } else {
                    toggleBtn.setText("👁");
                }
                toggleBtn.setToolTipText("Show Password");
            }
        });

        // Add to the RIGHT (East)
        pf.add(toggleBtn, BorderLayout.EAST);

        // Adjust padding: 40px on the RIGHT so text doesn't overlap the button
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 40)
        ));
    }
    // -------------------------------------------------------------------------

    private void styleBeigeInput(JTextField tf) {
        tf.setBackground(COL_BEIGE_INPUT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        tf.setFont(FontUtil.montserrat(14f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 14)));
        tf.setPreferredSize(new Dimension(280, 40));
        tf.setMaximumSize(new Dimension(280, 40));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleBeigeCombo(JComboBox<String> combo) {
        combo.setBackground(COL_BEIGE_INPUT);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        combo.setFont(FontUtil.montserrat(14f, Font.PLAIN, new Font("SansSerif", Font.PLAIN, 14)));
        combo.setPreferredSize(new Dimension(280, 40));
        combo.setMaximumSize(new Dimension(280, 40));
    }

    private JButton createBackButton() {
        JButton btn = new JButton("< Back");
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

    // GETTERS & LISTENERS
    public String getIdInput() { return idField.getText(); }
    public String getPassInput() { return new String(passField.getPassword()); }

    public String getSignName() { return signNameField.getText(); }
    public String getSignId() { return signIdField.getText(); }
    public String getSignRole() {
        Object selected = (signRoleCombo != null) ? signRoleCombo.getSelectedItem() : null;
        return selected != null ? selected.toString() : "";
    }
    public String getSignPass() { return new String(signPassField.getPassword()); }

    // NEW: Getter for Confirm Password
    public String getSignConfirmPass() { return new String(signConfirmPassField.getPassword()); }

    public void resetSignupFormAndReturnToLanding() {
        if (signNameField != null) signNameField.setText("");
        if (signIdField != null) signIdField.setText("");
        if (signPassField != null) signPassField.setText("");
        if (signConfirmPassField != null) signConfirmPassField.setText(""); // Reset confirm pass
        if (signRoleCombo != null && signRoleCombo.getItemCount() > 0) {
            ActionListener[] listeners = signRoleCombo.getActionListeners();
            for (ActionListener al : listeners) {
                signRoleCombo.removeActionListener(al);
            }
            signRoleCombo.setSelectedIndex(0);
            for (ActionListener al : listeners) {
                signRoleCombo.addActionListener(al);
            }
        }
        cardLayout.show(cardContainer, "LANDING");
        getRootPane().setDefaultButton(null);
        SwingUtilities.invokeLater(() -> {
            if (btnGoToLogin != null) btnGoToLogin.requestFocusInWindow();
        });
    }

    public void addLoginListener(ActionListener listener) {
        btnSubmitLogin.addActionListener(listener);
    }
    public void addSignupListener(ActionListener listener) {
        if(btnSubmitSignup != null) btnSubmitSignup.addActionListener(listener);
    }
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

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
                g2.setColor(textColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            } else {
                g2.setColor(fillColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}