package Model;

import java.util.List;

public class Question {
    private String questionText;
    private List<String> choices;
    private int correctAnswerIndex;

    public Question(String questionText, List<String> choices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    // Helper to get array for UI
    public String[] getOptionsArray() {
        return choices.toArray(new String[0]);
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}