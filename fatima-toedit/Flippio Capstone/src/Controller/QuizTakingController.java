package Controller;

import View.*;
import Model.*;
import Service.*;
import Exception.*; // Custom Exceptions
import java.util.Arrays;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        // Setup the Navigation Circles (1 to N)
        view.setupQuestionNavigator(quiz.getQuestions().size());

        // Handle Navigation Circle Clicks
        view.addNavigationListener(e -> {
            saveCurrentAnswer(); // Save before jumping
            // The view sets the ActionCommand to the index (e.g. "0", "1")
            int targetIndex = Integer.parseInt(e.getActionCommand());
            currentQuestionIndex = targetIndex;
            loadQuestion(currentQuestionIndex);
            updateButtons();
        });

        loadQuestion(0);
        updateButtons(); // This will also initialize the circle colors

        this.view.getBtnNext().addActionListener(e -> nextQuestion());
        this.view.getBtnPrev().addActionListener(e -> prevQuestion());
        this.view.getBtnSubmit().addActionListener(e -> submitQuiz());
    }

    private void loadQuestion(int index) {
        Question q = quiz.getQuestions().get(index);
        view.setQuestionText("Question " + (index + 1) + ": " + q.getQuestionText());
        view.setOptions(q.getOptionsArray());

        // Reload previous answer if it exists, otherwise clear selection
        if (userAnswers[index] != -1) {
            view.setSelectedOption(userAnswers[index]);
        } else {
            // If the view supports clearing selection, you might need a method for that.
            // Currently, setOptions resets selection internally in your view, so this is fine.
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
        // Update Prev/Next/Submit visibility
        view.getBtnPrev().setEnabled(currentQuestionIndex > 0);
        view.getBtnNext().setEnabled(currentQuestionIndex < quiz.getQuestions().size() - 1);
        view.getBtnSubmit().setEnabled(true); // Always keep submit enabled (or specific logic)

        // Update Circle Status (Green for answered)
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
            // Ensure all questions are answered before scoring
            for (int i = 0; i < userAnswers.length; i++) {
                if (userAnswers[i] == -1) {
                    // Navigate user to the specific unanswered question for better UX
                    currentQuestionIndex = i;
                    loadQuestion(currentQuestionIndex);
                    updateButtons();
                    throw new UnansweredQuestionException("You have not answered Question " + (i + 1));
                }
            }

            // Calculate Score
            int score = 0;
            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                if (userAnswers[i] == quiz.getQuestions().get(i).getCorrectAnswerIndex()) {
                    score++;
                }
            }

            // Save Result
            QuizResult result = new QuizResult(
                    currentStudent.getIdNumber(),
                    quiz.getQuizName(),
                    (double)score,
                    quiz.getQuestions().size()
            );

            ScoreService scoreService = new ScoreService("scores.csv");
            scoreService.saveResult(result);

            currentStudent.addQuizResult((double) score);
            studentService.updateStudentProgress(currentStudent);

            String message = String.format("Quiz Finished!\nScore: %d / %d\nYour result has been saved.",
                    score, quiz.getQuestions().size());

            JOptionPane.showMessageDialog(view, message);
            view.dispose();

            // Re-open Dashboard
            StudentDashboardView dashboardView = new StudentDashboardView();
            new StudentDashboardController(dashboardView, currentStudent);
            dashboardView.setVisible(true);

        } catch (UnansweredQuestionException e) {
            JOptionPane.showMessageDialog(view, e.getMessage(), "Submission Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error saving quiz result: " + e.getMessage());
        }
    }
}