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

        this.view.addLoginListener(e -> performLogin());
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
            String name = view.getSignName();
            String id = view.getSignId();
            String role = view.getSignRole();
            String pass = view.getSignPass();
            String confirmPass = view.getSignConfirmPass(); // Get Confirm Password

            if (name.isEmpty() || id.isEmpty() || role.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                view.showErrorMessage("Please fill in all registration fields.");
                return;
            }

            // CHECK IF PASSWORDS MATCH
            if (!pass.equals(confirmPass)) {
                view.showErrorMessage("Passwords do not match. Please try again.");
                return;
            }

            // The Service handles checking for duplicates and saving to file
            authService.register(id, pass, name, role.trim());

            try {
                if ("Student".equalsIgnoreCase(role.trim())) {
                    FileManager.StudentPerformanceFileManager spfm = new FileManager.StudentPerformanceFileManager(id);
                    spfm.ensureExists();
                } else if ("Teacher".equalsIgnoreCase(role.trim())) {
                    FileManager.TeacherQuizSummaryFileManager tqs = new FileManager.TeacherQuizSummaryFileManager(id);
                    tqs.writeAll(new java.util.ArrayList<>());
                }
            } catch (Exception ignore) {}

            JOptionPane.showMessageDialog(view, "Account successfully created!");

            // Reset form and return to a landing panel
            view.resetSignupFormAndReturnToLanding();

        } catch (UserInputErrorException ex) {
            // Logic Error (Duplicate ID, Invalid Role)
            view.showErrorMessage(ex.getMessage());
        } catch (Exception ex) {
            // System Error (File IO)
            view.showErrorMessage("System Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openStudentDashboard(Student student) throws IOException, FontFormatException {
        try {
            FileManager.StudentPerformanceFileManager spfm = new FileManager.StudentPerformanceFileManager(student.getIdNumber());
            spfm.ensureExists();
        } catch (IOException ignore) {  throw new IOException("Error opening student performance file!"); }
        StudentDashboardView dashboardView = new StudentDashboardView();
        new StudentDashboardController(dashboardView, student);
        dashboardView.setVisible(true);
    }

    private void openTeacherDashboard(Teacher teacher) {
        View.TeacherSubjectDashboardView view = new View.TeacherSubjectDashboardView();
        String perUserSubjects = "subject_" + teacher.getIdNumber() + ".csv";
        FileManager.SubjectFileManager sfm = new FileManager.SubjectFileManager(perUserSubjects);
        new Controller.SubjectController(sfm, view, teacher);
        view.setVisible(true);
    }
}