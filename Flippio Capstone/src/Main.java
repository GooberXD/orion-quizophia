import Service.*;
import View.*;
import Model.*;
import Controller.*;
import FileManager.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Run GUI code on the Event Dispatch Thread (Swing Best Practice)
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. SETUP: Generate Dummy Data if files don't exist
                setupStudentData();
                setupTeacherData();

                // 2. INITIALIZE SERVICES
                // AuthService loads the data we just created into memory
                AuthService authService = AuthService.getInstance();

                // 3. LAUNCH LOGIN VIEW
                LoginView loginView = new LoginView();

                // 4. ATTACH CONTROLLER
                // Connects the View (GUI) to the Service (Logic)
                new LoginController(loginView, authService);

                // 5. SHOW WINDOW
                loginView.setVisible(true);

            } catch (Exception e) {
                System.err.println("Critical Error: " + e.getMessage());
//                e.printStackTrace();
            }
        });
    }

    // Helper: Creates students.csv with a default user
    private static void setupStudentData() {
        File file = new File("students.csv");
        if (!file.exists()) {
            System.out.println("Generating default student data...");
            StudentFileManager mgr = new StudentFileManager("students.csv");
            List<Student> students = new ArrayList<>();

            // Default Student Credentials: ID: 1234, Pass: 1234
            students.add(new Student("1234", "1234", "Juan Dela Cruz"));

            try {
                mgr.save(students);
            } catch (IOException e) {
                System.err.println("Failed to create student data: " + e.getMessage());
            }
        }
    }

    // Helper: Creates teachers.csv with a default user
    private static void setupTeacherData() {
        File file = new File("teachers.csv");
        if (!file.exists()) {
            System.out.println("Generating default teacher data...");
            TeacherFileManager mgr = new TeacherFileManager("teachers.csv");
            List<Teacher> teachers = new ArrayList<>();

            // Default Teacher Credentials: ID: 9999, Pass: admin
            teachers.add(new Teacher("9999", "admin", "Jay Vince Serato"));

            try {
                mgr.save(teachers);
            } catch (IOException e) {
                System.err.println("Failed to create teacher data: " + e.getMessage());
            }
        }
    }
}