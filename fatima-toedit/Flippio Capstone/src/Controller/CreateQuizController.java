package Controller;

import Model.Question;
import Model.Teacher;
import View.CreateQuizView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizController {
    private static Teacher teacher;
    private CreateQuizView view;
    private List<Question> tempQuestions;

    public CreateQuizController(CreateQuizView view, Teacher teacher) {
        this.view = view;
        this.tempQuestions = new ArrayList<>();

        this.view.addAddQuestionListener(e -> addQuestion());
        this.view.addRemoveQuestionListener(e -> removeQuestion());
        this.view.addSaveQuizListener(e -> saveQuiz());
        this.view.addCancelListener(e -> view.dispose());
    }

    private void addQuestion() {
        if (view.getQuestionText().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Enter question text."); return;
        }

        List<String> options = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (view.getOption(i).isEmpty()) {
                JOptionPane.showMessageDialog(view, "Fill all options."); return;
            }
            options.add(view.getOption(i));
        }

        int correctIndex = view.getSelectedCorrectIndex();
        if (correctIndex == -1) {
            JOptionPane.showMessageDialog(view, "Click an option field to select it as the correct answer."); return;
        }

        Question q = new Question(view.getQuestionText(), options, correctIndex);
        tempQuestions.add(q);

        view.addQuestionToPreview("Q" + tempQuestions.size() + ": " + q.getQuestionText());
        view.clearQuestionInputs();
    }

    private void removeQuestion() {
        int index = view.getSelectedListIndex();
        if (index != -1 && index < tempQuestions.size()) {
            tempQuestions.remove(index);
            view.removeQuestionFromPreview(index);
        }
    }

    private void saveQuiz() {
        if (view.getQuizTitle().isEmpty() || tempQuestions.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Title required and at least 1 question."); return;
        }
        JOptionPane.showMessageDialog(view, "Quiz Saved Successfully!");
        view.dispose();
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored){}
        SwingUtilities.invokeLater(() -> {
            CreateQuizView v = new CreateQuizView();
            new CreateQuizController(v, teacher);
            v.setVisible(true);
        });
    }
}