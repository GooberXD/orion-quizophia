import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// edited this from Person to User class, Ligaray, 11272025

public class Teacher extends User {
    private List<Subject> subjects;
    private String questionBankPath;

    public Teacher(String id, String name) {
        super(id, name);
        this.subjects = new ArrayList<>();
    }

    @Override
    public String action() {
        return "Teacher " + getName() + " is managing quizzes.";
    }

    public void addSubject(Subject subject) { subjects.add(subject); }
    public void addQuestion(Subject subject, Question question) { subject.addQuestion(question); }
    public void saveQuestions(Subject subject) { System.out.println("Saving questions to file..."); }
    public List<Subject> getSubjects() { return subjects; }
}
