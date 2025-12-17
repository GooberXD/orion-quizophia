package Controller;

import View.*;
import Model.*;
import Service.*;
import java.util.Arrays;
import javax.swing.JOptionPane;
import java.util.List;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import FileManager.StudentPerformanceFileManager;
import FileManager.TeacherQuizSummaryFileManager;
import Utility.ResourceLoader;

public class QuizTakingController {
    private QuizTakingView view;
    private Quiz quiz;
    private Student currentStudent;
    private StudentService studentService;

    private int currentQuestionIndex = 0;
    private int[] userAnswers;

    public QuizTakingController(QuizTakingView view, Quiz quiz, Student student) {
        this.view = view;
        this.quiz = quiz;
        this.currentStudent = student;

        this.studentService = new StudentService("students.csv");

        this.userAnswers = new int[quiz.getQuestions().size()];
        Arrays.fill(userAnswers, -1);

        // Set up the Navigation Circles (1 to N)
        view.setupQuestionNavigator(quiz.getQuestions().size());

        // Handle Navigation Circle Clicks
        view.addNavigationListener(e -> {
            saveCurrentAnswer();

            int targetIndex = Integer.parseInt(e.getActionCommand());
            currentQuestionIndex = targetIndex;
            loadQuestion(currentQuestionIndex);
            updateButtons();
        });

        loadQuestion(0);
        updateButtons();

        this.view.getBtnNext().addActionListener(e -> nextQuestion());
        this.view.getBtnPrev().addActionListener(e -> prevQuestion());
        this.view.getBtnSubmit().addActionListener(e -> submitQuiz());
    }

    private void loadQuestion(int index) {
        Question q = quiz.getQuestions().get(index);
        view.setQuestionText("Question " + (index + 1) + ": " + q.getQuestionText());
        view.setOptions(q.getOptionsArray());

        // Reload the previous answer if it exists, otherwise clear selection
        if (userAnswers[index] != -1) {
            view.setSelectedOption(userAnswers[index]);
        }
    }

    private void saveCurrentAnswer() {
        int selected = view.getSelectedOptionIndex();
        if (selected != -1) {
            userAnswers[currentQuestionIndex] = selected;
        }
    }

