package Controller;

import Model.*;
import FileManager.*;
import java.util.*;

public class QuizListController {
    // This controller specifically manages the list of quizzes available
    // It can be used by the StudentDashboard (to show available quizzes)
    // or TeacherDashboard (to show created quizzes)

    private QuizFileManager quizManager;

    public QuizListController() {
        this.quizManager = new QuizFileManager("quizzes.txt");

    }

    public List<Quiz> getAvailableQuizzes() {
        try {
            return quizManager.load();
        } catch (Exception e) {
            System.err.println("Error loading quizzes: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    // Future expansion: deleteQuiz(), editQuiz()
}
