package Controller;

import View.*;
import Model.*;
import Service.*;
import Exception.*; // Custom Exceptions
import java.util.Arrays;
import javax.swing.JOptionPane;

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

    private void submitQuiz() {
        saveCurrentAnswer();

        try {
            // Ensure all questions are answered before scoring
            // NEW: Check for Unanswered Questions
            for (int i = 0; i < userAnswers.length; i++) {
                if (userAnswers[i] == -1) {
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

            // Save Result (combine: build result, persist score, update student progress)
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

            // Re-open Dashboard (single instance)
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