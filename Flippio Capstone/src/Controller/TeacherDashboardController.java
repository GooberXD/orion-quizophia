package Controller;

import View.*;
import Model.*;
import Service.*;
import Utility.*;
import Exception.*; // Custom Exceptions
import javax.swing.*;
import java.util.*;

public class TeacherDashboardController {
    private TeacherDashboardView view;
    private Teacher teacher;
    private ScoreService scoreService;

    public TeacherDashboardController(TeacherDashboardView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;
        this.scoreService = new ScoreService("scores.csv"); // Load scores

        initController();
    }

    private void initController() {
        // 1. Set Welcome Text
        view.setTeacherName(teacher.getName());

        // 2. Load Student Scores into the Table
        loadStudentScores();

        // 3. Navigation Listeners
        view.addLogoutListener(e -> {
            PageNavigation.switchViews(view, new LoginView());
            new LoginController(new LoginView(), AuthService.getInstance());
        });

        view.addCreateQuizListener(e -> {
            view.dispose(); // Close dashboard
            CreateQuizView createView = new CreateQuizView();
            new CreateQuizController(createView, teacher);
            createView.setVisible(true);
        });

        // NEW: Ranking Listener
        view.addRefreshRankListener(e -> calculateRankings());
    }

    private void loadStudentScores() {
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

    // NEW: Logic for Ranking System
    private void calculateRankings() {
        try {
            view.clearRankings();
            // We need the StudentService to get Student objects (for average calculation)
            StudentService studentService = new StudentService("students.csv");
            List<Student> students = studentService.getAllStudents();

            if (students.isEmpty()) {
                throw new RankingException("No students found to rank.");
            }

            // Sort by Average Score (Descending)
            students.sort((s1, s2) -> Double.compare(s2.getAverageScore(), s1.getAverageScore()));

            int rank = 1;
            for (Student s : students) {
                view.addRankingRow(rank++, s.getName(), s.getAverageScore());
            }

        } catch (RankingException e) {
            JOptionPane.showMessageDialog(view, "Ranking Error: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Unexpected error in ranking calculation.");
        }
    }
}