    private void nextQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex < quiz.getQuestions().size() - 1) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
            updateButtons();
        }
    }

    private void prevQuestion() {
        saveCurrentAnswer();
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            loadQuestion(currentQuestionIndex);
            updateButtons();
        }
    }

    private void updateButtons() {
        view.getBtnPrev().setEnabled(currentQuestionIndex > 0);
        view.getBtnNext().setEnabled(currentQuestionIndex < quiz.getQuestions().size() - 1);
        view.getBtnSubmit().setEnabled(true);

        updateCircleStatus();
    }

    // Helper to turn circles Green if they have an answer recorded
    private void updateCircleStatus() {
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            // Status 1 = Green (Answered), 0 = Default (Unanswered)
            int status = (userAnswers[i] != -1) ? 1 : 0;
            view.setQuestionStatus(i, status);
        }
    }

    private void submitQuiz() {
        saveCurrentAnswer();
        updateCircleStatus();

        try {
            // Check unanswered questions first and confirm with the user
            int firstUnanswered = -1;
            int unansweredCount = 0;
            for (int i = 0; i < userAnswers.length; i++) {
                if (userAnswers[i] == -1) {
                    if (firstUnanswered == -1) firstUnanswered = i;
                    unansweredCount++;
                }
            }

            if (unansweredCount > 0) {
                int choice = JOptionPane.showConfirmDialog(
                        view,
                        "You have " + unansweredCount + " unanswered question(s). Submit anyway?\n" +
                                "Choose No to review; you'll jump to Question " + (firstUnanswered + 1) + ".",
                        "Confirm Submit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (choice != JOptionPane.YES_OPTION) {
                    // Jump to first unanswered for a quick review
                    currentQuestionIndex = firstUnanswered;
                    loadQuestion(currentQuestionIndex);
                    updateButtons();
                    return; // abort submit for now
                }
            }

            // Calculate Score
            int score = 0;
            if (quiz.getQuestions() != null && userAnswers != null) {
                for (int i = 0; i < quiz.getQuestions().size(); i++) {
                    if (i < userAnswers.length && userAnswers[i] != -1 && userAnswers[i] == quiz.getQuestions().get(i).getCorrectAnswerIndex()) {
                        score++;
                    }
                }
            }

                // Calculate percentage and store to student performance CSV
            double percentage = (quiz.getQuestions().size() == 0) ? 0.0 : (score / (double) quiz.getQuestions().size()) * 100.0;
            currentStudent.addQuizResult(percentage);
            studentService.updateStudentProgress(currentStudent);

                try {
                    StudentPerformanceFileManager perfFm = new StudentPerformanceFileManager(currentStudent.getIdNumber());
                    String subjectLabel = (quiz.getSubjectId() != null && !quiz.getSubjectId().isEmpty()) ? quiz.getSubjectId() : "";
                    // Update an existing entry (e.g., INC) or append a new one
                    java.util.List<String[]> existingRows = perfFm.readAll();
                    boolean updatedExisting = false;
                    for (int i = 0; i < existingRows.size(); i++) {
                        String[] row = existingRows.get(i);
                        if (row.length >= 2 && row[1] != null && row[1].equalsIgnoreCase(quiz.getQuizName())) {
                            // Update this entry with a new percentage and derived status
                            String status = (percentage >= 50.0) ? "PASS" : "FAIL";
                            existingRows.set(i, new String[]{subjectLabel, quiz.getQuizName(), String.valueOf(percentage), status});
                            updatedExisting = true;
                            break;
                        }
                    }
                    if (updatedExisting) {
                        // Overwrite file with updated rows
                        java.io.File perfFile = new java.io.File("student_" + currentStudent.getIdNumber() + ".csv");
                        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(perfFile, false))) {
                            for (String[] row : existingRows) {
                                bw.write(String.join("|", row));
                                bw.newLine();
                            }
                        }
                    } else {
                        // Append a new entry
                        String status = (percentage >= 50.0) ? "PASS" : "FAIL";
                        perfFm.appendRecord(subjectLabel, quiz.getQuizName(), percentage, status);
                    }
                } catch (Exception ioEx) {
                    System.err.println("Failed to write student performance CSV: " + ioEx.getMessage());
                }

                // Increment per-teacher quiz participation count
                try {
                    if (quiz.getTeacherId() != null && !quiz.getTeacherId().isEmpty()) {
                        TeacherQuizSummaryFileManager quizFm = new TeacherQuizSummaryFileManager(quiz.getTeacherId());
                        List<String[]> rows = quizFm.readAll();
                        boolean found = false;
                        String sid = (quiz.getSubjectId() == null) ? "" : quiz.getSubjectId();
                        for (String[] r : rows) {
                            if (r.length >= 2) {
                                String rsid = r[0] == null ? "" : r[0];
                                String rquiz = r[1] == null ? "" : r[1];
                                if (rsid.equals(sid) && rquiz.equals(quiz.getQuizName())) {
                                    int count = 0;
                                    try { count = Integer.parseInt(r.length > 2 && r[2] != null && !r[2].isEmpty() ? r[2] : "0"); } catch (NumberFormatException nfe) { count = 0; }
                                    r[2] = String.valueOf(count + 1);
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            rows.add(new String[]{ sid, quiz.getQuizName(), "1" });
                        }
                        quizFm.writeAll(rows);
                    }
                } catch (Exception ioEx) {
                    System.err.println("Failed to update teacher quiz summary CSV: " + ioEx.getMessage());
                }

                // Compute percentage for feedback (one decimal place)
                double displayPercentage = (percentage);
                String message = String.format(
                    "Quiz Finished!\nScore: %d / %d (%.1f%%)\nYour result has been saved.",
                    score, quiz.getQuestions().size(), displayPercentage);

            JOptionPane.showMessageDialog(view, message);

            // Show celebratory image (nice.png) before exiting the quiz view
            try {
                ImageIcon img = ResourceLoader.loadImageIcon("nice.png");
                if (img != null) {
                    // Scale image to a reasonable preview size while keeping aspect
                    Image scaled = img.getImage().getScaledInstance(480, 320, Image.SCALE_SMOOTH);
                    JLabel pic = new JLabel(new ImageIcon(scaled));
                    JOptionPane.showMessageDialog(view, pic, "Nice!", JOptionPane.PLAIN_MESSAGE);
                }
            } catch (Exception ex) {
                // Ignore failures to show image
            }

            view.dispose();

            // Re-open Dashboard
            StudentDashboardView dashboardView = new StudentDashboardView();
            new StudentDashboardController(dashboardView, currentStudent);
            dashboardView.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error saving quiz result: " + e.getMessage());
        }
    }
}
