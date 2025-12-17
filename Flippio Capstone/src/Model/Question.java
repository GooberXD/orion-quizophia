package Model;

import java.util.List;
import java.util.ArrayList;

public class Question {
    private String questionText;
    private List<String> choices;
    private int correctAnswerIndex; // Index of the correct answer

    public Question(String questionText, List<String> choices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.choices = choices == null ? new ArrayList<>() : new ArrayList<>(choices);
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return new ArrayList<>(choices);
    }

    // Helper method for the GUI: Converts the List<String> to a String[] array.
    public String[] getOptionsArray() {
        return choices.toArray(new String[0]);
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setChoices(List<String> newChoices) {
        if (newChoices == null || newChoices.size() != 4) {
            throw new IllegalArgumentException("Choices must contain exactly 4 options.");
        }
        // Ensure no nulls and trim options
        ArrayList<String> cleaned = new ArrayList<>(4);
        for (String c : newChoices) {
            if (c == null) throw new IllegalArgumentException("Choice cannot be null.");
            cleaned.add(c.trim());
        }
        this.choices = cleaned;
        // Clamp correct answer index if out of bounds
        if (correctAnswerIndex < 0 || correctAnswerIndex >= this.choices.size()) {
            correctAnswerIndex = 0;
        }
    }

    public void setCorrectAnswerIndex(int index) {
        if (index < 0 || index >= (choices == null ? 0 : choices.size())) {
            throw new IllegalArgumentException("Correct answer index out of bounds.");
        }
        this.correctAnswerIndex = index;
    }
}
