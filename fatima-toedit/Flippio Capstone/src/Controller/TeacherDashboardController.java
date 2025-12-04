package Controller;

import FileManager.QuizFileManager;
import View.*;
import Model.*;
import Service.*;
import Utility.*;
import Exception.*;
import javax.swing.*;
import java.util.*;

public class TeacherDashboardController {
    private TeacherDashboardView view;
    private Teacher teacher;
    private ScoreService scoreService;

    public TeacherDashboardController(TeacherDashboardView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;
        this.scoreService = new ScoreService("scores.csv");

        initController();
    }

    private void initController() {
        // 1. Set Welcome Text
        view.setTeacherName(teacher.getName());

        // 2. Load Student Scores into the Table
        loadStudentScores();

        // 3. Navigation Listeners
        view.addLogoutListener(e -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView, AuthService.getInstance());
            PageNavigation.switchViews(view, loginView);
        });

        view.addCreateQuizListener(e -> {
            view.dispose(); // Close dashboard
            CreateQuizView createView = new CreateQuizView();
            new CreateQuizController(createView, teacher);
            createView.setVisible(true);
        });

        // 4. Ranking Listener
        view.addRefreshRankListener(e -> calculateRankings());

        // 5. Delete Quiz Listener
        view.addDeleteQuizListener(e -> deleteQuizProcess());
    }

    // --- LOGIC: DELETE QUIZ AND RECORDS ---
    private void deleteQuizProcess() {
        // 1. Ask user for the Quiz Name
        String quizName = JOptionPane.showInputDialog(view, "Enter the exact name of the Quiz to delete:");

        if (quizName != null && !quizName.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(view,
                    "Are you sure you want to delete '" + quizName + "'?\nThis will delete the quiz AND all student results associated with it.\nThis cannot be undone.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // A. Delete the Quiz Definition (quizzes.txt)
                    QuizFileManager quizManager = new QuizFileManager("quizzes.txt");
                    quizManager.deleteQuiz(quizName.trim());

                    // B. Delete Related Scores (scores.csv)
                    scoreService.deleteScoresByQuizName(quizName.trim());

                    // C. Refresh the Dashboard UI
                    loadStudentScores(); // Reload table (rows should disappear)
                    calculateRankings(); // Recalculate ranks (averages might change)

                    JOptionPane.showMessageDialog(view, "Quiz and related records deleted successfully.");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
                }
            }
        }
    }

    private void loadStudentScores() {
        // Clear existing table first (to prevent duplication or stale data)
        view.clearScoreTable(); // **Ensure View has this method**

        List<QuizResult> results = scoreService.getAllResults();

        for (QuizResult result : results) {
            String status = (result.getScore() / result.getTotalItems() >= 0.5) ? "PASSED" : "FAILED";

            view.addScoreRow(
                    result.getStudentId(),
                    result.getQuizName(),
                    result.getScore(),
                    result.getTotalItems(),
                    status
            );
        }
    }

    private void calculateRankings() {
        try {
            view.clearRankings();
            StudentService studentService = new StudentService("students.csv");
            List<Student> students = studentService.getAllStudents();

            if (students.isEmpty()) {
                // If no students, just return, don't throw error to allow empty state
                return;
            }

            // Sort by Average Score (Descending)
            students.sort((s1, s2) -> Double.compare(s2.getAverageScore(), s1.getAverageScore()));

            int rank = 1;
            for (Student s : students) {
                view.addRankingRow(rank++, s.getName(), s.getAverageScore());
            }

        } catch (Exception e) {
            // Ignore file errors during refresh
        }
    }
}