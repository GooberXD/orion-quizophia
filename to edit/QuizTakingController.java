package Controller;

import View.*;
import Model.*;
import Service.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class QuizTakingController {
    private QuizTakingView view;
    private Quiz quiz;
    private Student currentStudent; // NEW: Track who is taking the quiz
    private StudentService studentService; // NEW: Access to file saving logic

    private int currentQuestionIndex = 0;
    private int[] userAnswers;

    // MODIFIED CONSTRUCTOR: Now requires the Student object
    public QuizTakingController(QuizTakingView view, Quiz quiz, Student student) {
        this.view = view;
        this.quiz = quiz;
        this.currentStudent = student;

        // Initialize Service (File Handling logic)
        this.studentService = new StudentService("students.csv");

        this.userAnswers = new int[quiz.getQuestions().size()];

        // Initialize answers to -1 (meaning no answer selected)
        Arrays.fill(userAnswers, -1);

        // Initialize the View
        loadQuestion(0);
        updateButtons();

        // Attach Listeners
        this.view.getBtnNext().addActionListener(e -> nextQuestion());
        this.view.getBtnPrev().addActionListener(e -> prevQuestion());
        this.view.getBtnSubmit().addActionListener(e -> {
            try {
                submitQuiz();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (FontFormatException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void loadQuestion(int index) {
        Question q = quiz.getQuestions().get(index);

        // Update View Text
        view.setQuestionText("Question " + (index + 1) + ": " + q.getQuestionText());

        // Uses the helper method we added to Question.java
        view.setOptions(q.getOptionsArray());

        // Restore previous answer if it exists
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
        view.getBtnSubmit().setEnabled(currentQuestionIndex == quiz.getQuestions().size() - 1);
    }

    // --- KEY UPDATE: LOGIC TO SAVE DATA ---
    private void submitQuiz() throws IOException, FontFormatException {
        saveCurrentAnswer(); // Ensure last answer is captured

        // 1. Calculate Score
        int score = 0;
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            if (userAnswers[i] == quiz.getQuestions().get(i).getCorrectAnswerIndex()) {
                score++;
            }
        }

        // Create a result object
        QuizResult result = new QuizResult(
                currentStudent.getIdNumber(),
                quiz.getQuizName(),
                (double)score,
                quiz.getQuestions().size()
        );

        // Save using service
        ScoreService scoreService = new ScoreService("scores.csv");
        scoreService.saveResult(result);

        // ... Proceed to show message and close ...

        // 2. Update the Student Model in memory
        // (Assuming you want to store the raw score, or you can convert to percentage)
        currentStudent.addQuizResult((double) score);

        // 3. PERSISTENCE: Save to File using the Service
        studentService.updateStudentProgress(currentStudent);

        // 4. Feedback and Close
        String message = String.format("Quiz Finished!\nScore: %d / %d\nYour result has been saved.",
                score, quiz.getQuestions().size());

        JOptionPane.showMessageDialog(view, message);

        view.dispose(); // Close the quiz window

        // Optional: Open the Dashboard here if you have one ready
         new StudentDashboardView();

//         QuizTakingController.java -> submitQuiz()

// --- ADD THIS TO RE-OPEN DASHBOARD ---
        StudentDashboardView dashboardView = new StudentDashboardView();
        new StudentDashboardController(dashboardView, currentStudent); // Reloads with updated stats
        dashboardView.setVisible(true);
// -------------------------------------
    }
}
