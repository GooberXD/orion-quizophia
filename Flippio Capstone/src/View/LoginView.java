package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    // Fields matching your prototype [cite: 34, 35]
    private JTextField idField = new JTextField(20);
    private JPasswordField passField = new JPasswordField(20);
    private JButton signInButton = new JButton("Sign In");

    public LoginView() {
        // Setup Window
        this.setTitle("Flippio - Login"); // [cite: 36]
        this.setSize(400, 300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(4, 1));
        this.setLocationRelativeTo(null);

        // Create UI Elements based on prototype
        JPanel panel = new JPanel();
        panel.add(new JLabel("ID Number:")); // [cite: 34]
        panel.add(idField);
        panel.add(new JLabel("Password:")); // [cite: 35]
        panel.add(passField);
        panel.add(signInButton); // [cite: 37]

        this.add(panel);
    }

    // Method to attach the Controller (Listener)
    public void addLoginListener(ActionListener listener) {
        signInButton.addActionListener(listener);
    }

    public String getIdInput() { return idField.getText(); }
    public String getPassInput() { return new String(passField.getPassword()); }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
