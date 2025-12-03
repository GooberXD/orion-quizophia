package Model;

import java.util.List;
import java.util.ArrayList;

public class Question {
    // Fields matching your Class Diagram (Encapsulation)
    private String questionText;
    private List<String> choices;
    private int correctAnswerIndex; // Index of the correct answer (e.g., 0 for A, 1 for B)

    // Constructor
    public Question(String questionText, List<String> choices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    // Getters
    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    // --- MODIFICATION START ---
    // Helper method for the GUI: Converts the List<String> to a String[] array.
    // This allows the Controller to easily update the JRadioButtons.
    public String[] getOptionsArray() {
        return choices.toArray(new String[0]);
    }
    // --- MODIFICATION END ---

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    // Optional: Setter if you plan to edit questions later
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
