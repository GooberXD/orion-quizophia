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
    private String status;
    private QuizTakingController quizCtrl;

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
        double totalRawScore = 0;
        double totalQuestions = 0;
        int quizCount = 0;

        // Try to load scores from file to get complete information
        try {
            java.io.File file = new java.io.File("scores.csv");
            if (file.exists() && file.length() > 0) {
                java.util.Scanner scanner = new java.util.Scanner(file);

                // Read all lines
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split(",");

                    // Skip lines that don't have enough parts
                    if (parts.length < 4) continue;

                    // Check if this result belongs to current student
                    if (parts[0].equals(student.getIdNumber())) {
                        try {
                            String quizName = parts[1];
                            double rawScore = Double.parseDouble(parts[2]);
                            double total = Double.parseDouble(parts[3]);
                            double percentage = (rawScore / total) * 100;
                            status = (percentage >= 50.0) ? "PASSED" : "FAILED";

                            view.addResultRow(quizName, rawScore, total, status);

                            totalRawScore += rawScore;
                            totalQuestions += total;
                            quizCount++;
                        } catch (NumberFormatException e) {
                            // Skip this line if it has invalid numbers
                            System.err.println("Skipping invalid score data: " + line);
                            continue;
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.err.println("Error reading scores.csv: " + e.getMessage());

            // Fallback: use student's results with assumed total (10 questions)
            for (Double score : results) {
                String quizName = "Quiz Attempt " + (quizCount + 1);
                double total = 10.0; // Assume 10 questions
                double percentage = (score / total) * 100;
                String status = (percentage >= 50.0) ? "PASSED" : "FAILED";

                view.addResultRow(quizName, score, total, status);

                totalRawScore += score;
                totalQuestions += total;
                quizCount++;
            }
        }

        // Calculate average percentage
        double averagePercentage = (totalQuestions > 0) ? (totalRawScore / totalQuestions) * 100 : 0.0;
        view.updateAverageScore(averagePercentage);
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

        // Read scores from scores.csv to get accurate data
        double totalRawScore = 0;
        double totalQuestions = 0;
        int quizCount = 0;
        List<String> quizEntries = new ArrayList<>();

        try {
            java.io.File file = new java.io.File("scores.csv");
            if (file.exists() && file.length() > 0) {
                java.util.Scanner scanner = new java.util.Scanner(file);

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) continue;

                    String[] parts = line.split(",");
                    if (parts.length < 4) continue;

                    if (parts[0].equals(student.getIdNumber())) {
                        try {
                            String quizName = parts[1];
                            double rawScore = Double.parseDouble(parts[2]);
                            double total = Double.parseDouble(parts[3]);
                            double percentage = (rawScore / total) * 100;
                            String status = (percentage >= 50.0) ? "PASSED" : "FAILED";

                            // Format: [quizName] : [score]/[totalScore] - [PASSED/FAILED]
                            String entry = quizName + " : " +
                                    String.format("%.1f / %.1f", rawScore, total) +
                                    " - " + status;
                            quizEntries.add(entry);

                            totalRawScore += rawScore;
                            totalQuestions += total;
                            quizCount++;
                        } catch (NumberFormatException e) {
                            continue;
                        }
                    }
                }
                scanner.close();
            }
        } catch (Exception e) {
            System.err.println("Error reading scores.csv: " + e.getMessage());

            // Fallback to student's results
            List<Double> results = student.getQuizResults();
            int count = 1;
            for(Double score : results) {
                double total = 10.0; // Assume 10 questions
                double percentage = (score / total) * 100;
                String status = (percentage >= 50.0) ? "PASSED" : "FAILED";

                String quizName = "Quiz Attempt " + count;
                String entry = quizName + " : " +
                        String.format("%.1f / %.1f", score, total) +
                        " - " + status;
                quizEntries.add(entry);

                totalRawScore += score;
                totalQuestions += total;
                quizCount++;
                count++;
            }
        }

        // Calculate average percentage
        double averagePercentage = (totalQuestions > 0) ? (totalRawScore / totalQuestions) * 100 : 0.0;
        reportBuilder.append("Average Score: ").append(String.format("%.2f%%", averagePercentage)).append("\n");
        reportBuilder.append("----------------------\n");
        reportBuilder.append("Quiz History:\n");

        // Add all quiz entries
        for (String entry : quizEntries) {
            reportBuilder.append(entry).append("\n");
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