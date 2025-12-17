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
    private Subject subject;
    private List<Question> tempQuestions;
    private boolean editMode = false;
    private String originalTitle = null;

    public CreateQuizController(CreateQuizView view, Teacher teacher) {
        this.view = view;
        this.teacher = teacher;
        this.tempQuestions = new ArrayList<>();

        this.view.addAddQuestionListener(e -> addOrUpdateQuestion());
        this.view.addSaveQuizListener(e -> saveFinalQuiz());
        this.view.addCancelListener(e -> close());
        this.view.addRemoveLastQuestionListener(e -> removeSelectedQuestion());

        this.view.addPropertyChangeListener("editQuestion", evt -> {
            if (evt.getNewValue() instanceof Integer) {
                int index = (Integer) evt.getNewValue();
                loadQuestionForEditing(index);
            }
        });
    }

    public CreateQuizController(CreateQuizView view, Teacher teacher, Subject subject) {
        this(view, teacher);
        this.subject = subject;
    }

    // Edit existing quiz by name
    public CreateQuizController(CreateQuizView view, Teacher teacher, Subject subject, String quizNameToEdit) {
        this(view, teacher, subject);
        this.editMode = true;
        this.originalTitle = quizNameToEdit;
        loadExistingQuiz(quizNameToEdit);
    }

    private void loadExistingQuiz(String quizName) {
        try {
            QuizFileManager qfm = new QuizFileManager("quizzes.txt");
            java.util.List<Quiz> all = qfm.load();
            for (Quiz q : all) {
                if (q.getQuizName().equalsIgnoreCase(quizName)) {
                    this.tempQuestions.clear();
                    this.tempQuestions.addAll(q.getQuestions());
                    this.view.setQuizTitle(q.getQuizName());
                    this.view.setQuizTitleEditable(true);
                    refreshPreview();
                    return;
                }
            }
            JOptionPane.showMessageDialog(view, "Quiz not found for editing: " + quizName);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Failed to load quiz: " + ex.getMessage());
        }
    }

    private void removeSelectedQuestion() {
        if (tempQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No questions to remove.");
            return;
        }

        String input = JOptionPane.showInputDialog(view,
                "Enter the Question Number to delete (1 - " + tempQuestions.size() + "):",
                "Delete Question",
                JOptionPane.QUESTION_MESSAGE);

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int questionNum = Integer.parseInt(input.trim());

            if (questionNum < 1 || questionNum > tempQuestions.size()) {
                JOptionPane.showMessageDialog(view,
                        "Invalid number. Please enter a number between 1 and " + tempQuestions.size() + ".",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Question removed = tempQuestions.remove(questionNum - 1);

            refreshPreview();

            JOptionPane.showMessageDialog(view, "Removed Question " + questionNum + ":\n" + removed.getQuestionText());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(view, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshPreview() {
        view.clearPreview();

        for (int i = 0; i < tempQuestions.size(); i++) {
            view.appendToPreview((i + 1) + ". " + tempQuestions.get(i).getQuestionText());
        }
    }

    private void addOrUpdateQuestion() {
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

        int correctIndex = view.getSelectedCorrectIndex();
        // Require that a correct answer is selected
        if (correctIndex < 0 || correctIndex >= 4) {
            JOptionPane.showMessageDialog(view, "Please choose a correct answer!");
            return;
        }
        Question q = new Question(view.getQuestionText(), options, correctIndex);

        if (view.isEditingQuestion()) {
            int editIndex = view.getEditingQuestionIndex();
            if (editIndex >= 0 && editIndex < tempQuestions.size()) {
                tempQuestions.set(editIndex, q);
                JOptionPane.showMessageDialog(view, "Question " + (editIndex + 1) + " updated successfully!");
            }
        } else {
            tempQuestions.add(q);
        }

        refreshPreview();
        view.clearQuestionInputs();
    }

    private void loadQuestionForEditing(int index) {
        if (index < 0 || index >= tempQuestions.size()) return;
        
        Question q = tempQuestions.get(index);
        String[] options = new String[q.getChoices().size()];
        options = q.getChoices().toArray(options);
        
        view.setQuestionData(q.getQuestionText(), options, q.getCorrectAnswerIndex());
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

            QuizFileManager manager = new QuizFileManager("quizzes.txt");

            if (!editMode) {
                List<Quiz> existingQuizzes = manager.load();
                for (Quiz q : existingQuizzes) {
                    if (q.getQuizName().equalsIgnoreCase(title)) {
                        // Only throw a duplicate error if quiz is in the same subject
                        if (subject != null && q.getSubjectId() != null && q.getSubjectId().equals(subject.getId())) {
                            throw new DuplicateQuizException("A quiz named '" + title + "' already exists in this subject. Please rename.");
                        } else if (subject == null && q.getSubjectId() == null) {
                            throw new DuplicateQuizException("A quiz named '" + title + "' already exists. Please rename.");
                        }
                    }
                }
                Quiz newQuiz = new Quiz(title, teacher != null ? teacher.getIdNumber() : null,
                        subject != null ? subject.getId() : null);
                for (Question q : tempQuestions) newQuiz.addQuestion(q);
                manager.save(newQuiz);
            } else {
                List<Quiz> existingQuizzes = manager.load();
                Quiz base = null;
                for (Quiz q : existingQuizzes) {
                    if (q.getQuizName().equalsIgnoreCase(originalTitle)) { base = q; break; }
                }
                if (base == null) throw new Exception("Original quiz not found.");
                String newTitle = view.getQuizTitle();
                Quiz updated = new Quiz(newTitle, base.getTeacherId(), base.getSubjectId());
                updated.setPublished(base.isPublished());
                for (Question q : tempQuestions) updated.addQuestion(q);
                if (!newTitle.equalsIgnoreCase(base.getQuizName())) {
                    int confirm = javax.swing.JOptionPane.showConfirmDialog(view,
                            "Renaming will also update existing score records. Proceed?",
                            "Confirm Rename",
                            javax.swing.JOptionPane.YES_NO_OPTION);
                    if (confirm != javax.swing.JOptionPane.YES_OPTION) throw new Exception("Rename cancelled.");
                    manager.renameQuiz(base.getQuizName(), newTitle);
                }
                manager.updateQuiz(updated);
            }

            JOptionPane.showMessageDialog(view, editMode ? "Quiz Updated Successfully!" : "Quiz Saved Successfully!");
            close();

        } catch (DuplicateQuizException | UserInputErrorException ex) {
            JOptionPane.showMessageDialog(view, "Validation Error: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Error saving quiz: " + ex.getMessage());
        }
    }

    private void close() {
        view.dispose();
        // Always recreate dashboard after closing editor to reflect changes; the previous dashboard was disposed in Modify flow
        TeacherDashboardView dashView = new TeacherDashboardView();
        if (subject != null) {
            new TeacherDashboardController(dashView, teacher, subject);
            dashView.setTitle("Flippio - " + subject.getName() + " Quizzes");
        } else {
            new TeacherDashboardController(dashView, teacher);
        }
        dashView.setVisible(true);
    }
}