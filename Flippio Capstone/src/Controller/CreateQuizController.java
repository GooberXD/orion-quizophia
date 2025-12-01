package Controller;

import Model.*;
import View.*;
import FileManager.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {
    private CreateQuizView view;
    private Teacher teacher; // To return to dashboard later
    private List<Question> tempQuestions; // Holds questions before saving

    public CreateQuizController(CreateQuizView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;
        this.tempQuestions = new ArrayList<>();

        // Attach Listeners
        this.view.addAddQuestionListener(e -> addQuestionToList());
        this.view.addSaveQuizListener(e -> saveFinalQuiz());
        this.view.addCancelListener(e -> close());
    }

    private void addQuestionToList() {
        // 1. Validate Inputs
        if (view.getQuestionText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a question text.");
            return;
        }
        List<String> options = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String opt = view.getOption(i);
            if (opt.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please fill in all 4 options.");
                return;
            }
            options.add(opt);
        }

        // 2. Create Question Object
        int correctIndex = view.getSelectedCorrectIndex();
        Question q = new Question(view.getQuestionText(), options, correctIndex);

        // 3. Add to Temp List
        tempQuestions.add(q);

        // 4. Update UI
        view.appendToPreview((tempQuestions.size()) + ". " + q.getQuestionText());
        view.clearQuestionInputs();
    }

    private void saveFinalQuiz() {
        // 1. Validate Quiz Level
        String title = view.getQuizTitle();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a Quiz Title.");
            return;
        }
        if (tempQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please add at least one question.");
            return;
        }

        // 2. Create Quiz Object
        Quiz newQuiz = new Quiz(title);
        for (Question q : tempQuestions) {
            newQuiz.addQuestion(q);
        }

        // 3. Save to File
        try {
            QuizFileManager manager = new QuizFileManager("quizzes.txt");
            manager.save(newQuiz);

            JOptionPane.showMessageDialog(view, "Quiz Saved Successfully!");
            close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error saving quiz: " + ex.getMessage());
        }
    }

    private void close() {
        view.dispose();
        // Re-open Dashboard
        TeacherDashboardView dashView = new TeacherDashboardView();
        new TeacherDashboardController(dashView, teacher);
        dashView.setVisible(true);
    }
}
