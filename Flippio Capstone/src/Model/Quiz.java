package Model;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String quizName;
    private List<Question> questions;
    private String teacherId;
    private String subjectId;
    private boolean published;

    public Quiz(String quizName) {
        this.quizName = quizName;
        this.questions = new ArrayList<>();
        this.published = false;
    }

    public Quiz(String quizName, String teacherId, String subjectId) {
        this(quizName);
        this.teacherId = teacherId;
        this.subjectId = subjectId;
    }

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

    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
