package Controller;

import Model.*;
import View.*;
import FileManager.*;
import Exception.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {
    private CreateQuizView view;
    private Teacher teacher;
    private List<Question> tempQuestions;

    public CreateQuizController(CreateQuizView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;
        this.tempQuestions = new ArrayList<>();

        // Attach Listeners
        this.view.addAddQuestionListener(e -> addQuestionToList());
        this.view.addSaveQuizListener(e -> saveFinalQuiz());
        this.view.addCancelListener(e -> close());

        // Updated Listener to call the new method
        this.view.addRemoveLastQuestionListener(e -> removeSelectedQuestion());
    }

    // --- NEW METHOD: REMOVE SPECIFIC QUESTION BY NUMBER ---
    private void removeSelectedQuestion() {
        // 1. Check if there is anything to remove
        if (tempQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No questions to remove.");
            return;
        }

        // 2. Ask user for the Question Number
        String input = JOptionPane.showInputDialog(view,
                "Enter the Question Number to delete (1 - " + tempQuestions.size() + "):",
                "Delete Question",
                JOptionPane.QUESTION_MESSAGE);

        // If user pressed Cancel, input is null
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int questionNum = Integer.parseInt(input.trim());

            // 3. Validate the number
            if (questionNum < 1 || questionNum > tempQuestions.size()) {
                JOptionPane.showMessageDialog(view,
                        "Invalid number. Please enter a number between 1 and " + tempQuestions.size() + ".",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Remove the item
            // Convert 1-based index (user input) to 0-based index (list)
            Question removed = tempQuestions.remove(questionNum - 1);

            // 5. Refresh the UI to re-number the remaining questions
            refreshPreview();

            JOptionPane.showMessageDialog(view, "Removed Question " + questionNum + ":\n" + removed.getQuestionText());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper to refresh the text area in the view (Renumbering happens here automatically)
    private void refreshPreview() {
        view.clearPreview();

        for (int i = 0; i < tempQuestions.size(); i++) {
            // Re-generate the list with new numbers (i+1)
            view.appendToPreview((i + 1) + ". " + tempQuestions.get(i).getQuestionText());
        }
    }

    private void addQuestionToList() {
        // 1. Validate Inputs
        if (view.getQuestionText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a question text.");
            return;
        }

        List<String> options = new ArrayList<>();
        boolean optionsComplete = true;
        for (int i = 0; i < 4; i++) {
            String opt = view.getOption(i);
            if (opt.isEmpty()) {
                optionsComplete = false;
                break;
            }
            options.add(opt);
        }

        if (!optionsComplete) {
            JOptionPane.showMessageDialog(view, "Please fill in all 4 options.");
            return;
        }

        // 2. Create Question Object
        int correctIndex = view.getSelectedCorrectIndex();
        Question q = new Question(view.getQuestionText(), options, correctIndex);

        // 3. Add to Temp List
        tempQuestions.add(q);

        // 4. Update UI
        refreshPreview();
        view.clearQuestionInputs();
    }

    private void saveFinalQuiz() {
        String title = view.getQuizTitle();

        try {
            if (title.isEmpty()) {
                throw new UserInputErrorException("Please enter a Quiz Title.");
            }
            if (tempQuestions.isEmpty()) {
                throw new UserInputErrorException("Please add at least one question.");
            }

            // Check for Duplicate Quiz Name
            QuizFileManager manager = new QuizFileManager("quizzes.txt");
            List<Quiz> existingQuizzes = manager.load();

            for (Quiz q : existingQuizzes) {
                if (q.getQuizName().equalsIgnoreCase(title)) {
                    throw new DuplicateQuizException("A quiz named '" + title + "' already exists. Please rename.");
                }
            }

            // Create and Save
            Quiz newQuiz = new Quiz(title);
            for (Question q : tempQuestions) {
                newQuiz.addQuestion(q);
            }

            manager.save(newQuiz);

            JOptionPane.showMessageDialog(view, "Quiz Saved Successfully!");
            close();

        } catch (DuplicateQuizException | UserInputErrorException ex) {
            JOptionPane.showMessageDialog(view, "Validation Error: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error saving quiz: " + ex.getMessage());
        }
    }

    private void close() {
        view.dispose();
        TeacherDashboardView dashView = new TeacherDashboardView();
        new TeacherDashboardController(dashView, teacher);
        dashView.setVisible(true);
    }
}