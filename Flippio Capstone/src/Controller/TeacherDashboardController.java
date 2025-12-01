package Controller;

import View.*;
import Model.*;
import Service.*;
import Utility.*;
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
            // Note: You would typically re-initialize LoginController here
            new LoginController(new LoginView(), AuthService.getInstance());
        });

        view.addCreateQuizListener(e -> {
            view.dispose(); // Close dashboard
            CreateQuizView createView = new CreateQuizView();
            new CreateQuizController(createView, teacher);
            createView.setVisible(true);
        });
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
}
