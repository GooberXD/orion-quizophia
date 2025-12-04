package Controller;

import Model.*;
import View.*;
import Service.*;
import FileManager.*;
import Exception.*; // Custom Exceptions
import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class StudentDashboardController {
    private StudentDashboardView view;
    private Student student;

    public StudentDashboardController(StudentDashboardView view, Student student) {
        this.view = view;
        this.student = student;

        initDashboard();
    }

    private void initDashboard() {
        view.setStudentName(student.getName());
        loadPerformanceData();
        loadQuizButtons();
        view.addLogoutListener(e -> logout());
        view.addDownloadListener(e -> downloadReport());
    }

    private void loadPerformanceData() {
        List<Double> results = student.getQuizResults();
        double total = 0;
        int count = 1;
        for (Double score : results) {
            String quizName = "Quiz Attempt " + count;
            String status = (score >= 5.0) ? "PASSED" : "FAILED";
            view.addResultRow(quizName, score, status);
            total += score;
            count++;
        }
        double average = (results.isEmpty()) ? 0.0 : (total / results.size()) * 10;
        view.updateAverageScore(average);
    }

    private void loadQuizButtons() {
        view.clearQuizButtons();
        QuizFileManager quizManager = new QuizFileManager("quizzes.txt");
        List<Quiz> availableQuizzes = new ArrayList<>();

        try {
            availableQuizzes = quizManager.load();
        } catch (Exception e) {
            System.err.println("Could not load quizzes: " + e.getMessage());
        }

        if (!availableQuizzes.isEmpty()) {
            for (Quiz q : availableQuizzes) {
                view.addQuizButton(q.getQuizName(), e -> launchQuiz(q));
            }
            return;
        }

        // Fallback: add built-in default topics when no quizzes are available from file
        addDefaultTopicButton("Fundamentals of OOP");
        addDefaultTopicButton("Abstract Classes");
        addDefaultTopicButton("Polymorphism");
    }

    private void launchQuiz(Quiz quiz) {
        QuizTakingView quizView = new QuizTakingView();
        new QuizTakingController(quizView, quiz, student);
        view.dispose();
        quizView.setVisible(true);
    }

    // Helpers for default topic fallback (from earlier implementation)
    private void addDefaultTopicButton(String topic) {
        view.addQuizButton(topic, e -> launchDefaultTopic(topic));
    }

    private void launchDefaultTopic(String topic) {
        Quiz quiz = buildDefaultQuizFor(topic);
        launchQuiz(quiz);
    }

    private Quiz buildDefaultQuizFor(String topic) {
        Quiz quiz = new Quiz(topic);
        List<String> options = Arrays.asList("A", "B", "C", "D");

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

        return quiz;
    }

    // --- MODIFIED METHOD: DOWNLOAD AND DISPLAY ---
    private void downloadReport() {
        // 1. Build the Report Content in Memory
        StringBuilder reportBuilder = new StringBuilder();

        reportBuilder.append("FLIPPIO STUDENT REPORT\n");
        reportBuilder.append("----------------------\n");
        reportBuilder.append("Student: ").append(student.getName()).append("\n");
        reportBuilder.append("ID: ").append(student.getIdNumber()).append("\n");
        reportBuilder.append("Average Score: ").append(String.format("%.2f", student.getAverageScore())).append("\n");
        reportBuilder.append("----------------------\n");
        reportBuilder.append("Quiz History:\n");

        List<Double> results = student.getQuizResults();
        int count = 1;
        for(Double score : results) {
            reportBuilder.append("Quiz ").append(count).append(": ").append(score).append("\n");
            count++;
        }

        String reportContent = reportBuilder.toString();
        String fileName = "Report_" + student.getName().replaceAll(" ", "_") + ".txt";

        // 2. Save to File
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName))) {
            writer.write(reportContent);

            // Success Message
            JOptionPane.showMessageDialog(view, "Report downloaded successfully to: " + fileName);

            // 3. SHOW DIALOG (GUI Output)
            showReportDialog(reportContent);

        } catch (java.io.IOException e) {
            // Throw Custom Exception
            JOptionPane.showMessageDialog(view, new FileWriteException("Could not save report: " + e.getMessage()).getMessage());
        }
    }

    // Helper to show the report in a scrollable text area
    private void showReportDialog(String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(350, 400)); // Size of the pop-up

        JOptionPane.showMessageDialog(view, scrollPane, "Grade Report Preview", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        view.dispose();
        LoginView loginView = new LoginView();
        AuthService auth = AuthService.getInstance();
        new LoginController(loginView, auth);
        loginView.setVisible(true);
    }
}