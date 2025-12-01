package Controller;

import View.*;
import Model.*;
import Service.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class LoginController {
    private LoginView view;
    private AuthService authService;

    public LoginController(LoginView view, AuthService authService) {
        this.view = view;
        this.authService = authService;

        // Connect the view's button to this controller's logic
        this.view.addLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        try {
            // 1. Get Input
            String id = view.getIdInput();
            String pass = view.getPassInput();

            // 2. Attempt Login via Service (Throws Exception if failed)
            User user = authService.login(id, pass);

            // 3. Route User based on Role (Polymorphism)
            if (user instanceof Student) {
                // --- MODIFICATION: Open Student Dashboard ---
                view.dispose(); // Close Login Window
                openStudentDashboard((Student) user);

            } else if (user instanceof Teacher) {
                // --- MODIFICATION: Open Teacher Dashboard ---
                view.dispose();
                openTeacherDashboard((Teacher) user);
            }

        } catch (Exception ex) {
            // Satisfies "Exception Handling" requirement [cite: 20]
            view.showErrorMessage("Login Failed: " + ex.getMessage());
        }
    }

    // Helper: Opens the Student Dashboard
    private void openStudentDashboard(Student student) throws IOException, FontFormatException {
        StudentDashboardView dashboardView = new StudentDashboardView();
        // Connect the Controller (Loads name, stats, and quiz buttons)
        new StudentDashboardController(dashboardView, student);
        dashboardView.setVisible(true);
    }

    // Helper: Opens the Teacher Dashboard
    private void openTeacherDashboard(Teacher teacher) {
        TeacherDashboardView dashboardView = new TeacherDashboardView();
        // Connect the Controller (Loads teacher name and student scores)
        new TeacherDashboardController(dashboardView, teacher);
        dashboardView.setVisible(true);
    }
}