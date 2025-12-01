package Controller;

import Model.*;
import View.*;
import Service.*;
import java.awt.event.*;
import java.util.*;

public class StudentDashboardController {
    private StudentDashboardView view;
    private Student student;

    public StudentDashboardController(StudentDashboardView view, Student student) {
        this.view = view;
        this.student = student;

        initDashboard();
    }

    private void initDashboard() {
        // 1. Set Welcome Message [cite: 72]
        view.setStudentName(student.getName());

        // 2. Load Performance Stats (Table & Average)
        loadPerformanceData();

        // 3. Load Available Quizzes (Home Tab)
        loadQuizButtons();

        // 4. Setup Logout
        view.addLogoutListener(e -> logout());
    }

    private void loadPerformanceData() {
        List<Double> results = student.getQuizResults();
        double total = 0;

        // Populate Table
        // Note: Since we only stored scores as Doubles in the Student model,
        // we will label them sequentially.
        int count = 1;
        for (Double score : results) {
            String quizName = "Quiz Attempt " + count;
            String status = (score >= 5.0) ? "PASSED" : "FAILED"; // Assumes passing is 50% of 10 items

            // Add row to the View
//            view.addResultRow(quizName, score + " / 10", status);
            view.addResultRow(quizName, score, status);

            total += score;
            count++;
        }

        // Calculate and Update Average [cite: 73]
        double average = (results.isEmpty()) ? 0.0 : (total / results.size()) * 10; // *10 to make it percentage if score is out of 10
        view.updateAverageScore(average);
    }

    private void loadQuizButtons() {
        // In a full app, these would come from a TeacherService or QuizFileManager.
        // For this capstone prototype, we create the "Objects" here.

        // QUIZ 1: OOP Fundamentals
        view.addQuizButton("Fundamentals of OOP", e -> {
            launchQuiz("Fundamentals of OOP");
        });

        // QUIZ 2: Abstract Classes
        view.addQuizButton("Abstract Classes", e -> {
            launchQuiz("Abstract Classes");
        });

        // QUIZ 3: Polymorphism
        view.addQuizButton("Polymorphism", e -> {
            launchQuiz("Polymorphism");
        });
    }

    private void launchQuiz(String topic) {
        // 1. Create the Quiz Object [cite: 79]
        Quiz quiz = new Quiz(topic);

        // Add Dummy Questions (Polymorphism: Different questions for different topics could go here)
        List<String> options = new ArrayList<>();
        options.add("A"); options.add("B"); options.add("C"); options.add("D");

        quiz.addQuestion(new Question("Sample Question 1 for " + topic, options, 0));
        quiz.addQuestion(new Question("Sample Question 2 for " + topic, options, 1));
        quiz.addQuestion(new Question("Sample Question 3 for " + topic, options, 2));
        quiz.addQuestion(new Question("Sample Question 4 for " + topic, options, 3));
        quiz.addQuestion(new Question("Sample Question 5 for " + topic, options, 0));
        quiz.addQuestion(new Question("Sample Question 6 for " + topic, options, 1));
        quiz.addQuestion(new Question("Sample Question 7 for " + topic, options, 2));
        quiz.addQuestion(new Question("Sample Question 8 for " + topic, options, 3));
        quiz.addQuestion(new Question("Sample Question 9 for " + topic, options, 0));
        quiz.addQuestion(new Question("Sample Question 10 for " + topic, options, 1));

        // 2. Launch the Quiz View
        QuizTakingView quizView = new QuizTakingView();

        // Pass the CURRENT student so their score is saved to the right person
        new QuizTakingController(quizView, quiz, student);

        // 3. Hide Dashboard
        view.dispose();
        quizView.setVisible(true);
    }

    private void logout() {
        view.dispose();
        // Return to Login Screen
        LoginView loginView = new LoginView();
        AuthService auth = AuthService.getInstance();
        new LoginController(loginView, auth);
        loginView.setVisible(true);
    }
}
