import Service.*;
import View.*;
import Controller.*;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        FileManager.QuizFileManager.repairSubjectQuizIndex();
        
        // Run GUI code on the Event Dispatch Thread (Swing Best Practice)
        SwingUtilities.invokeLater(() -> {
            try {
                AuthService authService = AuthService.getInstance();
                LoginView loginView = new LoginView();

                new LoginController(loginView, authService);
                loginView.setVisible(true);
    
            } catch (Exception e) {
                System.err.println("Critical Error during startup: " + e.getMessage());
            }
        });
    }
}