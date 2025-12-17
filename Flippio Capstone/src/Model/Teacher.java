package Model;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private List<String> createdQuizzes; // List of Quiz IDs created by this teacher

    public Teacher(String idNumber, String password, String name) {
        super(idNumber, password, name);
        this.createdQuizzes = new ArrayList<>();
    }

    // Methods specific to Teacher
    public void addCreatedQuiz(String quizId) {
        this.createdQuizzes.add(quizId);
    }

    public List<String> getCreatedQuizzes() {
        return this.createdQuizzes;
    }
}
