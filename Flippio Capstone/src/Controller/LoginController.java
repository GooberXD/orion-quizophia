package Controller;

import View.*;
import Model.*;
import Service.*;
import Exception.*; // Import custom exceptions

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LoginController {
    private LoginView view;
    private AuthService authService;

    public LoginController(LoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        // 1. Attach Login Listener
        this.view.addLoginListener(e -> performLogin());

        // 2. Attach Signup Listener
        this.view.addSignupListener(e -> performSignup());
    }

    // --- LOGIN LOGIC ---
    private void performLogin() {
        try {
            String id = view.getIdInput();
            String pass = view.getPassInput();

            if(id.isEmpty() || pass.isEmpty()) {
                view.showErrorMessage("Please fill in all login fields.");
                return;
            }

            User user = authService.login(id, pass);

            if (user instanceof Student) {
                view.dispose();
                openStudentDashboard((Student) user);
            } else if (user instanceof Teacher) {
                view.dispose();
                openTeacherDashboard((Teacher) user);
            }

        } catch (Exception ex) {
            view.showErrorMessage("Login Failed: " + ex.getMessage());
        }
    }

    // --- SIGNUP LOGIC ---
    private void performSignup() {
        try {
            // 1. Get Data from View
            String name = view.getSignName();
            String id = view.getSignId();
            String role = view.getSignRole();
            String pass = view.getSignPass();

            // 2. Input Validation
            if (name.isEmpty() || id.isEmpty() || role.isEmpty() || pass.isEmpty()) {
                view.showErrorMessage("Please fill in all registration fields.");
                return;
            }

            // 3. Call Service to Register
            // Note: The Service handles checking for duplicates and saving to file
            authService.register(id, pass, name, role.trim());

            // 4. Success Feedback
            JOptionPane.showMessageDialog(view, "Account successfully created!\nPlease return to the login screen.");

            // Optional: Clear fields or automatically switch card back to login?
            // view.clearSignupFields(); // If you implemented this method in View

        } catch (UserInputErrorException ex) {
            // Logic Error (Duplicate ID, Invalid Role)
            view.showErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            // System Error (File IO)
            view.showErrorMessage("System Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Helper: Opens the Student Dashboard
    private void openStudentDashboard(Student student) throws IOException, FontFormatException {
        StudentDashboardView dashboardView = new StudentDashboardView();
        new StudentDashboardController(dashboardView, student);
        dashboardView.setVisible(true);
    }

    // Helper: Opens the Teacher Dashboard
    private void openTeacherDashboard(Teacher teacher) {
        TeacherDashboardView dashboardView = new TeacherDashboardView();
        new TeacherDashboardController(dashboardView, teacher);
        dashboardView.setVisible(true);
    }
}