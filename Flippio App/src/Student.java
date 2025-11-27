import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// edited the class from Person to User, Ligaray, 11272025

public class Student extends User {
    private List<StudentScore> scores;
    private Map<String, Integer> subjectScores;

    public Student(String id, String name) {
        super(id, name);
        this.scores = new ArrayList<>();
        this.subjectScores = new HashMap<>();
    }

    @Override
    public String action() {
        return "Student " + getName() + " is taking a quiz.";
    }

    public void takeQuiz(Quiz quiz) {
        try {
            quiz.startQuiz(this);
        } catch (EmptyAnswerException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void setSubjectScore(String subject, int score) {
        subjectScores.put(subject, score);
        scores.add(new StudentScore(subject, score));
    }

    public List<StudentScore> getScores() { return scores; }
}
