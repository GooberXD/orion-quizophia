package Model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    // Fields matching your Class Diagram
    private String quizName;
    private List<Question> questions;

    public Quiz(String quizName) {
        this.quizName = quizName;
        this.questions = new ArrayList<>();
    }

    // Methods
    public void addQuestion(Question q) {
        this.questions.add(q);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }
}
